package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing an assignment submission by a learner.
 */
public class Submission extends BaseEntity {
    private int assignmentId;
    private String assignmentTitle;
    private int learnerId;
    private String learnerName;
    private String submissionText;
    private Integer score;
    private String feedback;
    private String status; // SUBMITTED, GRADED
    private Timestamp submittedAt;
    private Timestamp gradedAt;

    public Submission() {
        super();
    }

    public Submission(int id, int assignmentId, String assignmentTitle, int learnerId, String learnerName,
                      String submissionText, Integer score, String feedback, String status,
                      Timestamp submittedAt, Timestamp gradedAt) {
        super(id, submittedAt);
        this.assignmentId = assignmentId;
        this.assignmentTitle = assignmentTitle;
        this.learnerId = learnerId;
        this.learnerName = learnerName;
        this.submissionText = submissionText;
        this.score = score;
        this.feedback = feedback;
        this.status = status;
        this.submittedAt = submittedAt;
        this.gradedAt = gradedAt;
    }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }

    public String getAssignmentTitle() { return assignmentTitle; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public String getLearnerName() { return learnerName; }
    public void setLearnerName(String learnerName) { this.learnerName = learnerName; }

    public String getSubmissionText() { return submissionText; }
    public void setSubmissionText(String submissionText) { this.submissionText = submissionText; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public Timestamp getGradedAt() { return gradedAt; }
    public void setGradedAt(Timestamp gradedAt) { this.gradedAt = gradedAt; }

    public boolean isGraded() {
        return "GRADED".equalsIgnoreCase(status);
    }
}
