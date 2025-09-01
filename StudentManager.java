package com.rishabh.student;

import java.util.*;
import java.io.*;

public class StudentManager {
    private final Map<String, Student> byRoll = new HashMap<>();
    private final CsvStorage storage;

    public StudentManager(CsvStorage storage) {
        this.storage = storage;
    }

    // Load from default storage
    public void load() {
        List<Student> loaded = storage.load();
        byRoll.clear();
        for (Student s : loaded) {
            byRoll.put(s.getRollNo().toLowerCase(), s);
        }
    }

    public boolean addStudent(Student s) {
        String key = s.getRollNo().toLowerCase();
        if (byRoll.containsKey(key)) return false;
        byRoll.put(key, s);
        save();
        return true;
    }

    public List<Student> getAll() {
        return new ArrayList<>(byRoll.values());
    }

    public Student findByRoll(String roll) {
        if (roll == null) return null;
        return byRoll.get(roll.toLowerCase());
    }

    public List<Student> searchByName(String name) {
        List<Student> out = new ArrayList<>();
        for (Student s : byRoll.values()) {
            if (s.getFullName() != null && s.getFullName().toLowerCase().contains(name)) {
                out.add(s);
            }
        }
        return out;
    }

    public List<Student> searchByDepartment(String dept) {
        List<Student> out = new ArrayList<>();
        for (Student s : byRoll.values()) {
            if (s.getDepartment() != null && s.getDepartment().toLowerCase().contains(dept)) {
                out.add(s);
            }
        }
        return out;
    }

    public boolean deleteByRoll(String roll) {
        if (roll == null) return false;
        Student removed = byRoll.remove(roll.toLowerCase());
        if (removed != null) {
            save();
            return true;
        }
        return false;
    }

    public void save() {
        storage.save(new ArrayList<>(byRoll.values()));
    }

    public boolean exportTo(String path) {
        try {
            new CsvStorage(path).save(new ArrayList<>(byRoll.values()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean importFrom(String path) {
        try {
            List<Student> loaded = new CsvStorage(path).load();
            for (Student s : loaded) {
                byRoll.put(s.getRollNo().toLowerCase(), s);
            }
            save();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
