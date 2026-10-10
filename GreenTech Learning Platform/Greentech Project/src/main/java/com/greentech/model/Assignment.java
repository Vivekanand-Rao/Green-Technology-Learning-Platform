package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing an assignment created by an instructor.
 */
public class Assignment extends BaseEntity {
    private int courseId;
    private String courseTitle;
    private String title;
    private String description;
    private int maxScore;
    private String dueDate;

    public Assignment() {
        super();
    }

    public Assignment(int id, int courseId, String courseTitle, String title, String description, int maxScore, String dueDate, Timestamp createdAt) {
        super(id, createdAt);
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.title = title;
        this.description = description;
        this.maxScore = maxScore;
        this.dueDate = dueDate;
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
