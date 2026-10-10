package com.greentech.model;

/**
 * Specialized model representing an Instructor.
 */
public class InstructorUser extends User {
    public InstructorUser() {
        super();
        this.role = "INSTRUCTOR";
    }

    public InstructorUser(int id, String name, String email) {
        super(id, name, email, "INSTRUCTOR");
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
}
