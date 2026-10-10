package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.Discussion;

import java.util.List;

/**
 * Contract for Community Discussion activities in the GreenTech Learning Platform.
 */
public interface IDiscussionDAO extends IGenericDAO<Discussion, Integer> {
    boolean createTopic(Integer courseId, int userId, String title, String message) throws DatabaseException;
    boolean createReply(int parentId, int userId, String message) throws DatabaseException;
    List<Discussion> getRecentTopics(int limit) throws DatabaseException;
    List<Discussion> getTopicsByCourse(Integer courseId) throws DatabaseException;
    List<Discussion> getReplies(int topicId) throws DatabaseException;
    int getTotalDiscussionCount() throws DatabaseException;
}
