package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a Green Technology course.
 */
public class Course extends BaseEntity {
    private String title;
    private String category;
    private String description;
    private String content;
    private int instructorId;
    private String instructor; // Instructor's display name
    private String status; // PENDING, APPROVED, REJECTED
    private Timestamp updatedAt;

    public Course() {
        super();
    }

    public Course(int id, String title, String description, String instructor, String status) {
        super(id, null);
        this.title = title;
        this.category = "Renewable Energy";
        this.description = description;
        this.instructor = instructor;
        this.status = status;
    }

    public Course(int id, String title, String category, String description, String content, int instructorId, String instructor, String status, Timestamp createdAt, Timestamp updatedAt) {
        super(id, createdAt);
        this.title = title;
        this.category = category != null ? category : "Renewable Energy";
        this.description = description;
        this.content = content;
        this.instructorId = instructorId;
        this.instructor = instructor;
        this.status = status != null ? status : "PENDING";
        this.updatedAt = updatedAt;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }
}
