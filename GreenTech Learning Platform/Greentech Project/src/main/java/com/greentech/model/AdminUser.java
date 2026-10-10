package com.greentech.model;

/**
 * Specialized model representing an Administrator.
 * Demonstrates Inheritance and OOP Specialization.
 */
public class AdminUser extends User {
    public AdminUser() {
        super();
        this.role = "ADMIN";
    }

    public AdminUser(int id, String name, String email) {
        super(id, name, email, "ADMIN");
    }

    @Override
    public boolean canCreateCourse() {
        return true;
    }

    @Override
    public boolean canGradeAssignments() {
        return true;
    }

    @Override
    public boolean canEnrollInCourses() {
        return false;
    }

    @Override
    public String getDashboardUrl() {
        return "admin";
    }
}
