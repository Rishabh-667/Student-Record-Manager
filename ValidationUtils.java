package com.rishabh.student;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtils {
    public static List<String> validateStudent(Student s) {
        ArrayList<String> errs = new ArrayList<>();

        if (isBlank(s.getRollNo()) || !s.getRollNo().matches("[A-Za-z0-9-]{3,20}")) {
            errs.add("Invalid Roll No (3-20 alphanumeric/hyphen).");
        }
        if (isBlank(s.getFullName()) || s.getFullName().length() < 2) {
            errs.add("Name must be at least 2 characters.");
        }
        if (isBlank(s.getEmail()) || !s.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errs.add("Invalid email format.");
        }
        if (isBlank(s.getPhone()) || !s.getPhone().matches("^[0-9]{7,15}$")) {
            errs.add("Phone must be 7-15 digits.");
        }
        if (isBlank(s.getDepartment())) {
            errs.add("Department is required.");
        }
        if (s.getYear() < 1 || s.getYear() > 10) {
            errs.add("Year must be between 1 and 10.");
        }
        if (s.getCgpa() < 0.0 || s.getCgpa() > 10.0) {
            errs.add("CGPA must be between 0 and 10.");
        }

        return errs;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
