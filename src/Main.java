import java.util.Scanner;

/**
 * Main.java
 *
 * Entry point for the Student Management System (CLI-Based).
 * Manages the interactive terminal menu, user input validation,
 * and delegates business operations to StudentManager.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager manager = new StudentManager();
        boolean running = true;

        while (running) {
            displayMenu();
            System.out.print("Enter your choice: ");
            String choiceInput = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number between 1 and 6.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    handleAddStudent(scanner, manager);
                    break;
                case 2:
                    handleViewAllStudents(manager);
                    break;
                case 3:
                    handleSearchStudent(scanner, manager);
                    break;
                case 4:
                    handleUpdateStudent(scanner, manager);
                    break;
                case 5:
                    handleDeleteStudent(scanner, manager);
                    break;
                case 6:
                    System.out.println("\nThank you for using Student Management System.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose an option from 1 to 6.\n");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Prints the primary application menu.
     */
    private static void displayMenu() {
        System.out.println("\n=====================================");
        System.out.println("     STUDENT MANAGEMENT SYSTEM");
        System.out.println("=====================================");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.println("=====================================");
    }

    /**
     * Handles adding a new student record.
     */
    private static void handleAddStudent(Scanner scanner, StudentManager manager) {
        System.out.println("\n--- Add Student ---");
        int studentId = readPositiveInt(scanner, "Enter Student ID: ");

        // Check if student ID already exists before prompting for more fields
        if (manager.findStudentById(studentId) != null) {
            System.out.println("Student with ID " + studentId + " already exists.");
            return;
        }

        String name = readNonEmptyString(scanner, "Enter Name: ");
        int age = readAge(scanner, "Enter Age (5-100): ");
        String course = readNonEmptyString(scanner, "Enter Course: ");
        String email = readEmail(scanner, "Enter Email: ");

        Student newStudent = new Student(studentId, name, age, course, email);
        manager.addStudent(newStudent);
    }

    /**
     * Handles viewing all student records.
     */
    private static void handleViewAllStudents(StudentManager manager) {
        manager.viewAllStudents();
    }

    /**
     * Handles searching for a student by ID.
     */
    private static void handleSearchStudent(Scanner scanner, StudentManager manager) {
        System.out.println("\n--- Search Student ---");
        int studentId = readPositiveInt(scanner, "Enter Student ID to search: ");
        manager.searchStudent(studentId);
    }

    /**
     * Handles updating an existing student record.
     */
    private static void handleUpdateStudent(Scanner scanner, StudentManager manager) {
        System.out.println("\n--- Update Student ---");
        int studentId = readPositiveInt(scanner, "Enter Student ID to update: ");

        Student existing = manager.findStudentById(studentId);
        if (existing == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("\nExisting Student Details:");
        existing.displayStudent();

        System.out.println("\nEnter new details (Student ID remains unchanged):");
        String newName = readNonEmptyString(scanner, "Enter New Name: ");
        int newAge = readAge(scanner, "Enter New Age (5-100): ");
        String newCourse = readNonEmptyString(scanner, "Enter New Course: ");
        String newEmail = readEmail(scanner, "Enter New Email: ");

        manager.updateStudent(studentId, newName, newAge, newCourse, newEmail);
    }

    /**
     * Handles deleting a student record.
     */
    private static void handleDeleteStudent(Scanner scanner, StudentManager manager) {
        System.out.println("\n--- Delete Student ---");
        int studentId = readPositiveInt(scanner, "Enter Student ID to delete: ");
        manager.deleteStudent(studentId);
    }

    // ==========================================
    // Robust CLI Input Helper Methods
    // ==========================================

    /**
     * Prompts until user enters a positive integer (greater than 0).
     */
    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
                System.out.println("Error: Value must be a positive integer greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input. Please enter a valid whole number.");
            }
        }
    }

    /**
     * Prompts until user enters an age in the range 5 to 100.
     */
    private static int readAge(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int age = Integer.parseInt(input);
                if (age >= 5 && age <= 100) {
                    return age;
                }
                System.out.println("Error: Age must be between 5 and 100.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number for age.");
            }
        }
    }

    /**
     * Prompts until user enters a non-empty string.
     */
    private static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: Field cannot be empty. Please enter a value.");
        }
    }

    /**
     * Prompts until user enters a basic valid email containing '@' and '.'.
     */
    private static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty() && input.contains("@") && input.contains(".")) {
                return input;
            }
            System.out.println("Error: Invalid email format. Email must not be empty and must contain '@' and '.'.");
        }
    }
}
