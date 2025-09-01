package com.rishabh.student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CsvStorage {
    private final String path;

    public CsvStorage(String path) {
        this.path = path;
    }

    public List<Student> load() {
        List<Student> out = new ArrayList<>();
        Path p = Paths.get(path);
        if (!Files.exists(p)) return out;
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Basic CSV split (commas inside quotes supported)
                List<String> parts = parseCsvLine(line);
                if (parts.size() < 7) continue;
                String roll = parts.get(0);
                String name = parts.get(1);
                String email = parts.get(2);
                String phone = parts.get(3);
                String dept = parts.get(4);
                int year = safeInt(parts.get(5), 1);
                double cgpa = safeDouble(parts.get(6), 0.0);
                out.add(new Student(roll, name, email, phone, dept, year, cgpa));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void save(List<Student> students) {
        Path p = Paths.get(path);
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException ignored) {}
        try (BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            for (Student s : students) {
                String line = toCsvRow(Arrays.asList(
                        s.getRollNo(), s.getFullName(), s.getEmail(), s.getPhone(),
                        s.getDepartment(), String.valueOf(s.getYear()), String.valueOf(s.getCgpa())
                ));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helpers

    private static String escape(String s) {
        if (s == null) return "";
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n");
        String out = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + out + "\"" : out;
    }

    private static String toCsvRow(List<String> cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(escape(cols.get(i)));
        }
        return sb.toString();
    }

    private static List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"'); // escaped quote
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        out.add(cur.toString());
        return out;
    }

    private static int safeInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
    private static double safeDouble(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return def; }
    }
}
