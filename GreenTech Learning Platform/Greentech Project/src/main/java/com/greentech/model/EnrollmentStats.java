package com.greentech.model;

/**
 * Analytical DTO representing course enrollment and completion metrics for graphical visualization.
 */
public class EnrollmentStats {
    private int courseId;
    private String courseTitle;
    private int enrolledCount;
    private int completedCount;
    private double completionRate;

    public EnrollmentStats(int courseId, String courseTitle, int enrolledCount, int completedCount) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.enrolledCount = enrolledCount;
        this.completedCount = completedCount;
        this.completionRate = (enrolledCount > 0) ? ((double) completedCount / enrolledCount) * 100.0 : 0.0;
    }

    public int getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public int getEnrolledCount() { return enrolledCount; }
    public int getCompletedCount() { return completedCount; }
    public double getCompletionRate() { return completionRate; }
    public int getFormattedRate() { return (int) Math.round(completionRate); }
}
