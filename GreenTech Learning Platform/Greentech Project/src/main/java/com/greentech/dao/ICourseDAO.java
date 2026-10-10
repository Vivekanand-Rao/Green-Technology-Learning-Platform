package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Course;
import com.greentech.model.Enrollment;
import com.greentech.model.EnrollmentStats;

import java.util.List;

/**
 * Contract for Course and Enrollment operations in GreenTech Learning Platform.
 */
public interface ICourseDAO extends IGenericDAO<Course, Integer> {
    List<Course> getApprovedCourses() throws DatabaseException;
    List<Course> getCoursesByStatus(String status) throws DatabaseException;
    List<Course> getCoursesByInstructor(int instructorId) throws DatabaseException;
    List<Course> getEnrolledCourses(int learnerId) throws DatabaseException;
    List<Enrollment> getEnrollmentsByLearner(int learnerId) throws DatabaseException;
    boolean isEnrolled(int learnerId, int courseId) throws DatabaseException;
    boolean enroll(int learnerId, int courseId) throws DatabaseException;
    boolean updateProgress(int learnerId, int courseId, int progress) throws DatabaseException;
    int getEnrollmentCount(int learnerId) throws DatabaseException;
    boolean createCourse(String title, String category, String description, String content, int instructorId) throws DatabaseException;
    boolean updateCourse(int id, String title, String category, String description, String content) throws DatabaseException;
    boolean updateStatus(int courseId, String status) throws DatabaseException;
    int getPendingCount() throws DatabaseException;
    List<EnrollmentStats> getCourseEnrollmentStats() throws DatabaseException;
}
