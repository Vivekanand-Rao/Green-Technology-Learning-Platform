<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.greentech.model.User" %>
<%@ page import="com.greentech.model.Course" %>
<%@ page import="com.greentech.model.SystemSetting" %>
<%@ page import="com.greentech.model.EnrollmentStats" %>
<%@ page import="com.greentech.model.Discussion" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect("login.jsp?error=auth");
        return;
    }

    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) {
        response.sendRedirect("admin");
        return;
    }
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    List<SystemSetting> settings = (List<SystemSetting>) request.getAttribute("settings");
    List<EnrollmentStats> enrollmentStats = (List<EnrollmentStats>) request.getAttribute("enrollmentStats");
    List<Discussion> discussions = (List<Discussion>) request.getAttribute("discussions");

    String adminSuccess = (String) session.getAttribute("adminSuccess");
    String adminError = (String) session.getAttribute("adminError");
    session.removeAttribute("adminSuccess");
    session.removeAttribute("adminError");

    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    Integer totalCourses = (Integer) request.getAttribute("totalCourses");
    Integer pendingCount = (Integer) request.getAttribute("pendingCount");
    Integer totalDiscussions = (Integer) request.getAttribute("totalDiscussions");
    Integer activeSessions = (Integer) request.getAttribute("activeSessions");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrator Control Center | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn <span>Admin</span></a>
    <nav>
        <a href="admin" style="color:var(--primary); font-weight:700;">Admin Dashboard</a>
        <a href="courses">Browse Catalog</a>
        <a href="community">Community Forum</a>
        <a href="test-db">DB Diagnostic</a>
        <a class="btn small outline" href="logout">Logout (<%= currentUser.getName() %>)</a>
    </nav>
</header>

