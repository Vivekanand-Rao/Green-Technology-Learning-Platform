package com.greentech.model;

/**
 * Specialized model representing a Learner.
 */
public class LearnerUser extends User {
    public LearnerUser() {
        super();
        this.role = "LEARNER";
    }

    public LearnerUser(int id, String name, String email) {
        super(id, name, email, "LEARNER");
    }

    @Override
    public boolean canCreateCourse() {
        return false;
    }

    @Override
    public boolean canGradeAssignments() {
        return false;
    }

    @Override
    public boolean canEnrollInCourses() {
        return true;
    }
}
