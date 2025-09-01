package com.rishabh.student;

import java.util.*;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final String DATA_DIR = "data";
    private static final String DEFAULT_CSV = DATA_DIR + "/students.csv";

    public static void main(String[] args) {
        // Ensure data dir
        java.io.File dir = new java.io.File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        CsvStorage storage = new CsvStorage(DEFAULT_CSV);
        StudentManager manager = new StudentManager(storage);
        manager.load(); // load existing CSV (if any)

        while (true) {
            printMenu();
            String choice = prompt("Choose an option: ");
            switch (choice) {
                case "1" -> addStudent(manager);
                case "2" -> listStudents(manager);
                case "3" -> searchStudents(manager);
                case "4" -> updateStudent(manager);
                case "5" -> deleteStudent(manager);
                case "6" -> sortStudents(manager);
                case "7" -> exportCsv(manager);
                case "8" -> importCsv(manager);
                case "9" -> about();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("==========================================");
        System.out.println("      Student Record Management System     ");
        System.out.println("==========================================");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Search Students");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Sort Students");
        System.out.println("7. Export to CSV");
        System.out.println("8. Import from CSV");
        System.out.println("9. About");
        System.out.println("0. Exit");
        System.out.println("==========================================");
    }

    private static String prompt(String label) {
        System.out.print(label);
        return sc.nextLine().trim();
    }

    private static void addStudent(StudentManager manager) {
        System.out.println("Add New Student");
        String roll = prompt("Roll No (e.g., 23BCS123): ");
        String name = prompt("Full Name: ");
        String email = prompt("Email: ");
        String phone = prompt("Phone: ");
        String dept = prompt("Department (e.g., CSE, IT, AI/ML): ");
        int year = readInt("Year (1-4): ", 1, 10);
        double cgpa = readDouble("CGPA (0-10): ", 0, 10);

        Student s = new Student(roll, name, email, phone, dept, year, cgpa);
        List<String> errs = ValidationUtils.validateStudent(s);
        if (!errs.isEmpty()) {
            System.out.println("Error(s):");
            errs.forEach(e -> System.out.println(" - " + e));
            return;
        }
        if (manager.addStudent(s)) {
            System.out.println("Student added and saved.");
        } else {
            System.out.println("Roll number already exists. Not added.");
        }
    }

    private static void listStudents(StudentManager manager) {
        List<Student> all = manager.getAll();
        if (all.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        printTable(all);
    }

    private static void printTable(List<Student> list) {
        String fmt = "| %-10s | %-22s | %-24s | %-12s | %-8s | %-4s | %-4s |%n";
        System.out.format("+------------+------------------------+--------------------------+--------------+----------+------+------+\n");
        System.out.format("| Roll No    | Name                   | Email                    | Phone        | Dept     | Year | CGPA |\n");
        System.out.format("+------------+------------------------+--------------------------+--------------+----------+------+------+\n");
        for (Student s : list) {
            System.out.format(fmt,
                    truncate(s.getRollNo(), 10),
                    truncate(s.getFullName(), 22),
                    truncate(s.getEmail(), 24),
                    truncate(s.getPhone(), 12),
                    truncate(s.getDepartment(), 8),
                    s.getYear(),
                    String.format("%.2f", s.getCgpa())
            );
        }
        System.out.format("+------------+------------------------+--------------------------+--------------+----------+------+------+\n");
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static void searchStudents(StudentManager manager) {
        System.out.println("Search by: 1) Roll No  2) Name  3) Department");
        String opt = prompt("Choose: ");
        List<Student> result = new ArrayList<>();
        switch (opt) {
            case "1" -> {
                String key = prompt("Enter roll no: ").toLowerCase();
                Student s = manager.findByRoll(key);
                if (s != null) result.add(s);
            }
            case "2" -> {
                String key = prompt("Enter name (partial ok): ").toLowerCase();
                result = manager.searchByName(key);
            }
            case "3" -> {
                String key = prompt("Enter department: ").toLowerCase();
                result = manager.searchByDepartment(key);
            }
            default -> System.out.println("Invalid option.");
        }
        if (result.isEmpty()) System.out.println("No matching records.");
        else printTable(result);
    }

    private static void updateStudent(StudentManager manager) {
        String roll = prompt("Enter roll no to update: ");
        Student s = manager.findByRoll(roll);
        if (s == null) {
            System.out.println("No student with that roll no.");
            return;
        }
        System.out.println("Leave blank to keep existing value.");
        String name = prompt("Full Name [" + s.getFullName() + "]: ");
        String email = prompt("Email [" + s.getEmail() + "]: ");
        String phone = prompt("Phone [" + s.getPhone() + "]: ");
        String dept = prompt("Department [" + s.getDepartment() + "]: ");
        String yearStr = prompt("Year [" + s.getYear() + "]: ");
        String cgpaStr = prompt("CGPA [" + s.getCgpa() + "]: ");

        if (!name.isEmpty()) s.setFullName(name);
        if (!email.isEmpty()) s.setEmail(email);
        if (!phone.isEmpty()) s.setPhone(phone);
        if (!dept.isEmpty()) s.setDepartment(dept);
        if (!yearStr.isEmpty()) {
            try {
                int y = Integer.parseInt(yearStr);
                s.setYear(y);
            } catch (Exception ignored) {}
        }
        if (!cgpaStr.isEmpty()) {
            try {
                double c = Double.parseDouble(cgpaStr);
                s.setCgpa(c);
            } catch (Exception ignored) {}
        }

        List<String> errs = ValidationUtils.validateStudent(s);
        if (!errs.isEmpty()) {
            System.out.println("Error(s):");
            errs.forEach(e -> System.out.println(" - " + e));
            System.out.println("Update aborted.");
            return;
        }

        manager.save();
        System.out.println("Student updated and saved.");
    }

    private static void deleteStudent(StudentManager manager) {
        String roll = prompt("Enter roll no to delete: ");
        boolean ok = manager.deleteByRoll(roll);
        if (ok) System.out.println("Deleted and saved.");
        else System.out.println("Roll not found.");
    }

    private static void sortStudents(StudentManager manager) {
        System.out.println("Sort by: 1) Name  2) Roll No  3) CGPA  4) Department");
        String opt = prompt("Choose: ");
        List<Student> list = new ArrayList<>(manager.getAll());
        switch (opt) {
            case "1" -> list.sort(Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER));
            case "2" -> list.sort(Comparator.comparing(Student::getRollNo, String.CASE_INSENSITIVE_ORDER));
            case "3" -> list.sort(Comparator.comparingDouble(Student::getCgpa).reversed());
            case "4" -> list.sort(Comparator.comparing(Student::getDepartment, String.CASE_INSENSITIVE_ORDER));
            default -> {
                System.out.println("Invalid option.");
                return;
            }
        }
        printTable(list);
    }

    private static void exportCsv(StudentManager manager) {
        String path = prompt("Export path (default data/students.csv): ");
        if (path.isEmpty()) path = DEFAULT_CSV;
        boolean ok = manager.exportTo(path);
        if (ok) System.out.println("Exported to: " + path);
        else System.out.println("Export failed.");
    }

    private static void importCsv(StudentManager manager) {
        String path = prompt("Import from path: ");
        if (path.isEmpty()) {
            System.out.println("Path cannot be empty.");
            return;
        }
        boolean ok = manager.importFrom(path);
        if (ok) System.out.println("Imported and saved.");
        else System.out.println("Import failed. Check CSV format.");
    }

    private static void about() {
        System.out.println("Student Record Management System");
        System.out.println("Author: Rishabh Suri | Java Console App with CSV persistence");
        System.out.println("GitHub: create a repo and push the project (see README)");
    }

    private static int readInt(String label, int min, int max) {
        while (true) {
            String s = prompt(label);
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new RuntimeException();
                return v;
            } catch (Exception e) {
                System.out.println("Enter an integer between " + min + " and " + max + ".");
            }
        }
    }

    private static double readDouble(String label, double min, double max) {
        while (true) {
            String s = prompt(label);
            try {
                double v = Double.parseDouble(s);
                if (v < min || v > max) throw new RuntimeException();
                return v;
            } catch (Exception e) {
                System.out.println("Enter a number between " + min + " and " + max + ".");
            }
        }
    }
}
