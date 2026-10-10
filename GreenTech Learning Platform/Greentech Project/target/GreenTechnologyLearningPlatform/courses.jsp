<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.greentech.model.Course" %>
<%@ page import="com.greentech.model.User" %>
<%
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    String error = (String) request.getAttribute("error");
    User currentUser = (User) request.getAttribute("currentUser");
    Set<Integer> enrolledCourseIds = (Set<Integer>) request.getAttribute("enrolledCourseIds");

    String selectedCategory = (String) request.getAttribute("selectedCategory");
    if (selectedCategory == null) selectedCategory = "All";

    String searchQuery = (String) request.getAttribute("searchQuery");
    if (searchQuery == null) searchQuery = "";

    String enrolledParam = request.getParameter("enrolled");
    String errParam = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Green Technology Course Catalog | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn <span>Courses</span></a>
    <nav>
        <a href="index.html">Home</a>
        <a href="courses" style="color:var(--primary); font-weight:700;">Courses</a>
        <a href="community">Community</a>
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
            <span class="eyebrow">ACADEMIC CATALOG</span>
            <h1>Explore Green Technology Courses</h1>
            <p>Industry-aligned sustainability modules designed by leading clean tech researchers and instructors.</p>
        </div>
        <% if (currentUser != null && currentUser.canCreateCourse()) { %>
            <div>
                <a class="btn small" href="dashboard">+ Submit Course</a>
            </div>
        <% } %>
    </div>

    <% if ("true".equals(enrolledParam)) { %>
        <div class="alert success">
            <span>✓</span> Success! You are now enrolled. Access syllabus and assignments in your <a href="dashboard" style="color:#ffffff; text-decoration:underline;">Dashboard</a>.
        </div>
    <% } %>

    <% if ("role".equals(errParam)) { %>
        <div class="alert error">
            <span>✗</span> Only Learner accounts can enroll in courses. You are currently authenticated as an <%= currentUser != null ? currentUser.getRole() : "User" %>.
        </div>
    <% } %>

    <% if (error != null) { %>
        <div class="alert error">
            <span>✗</span> <%= error %>
        </div>
    <% } %>

    <!-- Search & Category Filters -->
    <div class="card" style="margin-bottom:28px;">
        <form method="get" action="courses" style="display:flex; gap:12px; flex-wrap:wrap; align-items:center;">
            <div style="flex:1; min-width:240px;">
                <input type="text" name="search" value="<%= searchQuery %>" placeholder="🔍 Search courses by keyword or topic..." style="margin:0;">
            </div>
            <div style="min-width:180px;">
                <select name="category" style="margin:0;" onchange="this.form.submit()">
                    <option value="All" <%= "All".equalsIgnoreCase(selectedCategory) ? "selected" : "" %>>All Categories</option>
                    <option value="Renewable Energy" <%= "Renewable Energy".equalsIgnoreCase(selectedCategory) ? "selected" : "" %>>Renewable Energy</option>
                    <option value="Waste Management" <%= "Waste Management".equalsIgnoreCase(selectedCategory) ? "selected" : "" %>>Waste Management</option>
                    <option value="Sustainable Architecture" <%= "Sustainable Architecture".equalsIgnoreCase(selectedCategory) ? "selected" : "" %>>Sustainable Architecture</option>
                    <option value="Clean Mobility" <%= "Clean Mobility".equalsIgnoreCase(selectedCategory) ? "selected" : "" %>>Clean Mobility</option>
                </select>
            </div>
            <button class="btn" type="submit">Filter Catalog</button>
            <% if (!"All".equalsIgnoreCase(selectedCategory) || !searchQuery.isEmpty()) { %>
                <a class="btn outline" href="courses">Clear Filters</a>
            <% } %>
        </form>
    </div>

    <!-- Courses Grid -->
    <div class="grid three">
        <% if (courses != null && !courses.isEmpty()) {
            for (Course c : courses) {
                boolean isEnrolled = (enrolledCourseIds != null && enrolledCourseIds.contains(c.getId()));
        %>
            <article class="card" style="display:flex; flex-direction:column; justify-content:space-between;">
                <div>
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                        <span class="tag" style="background:rgba(16,185,129,0.15); color:var(--accent);">
                            <%= c.getCategory() %>
                        </span>
                        <span class="tag approved">APPROVED</span>
                    </div>
                    <h3 style="margin-top:0;"><%= c.getTitle() %></h3>
                    <p style="font-size:0.92rem;"><%= c.getDescription() %></p>
                    <small style="display:block; margin:8px 0; color:var(--text-muted);">
                        Instructor: <strong><%= c.getInstructor() %></strong>
                    </small>
                    <% if (c.getContent() != null && !c.getContent().isEmpty()) { %>
                        <div style="background:var(--bg-secondary); padding:8px 12px; border-radius:var(--radius-sm); border:1px solid var(--border); font-size:0.82rem; margin-top:8px; max-height:80px; overflow-y:auto; color:var(--text-muted);">
                            <strong>Curriculum:</strong><br>
                            <%= c.getContent() %>
                        </div>
                    <% } %>
                </div>

                <div style="margin-top:20px; padding-top:14px; border-top:1px solid var(--border);">
                    <% if (currentUser == null) { %>
                        <a class="btn small full outline" href="login.jsp?error=auth">Sign in to Enroll</a>
                    <% } else if (currentUser.isLearner()) { %>
                        <% if (isEnrolled) { %>
                            <button class="btn small full" style="background:var(--success-bg); color:var(--success) !important; border-color:var(--success); cursor:default;" disabled>✓ Enrolled in Dashboard</button>
                        <% } else { %>
                            <form method="post" action="enroll" style="margin:0;">
                                <input type="hidden" name="courseId" value="<%= c.getId() %>">
                                <button class="btn small full" type="submit">Enroll Now (Free)</button>
                            </form>
                        <% } %>
                    <% } else { %>
                        <div style="text-align:center; font-size:0.85rem; color:var(--text-muted);">
                            Viewing as <%= currentUser.getRole() %>
                        </div>
                    <% } %>
                </div>
            </article>
        <%  }
        } else { %>
            <div class="card" style="grid-column: 1 / -1; text-align:center; padding:50px;">
                <h3>No Matching Courses Found</h3>
                <p>Try modifying your category filter or search keywords, or submit a new course from your instructor dashboard.</p>
                <a class="btn small" href="courses" style="margin-top:10px;">Reset Catalog Filters</a>
            </div>
        <% } %>
    </div>
</main>

<footer>
    © 2026 GreenLearn Platform — Comprehensive Green Technology Academic System
</footer>
</body>
</html>
