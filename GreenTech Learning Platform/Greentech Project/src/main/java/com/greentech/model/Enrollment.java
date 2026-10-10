package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a learner's enrollment in a course.
 */
public class Enrollment extends BaseEntity {
    private int learnerId;
    private String learnerName;
    private int courseId;
    private String courseTitle;
    private int progress; // 0 to 100%
    private String status; // ACTIVE, COMPLETED
    private Timestamp enrolledAt;
    private Timestamp completedAt;

    public Enrollment() {
        super();
    }

    public Enrollment(int id, int learnerId, String learnerName, int courseId, String courseTitle, int progress, String status, Timestamp enrolledAt, Timestamp completedAt) {
        super(id, enrolledAt);
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.progress = progress;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.completedAt = completedAt;
    }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(Timestamp enrolledAt) { this.enrolledAt = enrolledAt; }

    public Timestamp getCompletedAt() { return completedAt; }
    public void setCompletedAt(Timestamp completedAt) { this.completedAt = completedAt; }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status) || progress >= 100;
    }
}
