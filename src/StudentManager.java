import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * StudentManager.java
 *
 * Manages the in-memory collection of Student objects and coordinates
 * persistence with the text file (students.txt).
 *
 * Implements CRUD operations:
 * - Create: addStudent()
 * - Read: viewAllStudents(), searchStudentById()
 * - Update: updateStudent()
 * - Delete: deleteStudent()
 */
public class StudentManager {
    private static final String FILE_NAME = "students.txt";
    private static final String DELIMITER = "\\|";

    // In-memory collection of students
    private ArrayList<Student> students;

    public StudentManager() {
        this.students = new ArrayList<Student>();
        loadStudentsFromFile();
    }

    /**
     * Loads student records from students.txt into the ArrayList.
     * Gracefully handles:
     * - Missing file on first startup
     * - Corrupted or malformed lines without crashing
     */
    public void loadStudentsFromFile() {
        File file = new File(FILE_NAME);

        // If file does not exist, start with an empty list
        if (!file.exists()) {
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip blank lines
                if (line.isEmpty()) {
                    continue;
                }

                // Expecting 5 parts: studentId|name|age|course|email
                String[] parts = line.split(DELIMITER);
                if (parts.length != 5) {
                    System.out.println("[Notice] Skipping malformed record on line " + lineNumber + ": " + line);
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
                        System.out.println("[Notice] Skipping duplicate ID record on line " + lineNumber + ": ID " + studentId);
                        continue;
                    }

                    // Validate basic constraints
                    if (studentId <= 0 || name.isEmpty() || age < 5 || age > 100 || course.isEmpty() || !email.contains("@") || !email.contains(".")) {
                        System.out.println("[Notice] Skipping invalid data on line " + lineNumber + ": " + line);
                        continue;
                    }

                    students.add(new Student(studentId, name, age, course, email));
                } catch (NumberFormatException e) {
                    System.out.println("[Notice] Skipping corrupted numeric data on line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading " + FILE_NAME + ": " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Error closing file reader: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Saves the current ArrayList of students to students.txt.
     */
    public void saveStudentsToFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME));
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                writer.write(s.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving records to " + FILE_NAME + ": " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Error closing file writer: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Adds a new student record after validating uniqueness of student ID.
     */
    public boolean addStudent(Student student) {
        if (findStudentById(student.getStudentId()) != null) {
            System.out.println("Student with ID " + student.getStudentId() + " already exists.");
            return false;
        }

        students.add(student);
        saveStudentsToFile();
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
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return false;
        }

        student.setName(newName);
        student.setAge(newAge);
        student.setCourse(newCourse);
        student.setEmail(newEmail);

        saveStudentsToFile();
        System.out.println("Student updated successfully.");
        return true;
    }

    /**
     * Deletes a student by student ID.
     */
    public boolean deleteStudent(int studentId) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                students.remove(i);
                saveStudentsToFile();
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
}