<main class="section">
    <div class="welcome">
        <div>
            <span class="eyebrow">SYSTEM GOVERNANCE</span>
            <h1>Administrator Control Center</h1>
            <p>Manage platform accounts, review curriculum submissions, tune system parameters, and monitor learning analytics.</p>
        </div>
        <div>
            <span class="tag role-admin" style="font-size:0.85rem; padding:6px 14px;">Authorized: <%= currentUser.getName() %></span>
        </div>
    </div>

    <% if (adminSuccess != null) { %>
        <div class="alert success">
            <span>✓</span> <%= adminSuccess %>
        </div>
    <% } %>

    <% if (adminError != null) { %>
        <div class="alert error">
            <span>✗</span> <%= adminError %>
        </div>
    <% } %>

    <!-- Metric Stat Cards -->
    <div class="grid four" style="margin-bottom: 28px;">
        <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-content">
                <strong><%= totalUsers != null ? totalUsers : 0 %></strong>
                <span>Registered Users</span>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📚</div>
            <div class="stat-content">
                <strong><%= totalCourses != null ? totalCourses : 0 %></strong>
                <span>Total Courses</span>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background: rgba(250, 204, 21, 0.15); color: var(--warning);">⏳</div>
            <div class="stat-content">
                <strong style="color: <%= (pendingCount != null && pendingCount > 0) ? "var(--warning)" : "var(--primary)" %>;">
                    <%= pendingCount != null ? pendingCount : 0 %>
                </strong>
                <span>Pending Approvals</span>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon" style="background: rgba(56, 189, 248, 0.15); color: var(--info);">💬</div>
            <div class="stat-content">
                <strong><%= totalDiscussions != null ? totalDiscussions : 0 %></strong>
                <span>Community Posts</span>
            </div>
        </div>
    </div>

    <!-- Navigation Tabs -->
    <div class="tabs">
        <button class="tab-btn active" onclick="showTab('users-tab', this)">1. User Management</button>
        <button class="tab-btn" onclick="showTab('courses-tab', this)">2. Course Approvals (<%= pendingCount != null ? pendingCount : 0 %>)</button>
        <button class="tab-btn" onclick="showTab('analytics-tab', this)">3. Enrollment Stats & Charts</button>
        <button class="tab-btn" onclick="showTab('settings-tab', this)">4. System Settings</button>
        <button class="tab-btn" onclick="showTab('community-tab', this)">5. Community Moderation</button>
    </div>

    <!-- TAB 1: USER MANAGEMENT -->
    <section id="users-tab" class="tab-content">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
            <div>
                <h2>User Management</h2>
                <p>Create, view, update roles, and manage user accounts.</p>
            </div>
            <button class="btn small" onclick="toggleAddUserModal()">+ Add New User</button>
        </div>

        <!-- Add User Form Drawer -->
        <div id="addUserBox" class="card" style="display:none; margin-bottom:20px; border-color:var(--primary);">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                <h3>Create New User Account</h3>
                <button class="btn small secondary" onclick="toggleAddUserModal()">✕ Cancel</button>
            </div>
            <form method="post" action="admin">
                <input type="hidden" name="action" value="createUser">
                <div class="grid three">
                    <label>
                        Full Name
                        <input type="text" name="name" placeholder="e.g. Dr. Jane Smith" required>
                    </label>
                    <label>
                        Email Address
                        <input type="email" name="email" placeholder="e.g. janesmith@greenlearn.com" required>
                    </label>
                    <label>
                        Password
                        <input type="password" name="password" minlength="6" placeholder="At least 6 characters" required>
                    </label>
                </div>
                <div class="grid two" style="margin-top:10px;">
                    <label>
                        System Role
                        <select name="role">
                            <option value="LEARNER">Learner</option>
                            <option value="INSTRUCTOR">Instructor</option>
                            <option value="ADMIN">Administrator</option>
                        </select>
                    </label>
                    <div style="display:flex; align-items:flex-end;">
                        <button class="btn full" type="submit" style="margin-bottom:14px;">Confirm Create User</button>
                    </div>
                </div>
            </form>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>User Name</th>
                        <th>Email Address</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (users != null && !users.isEmpty()) {
                        for (User u : users) {
                            String roleClass = "role-learner";
                            if (u.isAdmin()) roleClass = "role-admin";
                            else if (u.isInstructor()) roleClass = "role-instructor";
                    %>
                        <tr>
                            <td>#<%= u.getId() %></td>
                            <td><strong><%= u.getName() %></strong></td>
                            <td><%= u.getEmail() %></td>
                            <td><span class="tag <%= roleClass %>"><%= u.getRole() %></span></td>
                            <td>
                                <span class="tag <%= "ACTIVE".equalsIgnoreCase(u.getStatus()) ? "active" : "inactive" %>">
                                    <%= u.getStatus() != null ? u.getStatus() : "ACTIVE" %>
                                </span>
                            </td>
                            <td><%= u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0, 10) : "N/A" %></td>
                            <td>
                                <div style="display:flex; gap:6px;">
                                    <!-- Inline Edit Role/Status Form -->
                                    <form method="post" action="admin" style="display:inline-flex; gap:4px; margin:0;">
                                        <input type="hidden" name="action" value="updateUser">
                                        <input type="hidden" name="id" value="<%= u.getId() %>">
                                        <input type="hidden" name="name" value="<%= u.getName() %>">
                                        <input type="hidden" name="email" value="<%= u.getEmail() %>">
                                        <select name="role" style="padding:4px 8px; font-size:0.8rem; margin:0; width:auto;">
                                            <option value="LEARNER" <%= "LEARNER".equals(u.getRole()) ? "selected" : "" %>>Learner</option>
                                            <option value="INSTRUCTOR" <%= "INSTRUCTOR".equals(u.getRole()) ? "selected" : "" %>>Instructor</option>
                                            <option value="ADMIN" <%= "ADMIN".equals(u.getRole()) ? "selected" : "" %>>Admin</option>
                                        </select>
                                        <select name="status" style="padding:4px 8px; font-size:0.8rem; margin:0; width:auto;">
                                            <option value="ACTIVE" <%= "ACTIVE".equalsIgnoreCase(u.getStatus()) ? "selected" : "" %>>Active</option>
                                            <option value="INACTIVE" <%= "INACTIVE".equalsIgnoreCase(u.getStatus()) ? "selected" : "" %>>Inactive</option>
                                        </select>
                                        <button class="btn small" type="submit" title="Save Role & Status">Update</button>
                                    </form>

                                    <!-- Delete Button (prevent self-deletion) -->
                                    <% if (u.getId() != currentUser.getId()) { %>
                                        <form method="post" action="admin" style="margin:0;" onsubmit="return confirm('Are you sure you want to delete user <%= u.getName() %>?');">
                                            <input type="hidden" name="action" value="deleteUser">
                                            <input type="hidden" name="id" value="<%= u.getId() %>">
                                            <button class="btn small danger" type="submit">Delete</button>
                                        </form>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="7" style="text-align:center; color:var(--text-muted);">No users found.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>

    <!-- TAB 2: COURSE CONTENT MANAGEMENT -->
    <section id="courses-tab" class="tab-content" style="display:none;">
        <div style="margin-bottom:16px;">
            <h2>Course Content Management</h2>
            <p>Review submitted curriculum from instructors, inspect syllabus content, and approve or reject courses.</p>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Course Title & Category</th>
                        <th>Instructor</th>
                        <th>Status</th>
                        <th>Syllabus Preview</th>
                        <th>Approval Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (courses != null && !courses.isEmpty()) {
                        for (Course c : courses) {
                            String tagClass = "pending";
                            if (c.isApproved()) tagClass = "approved";
                            else if ("REJECTED".equalsIgnoreCase(c.getStatus())) tagClass = "rejected";
                    %>
                        <tr>
                            <td>#<%= c.getId() %></td>
                            <td>
                                <strong><%= c.getTitle() %></strong><br>
                                <span class="tag" style="background:rgba(16,185,129,0.15); color:var(--accent); font-size:11px; margin-top:3px;">
                                    <%= c.getCategory() %>
                                </span>
                            </td>
                            <td><%= c.getInstructor() %></td>
                            <td><span class="tag <%= tagClass %>"><%= c.getStatus() %></span></td>
                            <td style="max-width:320px;">
                                <div style="font-size:0.85rem; color:var(--text-muted); max-height:60px; overflow-y:auto; line-height:1.4;">
                                    <%= c.getContent() != null && !c.getContent().isEmpty() ? c.getContent() : c.getDescription() %>
                                </div>
                            </td>
                            <td>
                                <form method="post" action="admin" style="display:flex; gap:6px; margin:0;">
                                    <input type="hidden" name="action" value="updateCourseStatus">
                                    <input type="hidden" name="courseId" value="<%= c.getId() %>">
                                    <% if (!c.isApproved()) { %>
                                        <button class="btn small" name="status" value="APPROVED">✓ Approve</button>
                                    <% } %>
                                    <% if (!"REJECTED".equalsIgnoreCase(c.getStatus())) { %>
                                        <button class="btn small danger" name="status" value="REJECTED">✗ Reject</button>
                                    <% } %>
                                </form>
                            </td>
                        </tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="6" style="text-align:center; color:var(--text-muted);">No courses submitted yet.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>

    <!-- TAB 3: ENROLLMENT & COMPLETION STATS WITH GRAPHICAL CHARTS -->
    <section id="analytics-tab" class="tab-content" style="display:none;">
        <div style="margin-bottom:16px;">
            <h2>Course Enrollment & Completion Analytics</h2>
            <p>Graphical representations of live course enrollments and completions derived directly from database records.</p>
        </div>

        <div class="grid two">
            <!-- Visual Bar Chart Card -->
            <div class="card">
                <h3>Enrollment Volume by Course</h3>
                <p>Total learner enrollments registered per course:</p>
                <div class="chart-bar-container" style="margin-top:20px;">
                    <% if (enrollmentStats != null && !enrollmentStats.isEmpty()) {
                        int maxEnrollment = 1;
                        for (EnrollmentStats es : enrollmentStats) {
                            if (es.getEnrolledCount() > maxEnrollment) maxEnrollment = es.getEnrolledCount();
                        }
                        for (EnrollmentStats es : enrollmentStats) {
                            int barWidth = (int) Math.round(((double) es.getEnrolledCount() / maxEnrollment) * 100);
                            if (es.getEnrolledCount() > 0 && barWidth < 5) barWidth = 5;
                    %>
                        <div class="chart-bar-row">
                            <span style="font-size:0.85rem; font-weight:600; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                                <%= es.getCourseTitle() %>
                            </span>
                            <div class="progress-bar-bg" style="height:16px;">
                                <div class="progress-bar-fill" style="width: <%= barWidth %>%;"></div>
                            </div>
                            <span style="text-align:right; font-weight:700; color:var(--primary); font-size:0.85rem;">
                                <%= es.getEnrolledCount() %> Enrolled
                            </span>
                        </div>
                    <%  }
                    } else { %>
                        <p style="color:var(--text-muted);">No enrollment data recorded yet.</p>
                    <% } %>
                </div>
            </div>

            <!-- Completion Rate Card -->
            <div class="card">
                <h3>Course Completion Rates</h3>
                <p>Ratio of learners who achieved 100% curriculum completion:</p>
                <div class="chart-bar-container" style="margin-top:20px;">
                    <% if (enrollmentStats != null && !enrollmentStats.isEmpty()) {
                        for (EnrollmentStats es : enrollmentStats) {
                    %>
                        <div class="chart-bar-row">
                            <span style="font-size:0.85rem; font-weight:600; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                                <%= es.getCourseTitle() %>
                            </span>
                            <div class="progress-bar-bg" style="height:16px;">
                                <div class="progress-bar-fill" style="width: <%= es.getFormattedRate() %>%; background: linear-gradient(90deg, #38bdf8, #22c55e);"></div>
                            </div>
                            <span style="text-align:right; font-weight:700; color:#38bdf8; font-size:0.85rem;">
                                <%= es.getFormattedRate() %>% (<%= es.getCompletedCount() %>/<%= es.getEnrolledCount() %>)
                            </span>
                        </div>
                    <%  }
                    } else { %>
                        <p style="color:var(--text-muted);">No completions recorded yet.</p>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Telemetry Summary Table -->
        <div class="card" style="margin-top:20px;">
            <h3>Enrollment Statistics Telemetry Table</h3>
            <div class="table-wrap" style="margin-top:12px;">
                <table>
                    <thead>
                        <tr>
                            <th>Course ID</th>
                            <th>Course Name</th>
                            <th>Total Learners Enrolled</th>
                            <th>Completed Learners</th>
                            <th>Completion Rate</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (enrollmentStats != null && !enrollmentStats.isEmpty()) {
                            for (EnrollmentStats es : enrollmentStats) {
                        %>
                            <tr>
                                <td>#<%= es.getCourseId() %></td>
                                <td><strong><%= es.getCourseTitle() %></strong></td>
                                <td><%= es.getEnrolledCount() %> learners</td>
                                <td><%= es.getCompletedCount() %> completed</td>
                                <td>
                                    <span class="tag <%= es.getFormattedRate() > 50 ? "approved" : "pending" %>">
                                        <%= es.getFormattedRate() %>%
                                    </span>
                                </td>
                            </tr>
                        <%  }
                        } else { %>
                            <tr><td colspan="5" style="text-align:center; color:var(--text-muted);">No metrics available.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <!-- TAB 4: SYSTEM SETTINGS -->
    <section id="settings-tab" class="tab-content" style="display:none;">
        <div style="margin-bottom:16px;">
            <h2>System-Wide Configuration Settings</h2>
            <p>Update system runtime options, visitor self-registration access, and maintenance controls.</p>
        </div>

        <div class="card" style="max-width:750px;">
            <form method="post" action="admin">
                <input type="hidden" name="action" value="updateSettings">
                
                <%
                    String platName = "GreenLearn Academy";
                    String maintMode = "false";
                    String allowReg = "true";
                    String enrollCap = "100";
                    String contactEmail = "support@greenlearn.com";
                    if (settings != null) {
                        for (SystemSetting s : settings) {
                            if ("platform_name".equals(s.getSettingKey())) platName = s.getSettingValue();
                            if ("maintenance_mode".equals(s.getSettingKey())) maintMode = s.getSettingValue();
                            if ("allow_registration".equals(s.getSettingKey())) allowReg = s.getSettingValue();
                            if ("default_enrollment_cap".equals(s.getSettingKey())) enrollCap = s.getSettingValue();
                            if ("contact_email".equals(s.getSettingKey())) contactEmail = s.getSettingValue();
                        }
                    }
                %>

                <label>
                    Platform Title
                    <input type="text" name="platform_name" value="<%= platName %>" required>
                    <small>Displayed on browser titles and header brand banners.</small>
                </label>

                <div class="grid two">
                    <label>
                        Allow Public Visitor Registration
                        <select name="allow_registration">
                            <option value="true" <%= "true".equalsIgnoreCase(allowReg) ? "selected" : "" %>>Enabled (Visitors can register)</option>
                            <option value="false" <%= "false".equalsIgnoreCase(allowReg) ? "selected" : "" %>>Disabled (Admin invites only)</option>
                        </select>
                    </label>

                    <label>
                        Maintenance Mode
                        <select name="maintenance_mode">
                            <option value="false" <%= "false".equalsIgnoreCase(maintMode) ? "selected" : "" %>>Normal Operation (Active)</option>
                            <option value="true" <%= "true".equalsIgnoreCase(maintMode) ? "selected" : "" %>>Maintenance Mode Enabled</option>
                        </select>
                    </label>
                </div>

                <div class="grid two">
                    <label>
                        Default Batch Enrollment Cap
                        <input type="number" name="default_enrollment_cap" value="<%= enrollCap %>" min="1" max="1000">
                    </label>

                    <label>
                        Administrative Contact Email
                        <input type="email" name="contact_email" value="<%= contactEmail %>" required>
                    </label>
                </div>

                <div style="margin-top:16px;">
                    <button class="btn" type="submit">Save System Configuration</button>
                </div>
            </form>
        </div>
    </section>

    <!-- TAB 5: COMMUNITY INTERACTION & MODERATION -->
    <section id="community-tab" class="tab-content" style="display:none;">
        <div style="margin-bottom:16px;">
            <h2>Community Discussion Activity</h2>
            <p>Overview of active forum discussions, peer questions, and moderation controls.</p>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Topic Title & Message</th>
                        <th>Author</th>
                        <th>Associated Course</th>
                        <th>Replies</th>
                        <th>Posted Date</th>
                        <th>Moderation</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (discussions != null && !discussions.isEmpty()) {
                        for (Discussion d : discussions) {
                    %>
                        <tr>
                            <td>#<%= d.getId() %></td>
                            <td>
                                <strong><%= d.getTitle() %></strong><br>
                                <small style="color:var(--text-muted);"><%= d.getMessage() %></small>
                            </td>
                            <td>
                                <strong><%= d.getUserName() %></strong><br>
                                <span class="tag role-<%= d.getUserRole() != null ? d.getUserRole().toLowerCase() : "learner" %>">
                                    <%= d.getUserRole() %>
                                </span>
                            </td>
                            <td><%= d.getCourseTitle() != null ? d.getCourseTitle() : "General Forum" %></td>
                            <td><%= d.getReplyCount() %> replies</td>
                            <td><%= d.getCreatedAt() != null ? d.getCreatedAt().toString().substring(0, 16) : "Recent" %></td>
                            <td>
                                <form method="post" action="admin" style="margin:0;" onsubmit="return confirm('Remove discussion topic #<%= d.getId() %>?');">
                                    <input type="hidden" name="action" value="deleteDiscussion">
                                    <input type="hidden" name="id" value="<%= d.getId() %>">
                                    <button class="btn small danger" type="submit">Remove</button>
                                </form>
                            </td>
                        </tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="7" style="text-align:center; color:var(--text-muted);">No community discussions yet.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </section>
</main>

<footer>
    © 2026 GreenLearn Platform — Comprehensive Green Technology Academic System
</footer>

<script>
function showTab(tabId, btn) {
    document.querySelectorAll('.tab-content').forEach(t => t.style.display = 'none');
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.getElementById(tabId).style.display = 'block';
    btn.classList.add('active');
}

function toggleAddUserModal() {
    const box = document.getElementById('addUserBox');
    box.style.display = box.style.display === 'none' ? 'block' : 'none';
}
</script>
</body>
</html>