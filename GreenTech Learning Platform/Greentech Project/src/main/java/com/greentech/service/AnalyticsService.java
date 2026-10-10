package com.greentech.service;

import com.greentech.dao.CourseDAO;
import com.greentech.dao.DiscussionDAO;
import com.greentech.dao.UserDAO;
import com.greentech.model.EnrollmentStats;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe analytical service demonstrating Multithreading, Concurrency, and Synchronization.
 * Aggregates platform metrics and enrollment telemetry across concurrent user sessions safely.
 */
public class AnalyticsService {
    private static volatile AnalyticsService instance;
    private static final Object LOCK = new Object();

    private final UserDAO userDAO = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final DiscussionDAO discussionDAO = new DiscussionDAO();

    // Thread-safe cached telemetry
    private final ConcurrentHashMap<String, Object> metricCache = new ConcurrentHashMap<>();
    private final AtomicInteger activeSessionCount = new AtomicInteger(0);
    private long lastCacheRefreshTime = 0;
    private static final long CACHE_TTL_MS = 15000; // 15 seconds cache

    private AnalyticsService() {}

    /**
     * Thread-safe double-checked locking singleton accessor.
     */
    public static AnalyticsService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AnalyticsService();
                }
            }
        }
        return instance;
    }

    public void incrementActiveSessions() {
        activeSessionCount.incrementAndGet();
    }

    public void decrementActiveSessions() {
        activeSessionCount.updateAndGet(c -> Math.max(0, c - 1));
    }

    public int getActiveSessionCount() {
        return activeSessionCount.get();
    }

    /**
     * Synchronized aggregate metrics refresh to prevent thundering herd under concurrent load.
     */
    public synchronized void refreshMetricsIfStale() {
        long now = System.currentTimeMillis();
        if (now - lastCacheRefreshTime > CACHE_TTL_MS) {
            try {
                int totalUsers = userDAO.getUserCount();
                int totalCourses = courseDAO.getAllCourses().size();
                int pendingApprovals = courseDAO.getPendingCount();
                int totalDiscussions = discussionDAO.getTotalDiscussionCount();
                List<EnrollmentStats> stats = courseDAO.getCourseEnrollmentStats();

                metricCache.put("totalUsers", totalUsers);
                metricCache.put("totalCourses", totalCourses);
                metricCache.put("pendingApprovals", pendingApprovals);
                metricCache.put("totalDiscussions", totalDiscussions);
                metricCache.put("enrollmentStats", stats);
                lastCacheRefreshTime = now;
            } catch (Exception e) {
                // Keep existing cached data if DB query has issues
            }
        }
    }

    public int getTotalUsers() {
        refreshMetricsIfStale();
        return (int) metricCache.getOrDefault("totalUsers", 0);
    }

    public int getTotalCourses() {
        refreshMetricsIfStale();
        return (int) metricCache.getOrDefault("totalCourses", 0);
    }

    public int getPendingApprovals() {
        refreshMetricsIfStale();
        return (int) metricCache.getOrDefault("pendingApprovals", 0);
    }

    public int getTotalDiscussions() {
        refreshMetricsIfStale();
        return (int) metricCache.getOrDefault("totalDiscussions", 0);
    }

    @SuppressWarnings("unchecked")
    public List<EnrollmentStats> getEnrollmentStats() {
        refreshMetricsIfStale();
        List<EnrollmentStats> list = (List<EnrollmentStats>) metricCache.get("enrollmentStats");
        return list != null ? list : Collections.emptyList();
    }
}
