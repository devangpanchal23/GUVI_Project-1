# Student Management System

A console-based Java application for adding, viewing, searching, updating, and deleting student records. It demonstrates object-oriented programming, the Java Collections Framework (`ArrayList<Student>`), input validation, and text-file persistence.

## Project structure

```text
student-management-system/
├── data/
│   └── students.txt       # Persistent pipe-delimited student records
├── src/
│   ├── Main.java          # CLI menu and input handling
│   ├── Student.java       # Encapsulated student model
│   └── StudentManager.java # Collection, CRUD operations, and persistence
├── setup_guide.txt
└── README.md
```

The application reads and writes `data/students.txt`. It locates the project data folder when launched from either the repository root or its `src/` directory, and creates the data directory on save if needed. Existing records use this format:

```text
studentId|name|age|course|email
```

Malformed rows are reported and retained when valid records are saved, so a CRUD operation does not silently discard data it could not parse.

## Compile and run

Run these commands from the repository root with JDK 8 or newer:

```sh
mkdir -p out
javac -d out src/*.java
java -cp out Main
```

The `out/` directory contains compiled classes and is ignored by Git. Student records remain in `data/students.txt` across application restarts.

If you are already in `src/`, you can also compile and launch there:

```sh
javac *.java
java Main
```
