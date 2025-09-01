package com.rishabh.student;

public class Student {
    private String rollNo;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private int year;
    private double cgpa;

    public Student(String rollNo, String fullName, String email, String phone,
                   String department, int year, double cgpa) {
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.year = year;
        this.cgpa = cgpa;
    }

    public String getRollNo() { return rollNo; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public int getYear() { return year; }
    public double getCgpa() { return cgpa; }

    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDepartment(String department) { this.department = department; }
    public void setYear(int year) { this.year = year; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
}
