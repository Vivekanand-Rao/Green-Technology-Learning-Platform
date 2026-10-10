package com.greentech.model;

import java.sql.Timestamp;

/**
 * Domain entity representing a community discussion topic or reply.
 */
public class Discussion extends BaseEntity {
    private Integer courseId;
    private String courseTitle;
    private int userId;
    private String userName;
    private String userRole;
    private String title;
    private String message;
    private Integer parentId;
    private int replyCount;

    public Discussion() {
        super();
    }

    public Discussion(int id, Integer courseId, String courseTitle, int userId, String userName, String userRole,
                      String title, String message, Integer parentId, Timestamp createdAt, int replyCount) {
        super(id, createdAt);
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.title = title;
        this.message = message;
        this.parentId = parentId;
        this.replyCount = replyCount;
    }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public boolean isReply() {
        return parentId != null && parentId > 0;
    }
}
