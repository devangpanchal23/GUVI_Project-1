import java.io.IOException;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * StudentManager.java
 *
 * Manages the in-memory collection of Student objects and coordinates
 * persistence with the text file (data/students.txt).
 *
 * Implements CRUD operations:
 * - Create: addStudent()
 * - Read: viewAllStudents(), searchStudentById()
 * - Update: updateStudent()
 * - Delete: deleteStudent()
 */
public class StudentManager {
    private static final Path DATA_FILE = resolveDataFile();
    private static final String DELIMITER = "\\|";

    // In-memory collection of students
    private ArrayList<Student> students;
    // Keep rows we cannot load so a later CRUD save does not silently discard them.
    private ArrayList<String> preservedRows;

    /**
     * Resolve the project data file when launched from either the repository
     * root or its src directory. Prefer an existing file so old records win.
     */
    private static Path resolveDataFile() {
        Path workingDirectory = Paths.get("").toAbsolutePath().normalize();
        Path localDataFile = workingDirectory.resolve(Paths.get("data", "students.txt"));
        Path parent = workingDirectory.getParent();
        boolean launchedFromSourceDirectory = "src".equals(workingDirectory.getFileName().toString());
        if (launchedFromSourceDirectory && parent != null) {
            return parent.resolve(Paths.get("data", "students.txt"));
        }
        return localDataFile;
    }

    public StudentManager() {
        this.students = new ArrayList<Student>();
        this.preservedRows = new ArrayList<String>();
        loadStudentsFromFile();
    }

    /**
     * Loads student records from data/students.txt into the ArrayList.
     * Gracefully handles:
     * - Missing file on first startup
     * - Corrupted or malformed lines without crashing
     */
    public void loadStudentsFromFile() {
        students.clear();
        preservedRows.clear();

        // If file does not exist, start with an empty list
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try (java.io.BufferedReader reader = Files.newBufferedReader(DATA_FILE, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String originalLine = line;
                line = line.trim();

                // Skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                // Expecting 5 parts: studentId|name|age|course|email
                String[] parts = line.split(DELIMITER);
                if (parts.length != 5) {
                    System.out.println("[Notice] Skipping malformed record on line " + lineNumber + ": " + line);
                    preservedRows.add(originalLine);
                    continue;
                }

                try {
                    int studentId = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    String course = parts[3].trim();
                    String email = parts[4].trim();

                    // Check for duplicate ID already in the list
                    if (findStudentById(studentId) != null) {
                        System.out.println(
                                "[Notice] Skipping duplicate ID record on line " + lineNumber + ": ID " + studentId);
                        preservedRows.add(originalLine);
                        continue;
                    }

                    // Validate basic constraints
                    if (studentId <= 0 || name.isEmpty() || age < 5 || age > 100 || course.isEmpty()
                            || !email.contains("@") || !email.contains(".")) {
                        System.out.println("[Notice] Skipping invalid data on line " + lineNumber + ": " + line);
                        preservedRows.add(originalLine);
                        continue;
                    }

                    students.add(new Student(studentId, name, age, course, email));
                } catch (NumberFormatException e) {
                    System.out.println("[Notice] Skipping corrupted numeric data on line " + lineNumber + ": " + line);
                    preservedRows.add(originalLine);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading " + DATA_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Saves the current ArrayList of students to data/students.txt.
     */
    public boolean saveStudentsToFile() {
        Path temporaryFile = null;
        try {
            Path parent = DATA_FILE.getParent();
            Files.createDirectories(parent);
            temporaryFile = Files.createTempFile(parent, "students-", ".tmp");
            try (java.io.BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Student student : students) {
                    writer.write(student.toFileString());
                    writer.newLine();
                }
                for (String row : preservedRows) {
                    writer.write(row);
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, DATA_FILE, StandardCopyOption.REPLACE_EXISTING);
            }
            temporaryFile = null;
            return true;
        } catch (IOException e) {
            System.out.println("Error saving records to " + DATA_FILE + ": " + e.getMessage());
            return false;
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    System.out.println("Error removing temporary data file " + temporaryFile + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Adds a new student record after validating uniqueness of student ID.
     */
    public boolean addStudent(Student student) {
        if (!isValidStudent(student)) {
            System.out.println("Student details are invalid.");
            return false;
        }
        if (findStudentById(student.getStudentId()) != null) {
            System.out.println("Student with ID " + student.getStudentId() + " already exists.");
            return false;
        }

        students.add(student);
        if (!saveStudentsToFile()) {
            students.remove(students.size() - 1);
            return false;
        }
        System.out.println("Student added successfully.");
        return true;
    }

    /**
     * Displays all students in the collection.
     */
    public void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        System.out.println("\n========== ALL STUDENT RECORDS (" + students.size() + ") ==========");
        for (int i = 0; i < students.size(); i++) {
            students.get(i).displayStudent();
        }
    }

    /**
     * Searches for a student by student ID.
     */
    public Student searchStudent(int studentId) {
        Student found = findStudentById(studentId);
        if (found != null) {
            System.out.println("\nStudent record found:");
            found.displayStudent();
            return found;
        } else {
            System.out.println("Student not found.");
            return null;
        }
    }

    /**
     * Updates an existing student's details (name, age, course, email).
     * Student ID is preserved as the immutable unique key.
     */
    public boolean updateStudent(int studentId, String newName, int newAge, String newCourse, String newEmail) {
        if (newName == null || newName.trim().isEmpty() || newAge < 5 || newAge > 100
                || newCourse == null || newCourse.trim().isEmpty() || !isValidEmail(newEmail)) {
            System.out.println("Student details are invalid.");
            return false;
        }
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return false;
        }

        String oldName = student.getName();
        int oldAge = student.getAge();
        String oldCourse = student.getCourse();
        String oldEmail = student.getEmail();
        student.setName(newName);
        student.setAge(newAge);
        student.setCourse(newCourse);
        student.setEmail(newEmail);

        if (!saveStudentsToFile()) {
            student.setName(oldName);
            student.setAge(oldAge);
            student.setCourse(oldCourse);
            student.setEmail(oldEmail);
            return false;
        }
        System.out.println("Student updated successfully.");
        return true;
    }

    /**
     * Deletes a student by student ID.
     */
    public boolean deleteStudent(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                Student removed = students.remove(i);
                if (!saveStudentsToFile()) {
                    students.add(i, removed);
                    return false;
                }
                System.out.println("Student deleted successfully.");
                return true;
            }
        }

        System.out.println("Student not found.");
        return false;
    }

    /**
     * Helper method to search student by ID.
     * Returns matching Student object or null if not found.
     */
    public Student findStudentById(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            if (s.getStudentId() == studentId) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns total number of students currently loaded.
     */
    public int getStudentCount() {
        return students.size();
    }

    private boolean isValidStudent(Student student) {
        return student != null && student.getStudentId() > 0
                && student.getName() != null && !student.getName().trim().isEmpty()
                && student.getAge() >= 5 && student.getAge() <= 100
                && student.getCourse() != null && !student.getCourse().trim().isEmpty()
                && isValidEmail(student.getEmail());
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
