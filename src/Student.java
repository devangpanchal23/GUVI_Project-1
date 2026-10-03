/**
 * Student.java
 *
 * Represents an individual Student entity in the Student Management System.
 * Demonstrates Core Java OOP principles:
 * - Encapsulation (private fields, public getters and setters)
 * - Constructor initialization
 * - Standard object representation via toString() and formatted display
 */
public class Student {
    private int studentId;
    private String name;
    private int age;
    private String course;
    private String email;

    // Parameterized constructor to initialize student attributes
    public Student(int studentId, String name, int age, String course, String email) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.course = course;
        this.email = email;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Converts student record into delimiter-separated format for file persistence.
     * Format: studentId|name|age|course|email
     */
    public String toFileString() {
        return studentId + "|" + name + "|" + age + "|" + course + "|" + email;
    }

    /**
     * Displays formatted student card on console.
     */
    public void displayStudent() {
        System.out.println("-----------------------------------------");
        System.out.println("Student ID : " + studentId);
        System.out.println("Name       : " + name);
        System.out.println("Age        : " + age);
        System.out.println("Course     : " + course);
        System.out.println("Email      : " + email);
        System.out.println("-----------------------------------------");
    }

    @Override
    public String toString() {
        return "Student [ID=" + studentId + ", Name=" + name + ", Age=" + age +
               ", Course=" + course + ", Email=" + email + "]";
    }
}
