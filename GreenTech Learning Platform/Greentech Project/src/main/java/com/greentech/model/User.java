package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a system user with role-based attributes and polymorphic behaviors.
 */
public class User extends BaseEntity {
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String salt;
    protected String role; // ADMIN, INSTRUCTOR, LEARNER
    protected String status; // ACTIVE, INACTIVE

    public User() {
        super();
    }

    public User(int id, String name, String email, String role) {
        super(id, null);
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = "ACTIVE";
    }

    public User(int id, String name, String email, String passwordHash, String salt, String role, String status, Timestamp createdAt) {
        super(id, createdAt);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.status = status != null ? status : "ACTIVE";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Role-based helper methods demonstrating Polymorphism and Encapsulation
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isInstructor() {
        return "INSTRUCTOR".equalsIgnoreCase(role);
    }

    public boolean isLearner() {
        return "LEARNER".equalsIgnoreCase(role);
    }

    public boolean canCreateCourse() {
        return isInstructor() || isAdmin();
    }

    public boolean canGradeAssignments() {
        return isInstructor() || isAdmin();
    }

    public boolean canEnrollInCourses() {
        return isLearner();
    }

    public String getDashboardUrl() {
        if (isAdmin()) return "admin";
        return "dashboard";
    }

    /**
     * Factory method creating a specialized user subclass instance based on the role string.
     */
    public static User createSpecialized(int id, String name, String email, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AdminUser(id, name, email);
        } else if ("INSTRUCTOR".equalsIgnoreCase(role)) {
            return new InstructorUser(id, name, email);
        } else {
            return new LearnerUser(id, name, email);
        }
    }
}
