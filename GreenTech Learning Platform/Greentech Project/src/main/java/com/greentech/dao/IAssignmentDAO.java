package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Assignment;
import com.greentech.model.Submission;

import java.util.List;

/**
 * Contract for managing Course Assignments and Learner Submissions.
 */
public interface IAssignmentDAO extends IGenericDAO<Assignment, Integer> {
    boolean createAssignment(int courseId, String title, String description, int maxScore, String dueDate) throws DatabaseException;
    List<Assignment> getAssignmentsByCourse(int courseId) throws DatabaseException;
    boolean submitAssignment(int assignmentId, int learnerId, String submissionText) throws DatabaseException;
    boolean gradeSubmission(int submissionId, int score, String feedback) throws DatabaseException;
    List<Submission> getSubmissionsByInstructor(int instructorId) throws DatabaseException;
    List<Submission> getSubmissionsByLearner(int learnerId) throws DatabaseException;
    Submission getSubmissionById(int submissionId) throws DatabaseException;
}
