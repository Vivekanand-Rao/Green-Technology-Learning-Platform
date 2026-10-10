package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing an achievement certificate awarded upon course completion.
 */
public class Certificate extends BaseEntity {
    private int learnerId;
    private String learnerName;
    private int courseId;
    private String courseTitle;
    private String certificateCode;
    private Timestamp issueDate;

    public Certificate() {
        super();
    }

    public Certificate(int id, int learnerId, String learnerName, int courseId, String courseTitle, String certificateCode, Timestamp issueDate) {
        super(id, issueDate);
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.certificateCode = certificateCode;
        this.issueDate = issueDate;
    }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getCertificateCode() { return certificateCode; }
    public void setCertificateCode(String certificateCode) { this.certificateCode = certificateCode; }

    public Timestamp getIssueDate() { return issueDate; }
    public void setIssueDate(Timestamp issueDate) { this.issueDate = issueDate; }
}
