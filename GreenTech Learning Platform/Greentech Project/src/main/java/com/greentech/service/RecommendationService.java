package com.greentech.service;

import com.greentech.dao.CourseDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.model.Course;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service providing personalized course recommendations based on the learner's enrolled subject history.
 * Demonstrates Collections, Generics, and Java Streams API.
 */
public class RecommendationService {
    private final CourseDAO courseDAO = new CourseDAO();

    /**
     * Computes recommendations tailored to the learner's previous subject interests.
     */
    public List<Course> getRecommendationsForLearner(int learnerId) {
        try {
            List<Course> enrolled = courseDAO.getEnrolledCourses(learnerId);
            List<Course> allApproved = courseDAO.getApprovedCourses();

            Set<Integer> enrolledIds = enrolled.stream()
                    .map(Course::getId)
                    .collect(Collectors.toSet());

            // Extract preferred categories from enrolled courses
            Set<String> preferredCategories = enrolled.stream()
                    .map(Course::getCategory)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Filter out already enrolled courses
            List<Course> candidates = allApproved.stream()
                    .filter(c -> !enrolledIds.contains(c.getId()))
                    .collect(Collectors.toList());

            if (candidates.isEmpty()) {
                return Collections.emptyList();
            }

            // Prioritize courses matching preferred categories
            List<Course> recommended = new ArrayList<>();
            List<Course> otherCourses = new ArrayList<>();

            for (Course c : candidates) {
                if (preferredCategories.contains(c.getCategory())) {
                    recommended.add(c);
                } else {
                    otherCourses.add(c);
                }
            }

            // Append general popular courses if recommendations are few
            if (recommended.size() < 3) {
                recommended.addAll(otherCourses);
            }

            return recommended.stream().limit(4).collect(Collectors.toList());
        } catch (DatabaseException e) {
            return Collections.emptyList();
        }
    }
}
