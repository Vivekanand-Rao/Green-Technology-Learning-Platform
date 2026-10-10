<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.greentech.model.User" %>
<%@ page import="com.greentech.model.Discussion" %>
<%@ page import="java.util.List" %>
<%
    User currentUser = (User) session.getAttribute("user");
    List<Discussion> topics = (List<Discussion>) request.getAttribute("topics");
    if (topics == null) {
        response.sendRedirect("community");
        return;
    }
    Discussion activeTopic = (Discussion) request.getAttribute("activeTopic");
    List<Discussion> replies = (List<Discussion>) request.getAttribute("replies");

    String feedbackMsg = (String) session.getAttribute("feedbackMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    session.removeAttribute("feedbackMsg");
    session.removeAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Community Discussions | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn <span>Community</span></a>
    <nav>
        <a href="index.html">Home</a>
        <a href="courses">Courses</a>
        <a href="community" style="color:var(--primary); font-weight:700;">Community Forum</a>
        <% if (currentUser != null) { %>
            <a href="<%= currentUser.getDashboardUrl() %>">Dashboard (<%= currentUser.getName() %>)</a>
            <a class="btn small outline" href="logout">Logout</a>
        <% } else { %>
            <a href="login.jsp">Login</a>
            <a class="btn small" href="register.jsp">Register</a>
        <% } %>
        <a href="test-db">DB Status</a>
    </nav>
</header>

<main class="section">
    <div class="welcome">
        <div>
            <span class="eyebrow">GREEN INNOVATION FORUM</span>
            <h1>Community Discussion Forum</h1>
            <p>Exchange sustainability research, collaborate on clean energy projects, and seek guidance from instructors.</p>
        </div>
        <% if (currentUser != null) { %>
            <div>
                <a class="btn small" href="#new-topic">+ Start New Discussion</a>
            </div>
        <% } %>
    </div>

    <% if (feedbackMsg != null) { %>
        <div class="alert success">
            <span>✓</span> <%= feedbackMsg %>
        </div>
    <% } %>

    <% if (errorMsg != null) { %>
        <div class="alert error">
            <span>✗</span> <%= errorMsg %>
        </div>
    <% } %>

    <div class="grid two">
        <!-- Left Column: Discussion Topics Feed -->
        <div>
            <h2>Discussion Threads</h2>
            <div style="display:flex; flex-direction:column; gap:16px; margin-top:14px;">
                <% if (topics != null && !topics.isEmpty()) {
                    for (Discussion d : topics) {
                        boolean isSelected = (activeTopic != null && activeTopic.getId() == d.getId());
                %>
                    <div class="card" style="<%= isSelected ? "border-color:var(--primary); background:var(--surface-hover);" : "" %>">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                            <span class="tag" style="background:rgba(56,189,248,0.15); color:var(--info);">
                                <%= d.getCourseTitle() != null ? d.getCourseTitle() : "General Sustainability" %>
                            </span>
                            <span class="tag role-<%= d.getUserRole() != null ? d.getUserRole().toLowerCase() : "learner" %>">
                                <%= d.getUserRole() %>
                            </span>
                        </div>
                        <h3 style="margin:4px 0;"><%= d.getTitle() %></h3>
                        <p style="font-size:0.92rem;"><%= d.getMessage() %></p>
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:12px; font-size:0.85rem; color:var(--text-muted);">
                            <span>By: <strong><%= d.getUserName() %></strong> &bull; <%= d.getReplyCount() %> replies</span>
                            <a class="btn small outline" href="community?topicId=<%= d.getId() %>">View Discussion &rarr;</a>
                        </div>
                    </div>
                <%  }
                } else { %>
                    <div class="card" style="text-align:center; padding:30px;">
                        <p>No discussions initiated yet. Be the first to start a conversation!</p>
                    </div>
                <% } %>
            </div>
        </div>

        <!-- Right Column: Active Thread Details or New Topic Form -->
        <div>
            <% if (activeTopic != null) { %>
                <!-- Active Topic & Thread View -->
                <div class="card" style="border-color:var(--primary); margin-bottom:20px;">
                    <span class="eyebrow">ACTIVE CONVERSATION</span>
                    <h2><%= activeTopic.getTitle() %></h2>
                    <p style="color:#ffffff; font-size:1.02rem; margin:14px 0; background:var(--bg-secondary); padding:14px; border-radius:var(--radius-sm); border:1px solid var(--border);">
                        <%= activeTopic.getMessage() %>
                    </p>
                    <small>
                        Posted by: <strong><%= activeTopic.getUserName() %></strong> (<%= activeTopic.getUserRole() %>) &bull; 
                        <%= activeTopic.getCreatedAt() != null ? activeTopic.getCreatedAt().toString().substring(0, 16) : "Recent" %>
                    </small>

                    <!-- Replies List -->
                    <div style="margin-top:20px;">
                        <h4>Replies (<%= replies != null ? replies.size() : 0 %>)</h4>
                        <div style="display:flex; flex-direction:column; gap:12px; margin-top:10px;">
                            <% if (replies != null && !replies.isEmpty()) {
                                for (Discussion rep : replies) { %>
                                    <div style="background:var(--bg); padding:12px; border-radius:var(--radius-sm); border:1px solid var(--border);">
                                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
                                            <strong style="color:var(--primary); font-size:0.9rem;"><%= rep.getUserName() %></strong>
                                            <span class="tag role-<%= rep.getUserRole() != null ? rep.getUserRole().toLowerCase() : "learner" %>" style="font-size:10px;">
                                                <%= rep.getUserRole() %>
                                            </span>
                                        </div>
                                        <p style="font-size:0.9rem; margin-bottom:4px; color:var(--text);"><%= rep.getMessage() %></p>
                                        <small style="color:var(--text-dim);"><%= rep.getCreatedAt() != null ? rep.getCreatedAt().toString().substring(0, 16) : "" %></small>
                                    </div>
                            <%  }
                            } else { %>
                                <p style="color:var(--text-muted); font-size:0.9rem;">No replies in this thread yet. Write the first response below.</p>
                            <% } %>
                        </div>
                    </div>

                    <!-- Post Reply Form -->
                    <% if (currentUser != null) { %>
                        <form method="post" action="community" style="margin-top:20px;">
                            <input type="hidden" name="action" value="createReply">
                            <input type="hidden" name="parentId" value="<%= activeTopic.getId() %>">
                            <label>
                                Your Reply:
                                <textarea name="message" rows="3" placeholder="Share your perspective or solution..." required></textarea>
                            </label>
                            <button class="btn small full" type="submit">Post Reply</button>
                        </form>
                    <% } else { %>
                        <div style="margin-top:20px; text-align:center;">
                            <a class="btn small outline" href="login.jsp">Sign in to Reply</a>
                        </div>
                    <% } %>
                </div>
            <% } %>

            <!-- Start New Topic Card -->
            <div id="new-topic" class="card">
                <span class="eyebrow">CONTRIBUTE</span>
                <h2>Start a New Discussion</h2>
                <p>Post a question or initiate a topic for the green technology community.</p>

                <% if (currentUser != null) { %>
                    <form method="post" action="community" style="margin-top:14px;">
                        <input type="hidden" name="action" value="createTopic">
                        <label>
                            Topic Title
                            <input type="text" name="title" placeholder="e.g. Sizing Microgrid Battery Inverters" required>
                        </label>
                        <label>
                            Your Question or Discussion Body
                            <textarea name="message" rows="4" placeholder="Detail your thoughts or green technology question..." required></textarea>
                        </label>
                        <button class="btn full" type="submit">Publish Discussion Topic</button>
                    </form>
                <% } else { %>
                    <div style="text-align:center; padding:20px;">
                        <p>You must be signed in to post topics or participate in community discussions.</p>
                        <a class="btn" href="login.jsp">Sign in to Participate</a>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</main>

<footer>
    © 2026 GreenLearn Platform — Comprehensive Green Technology Academic System
</footer>
</body>
</html>
