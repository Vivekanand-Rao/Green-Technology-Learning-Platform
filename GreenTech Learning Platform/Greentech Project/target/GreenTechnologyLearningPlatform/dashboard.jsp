<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.greentech.model.User" %>
<%@ page import="com.greentech.model.Course" %>
<%@ page import="com.greentech.model.Enrollment" %>
<%@ page import="com.greentech.model.Assignment" %>
<%@ page import="com.greentech.model.Submission" %>
<%@ page import="com.greentech.model.Discussion" %>
<%@ page import="com.greentech.model.Certificate" %>
<%@ page import="com.greentech.model.EnrollmentStats" %>
<%@ page import="java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp?error=auth");
        return;
    }
    if (user.isAdmin()) {
        response.sendRedirect("admin");
        return;
    }

    String feedbackMsg = (String) session.getAttribute("feedbackMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    session.removeAttribute("feedbackMsg");
    session.removeAttribute("errorMsg");

    // Learner Data
    List<Enrollment> enrollments = (List<Enrollment>) request.getAttribute("enrollments");
    List<Course> instructorCourses = (List<Course>) request.getAttribute("instructorCourses");
    if ((user.isLearner() && enrollments == null) || (user.isInstructor() && instructorCourses == null)) {
        response.sendRedirect("dashboard");
        return;
    }
    List<Submission> submissions = (List<Submission>) request.getAttribute("submissions");
    List<Assignment> availableAssignments = (List<Assignment>) request.getAttribute("availableAssignments");
    List<Discussion> communityTopics = (List<Discussion>) request.getAttribute("communityTopics");
    List<Certificate> certificates = (List<Certificate>) request.getAttribute("certificates");
    List<Course> recommendations = (List<Course>) request.getAttribute("recommendations");

    // Instructor Data
    List<Submission> pendingSubmissions = (List<Submission>) request.getAttribute("pendingSubmissions");
    List<Discussion> learnerDiscussions = (List<Discussion>) request.getAttribute("learnerDiscussions");
    List<EnrollmentStats> performanceStats = (List<EnrollmentStats>) request.getAttribute("performanceStats");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= user.getRole() %> Dashboard | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn <span><%= user.isInstructor() ? "Instructor" : "Learner" %></span></a>
    <nav>
        <a href="dashboard" style="color:var(--primary); font-weight:700;">Dashboard</a>
        <a href="courses">Browse Courses</a>
        <a href="community">Community</a>
        <a href="test-db">DB Status</a>
        <a class="btn small outline" href="logout">Logout (<%= user.getName() %>)</a>
    </nav>
</header>

<main class="section">
    <div class="welcome">
        <div>
            <span class="eyebrow"><%= user.getRole() %> WORKSPACE</span>
            <h1>Welcome back, <%= user.getName() %> 👋</h1>
            <p>Email: <strong><%= user.getEmail() %></strong> &bull; Role: 
                <span class="tag role-<%= user.getRole().toLowerCase() %>"><%= user.getRole() %></span>
            </p>
        </div>
        <div>
            <a class="btn small" href="courses">Explore All Courses</a>
        </div>
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

    <!-- ========================================== -->
    <!--          INSTRUCTOR DASHBOARD              -->
    <!-- ========================================== -->
    <% if (user.isInstructor()) { %>
        <!-- Instructor Top Metrics -->
        <div class="grid four" style="margin-bottom: 24px;">
            <div class="stat-card">
                <div class="stat-icon">📚</div>
                <div class="stat-content">
                    <strong><%= instructorCourses != null ? instructorCourses.size() : 0 %></strong>
                    <span>Authored Courses</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: rgba(250, 204, 21, 0.15); color: var(--warning);">📝</div>
                <div class="stat-content">
                    <strong><%= pendingSubmissions != null ? pendingSubmissions.size() : 0 %></strong>
                    <span>Learner Submissions</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: rgba(56, 189, 248, 0.15); color: var(--info);">💬</div>
                <div class="stat-content">
                    <strong><%= learnerDiscussions != null ? learnerDiscussions.size() : 0 %></strong>
                    <span>Community Posts</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">⭐</div>
                <div class="stat-content">
                    <strong>Level 1</strong>
                    <span>Instructor Standing</span>
                </div>
            </div>
        </div>

        <!-- Instructor Navigation Tabs -->
        <div class="tabs">
            <button class="tab-btn active" onclick="showTab('inst-courses', this)">1. Course Creation & History</button>
            <button class="tab-btn" onclick="showTab('inst-grading', this)">2. Assignment Grading (<%= pendingSubmissions != null ? pendingSubmissions.size() : 0 %>)</button>
            <button class="tab-btn" onclick="showTab('inst-performance', this)">3. Course Performance</button>
            <button class="tab-btn" onclick="showTab('inst-discussions', this)">4. Learner Interactions</button>
        </div>

        <!-- TAB 1: COURSE CREATION & HISTORY -->
        <div id="inst-courses" class="tab-content">
            <div class="grid two">
                <!-- Course Creation Form -->
                <div class="card">
                    <span class="eyebrow">NEW CURRICULUM</span>
                    <h2>Create a New Course</h2>
                    <p>Submit your course details and syllabus curriculum for administrator approval.</p>
                    <form method="post" action="create-course" style="margin-top:14px;">
                        <label>
                            Course Title
                            <input type="text" name="title" placeholder="e.g. Geothermal Energy Systems" required>
                        </label>
                        <label>
                            Category
                            <select name="category">
                                <option value="Renewable Energy">Renewable Energy</option>
                                <option value="Waste Management">Waste Management</option>
                                <option value="Sustainable Architecture">Sustainable Architecture</option>
                                <option value="Clean Mobility">Clean Mobility</option>
                                <option value="Environmental Policy">Environmental Policy</option>
                            </select>
                        </label>
                        <label>
                            Course Description
                            <input type="text" name="description" placeholder="A concise summary of key concepts covered" required>
                        </label>
                        <label>
                            Syllabus Curriculum & Content
                            <textarea name="content" rows="4" placeholder="Module 1: Thermodynamics&#10;Module 2: Subsurface Heat Transfer&#10;Module 3: Power Plant Cycle"></textarea>
                        </label>
                        <button class="btn full" type="submit">Submit Course for Approval</button>
                    </form>
                </div>

                <!-- Create Assignment for Course -->
                <div class="card">
                    <span class="eyebrow">COURSEWORK</span>
                    <h2>Create Course Assignment</h2>
                    <p>Assign practical projects, calculations, or essays to learners.</p>
                    <form method="post" action="assignment" style="margin-top:14px;">
                        <input type="hidden" name="action" value="createAssignment">
                        <label>
                            Select Authored Course
                            <select name="courseId" required>
                                <% if (instructorCourses != null && !instructorCourses.isEmpty()) {
                                    for (Course c : instructorCourses) { %>
                                        <option value="<%= c.getId() %>"><%= c.getTitle() %> (<%= c.getStatus() %>)</option>
                                <%  }
                                } else { %>
                                    <option value="">No courses created yet</option>
                                <% } %>
                            </select>
                        </label>
                        <label>
                            Assignment Title
                            <input type="text" name="title" placeholder="e.g. Wind Siting Calculation" required>
                        </label>
                        <div class="grid two">
                            <label>
                                Maximum Score
                                <input type="number" name="maxScore" value="100" min="10" max="100" required>
                            </label>
                            <label>
                                Due Date / Window
                                <input type="text" name="dueDate" placeholder="e.g. 2026-11-20" required>
                            </label>
                        </div>
                        <label>
                            Assignment Instructions
                            <textarea name="description" rows="3" placeholder="Provide instructions, rubric guidelines, and required outputs." required></textarea>
                        </label>
                        <button class="btn full secondary" type="submit">Publish Assignment</button>
                    </form>
                </div>
            </div>

            <!-- Course History Table -->
            <div class="card" style="margin-top:24px;">
                <h2>Course History & Publication Log</h2>
                <p>Complete historical log of your authored courses and their approval status.</p>
                <div class="table-wrap" style="margin-top:12px;">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Course Title</th>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th>Submitted Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (instructorCourses != null && !instructorCourses.isEmpty()) {
                                for (Course c : instructorCourses) {
                                    String tagClass = "pending";
                                    if (c.isApproved()) tagClass = "approved";
                                    else if ("REJECTED".equalsIgnoreCase(c.getStatus())) tagClass = "rejected";
                            %>
                                <tr>
                                    <td>#<%= c.getId() %></td>
                                    <td><strong><%= c.getTitle() %></strong></td>
                                    <td><span class="tag" style="background:rgba(16,185,129,0.15); color:var(--accent);"><%= c.getCategory() %></span></td>
                                    <td><%= c.getDescription() %></td>
                                    <td><span class="tag <%= tagClass %>"><%= c.getStatus() %></span></td>
                                    <td><%= c.getCreatedAt() != null ? c.getCreatedAt().toString().substring(0, 10) : "Recent" %></td>
                                </tr>
                            <%  }
                            } else { %>
                                <tr><td colspan="6" style="text-align:center; color:var(--text-muted);">You have not created any courses yet. Use the form above to submit your first course.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TAB 2: ASSIGNMENT GRADING PANEL -->
        <div id="inst-grading" class="tab-content" style="display:none;">
            <div style="margin-bottom:16px;">
                <h2>Assignment Grading & Feedback Panel</h2>
                <p>Review learner submissions, verify work, and provide official grading and written feedback.</p>
            </div>

            <% if (pendingSubmissions != null && !pendingSubmissions.isEmpty()) { %>
                <div class="grid two">
                    <% for (Submission sub : pendingSubmissions) { %>
                        <div class="card" style="border-color: <%= sub.isGraded() ? "var(--border)" : "var(--primary)" %>;">
                            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                                <span class="tag <%= sub.isGraded() ? "graded" : "submitted" %>"><%= sub.getStatus() %></span>
                                <small><%= sub.getSubmittedAt() != null ? sub.getSubmittedAt().toString().substring(0, 16) : "Recent" %></small>
                            </div>
                            <h3><%= sub.getAssignmentTitle() %></h3>
                            <p style="color:var(--text); font-size:0.9rem;">
                                Submitted by: <strong><%= sub.getLearnerName() %></strong>
                            </p>
                            <div style="background:var(--bg-secondary); padding:12px; border-radius:var(--radius-sm); border:1px solid var(--border); margin:10px 0; font-size:0.9rem; max-height:120px; overflow-y:auto;">
                                <strong>Learner Solution:</strong><br>
                                <%= sub.getSubmissionText() %>
                            </div>

                            <% if (sub.isGraded()) { %>
                                <div style="margin-top:10px; padding:10px; background:rgba(34,197,94,0.1); border-radius:var(--radius-sm); border:1px solid rgba(34,197,94,0.3);">
                                    <strong>Score: <%= sub.getScore() %>/100</strong><br>
                                    <small style="color:var(--text-muted);">Feedback: <%= sub.getFeedback() %></small>
                                </div>
                            <% } else { %>
                                <form method="post" action="assignment" style="margin-top:12px;">
                                    <input type="hidden" name="action" value="gradeSubmission">
                                    <input type="hidden" name="submissionId" value="<%= sub.getId() %>">
                                    <div class="grid two">
                                        <label>
                                            Score (out of 100)
                                            <input type="number" name="score" min="0" max="100" placeholder="e.g. 92" required>
                                        </label>
                                        <label>
                                            Evaluation Feedback
                                            <input type="text" name="feedback" placeholder="e.g. Well calculated, good analysis" required>
                                        </label>
                                    </div>
                                    <button class="btn small full" type="submit">Submit Grade & Feedback</button>
                                </form>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="card" style="text-align:center; padding:40px;">
                    <h3>No Learner Submissions Pending</h3>
                    <p>When enrolled learners complete your assignments, their solutions will appear here for evaluation.</p>
                </div>
            <% } %>
        </div>

        <!-- TAB 3: COURSE PERFORMANCE -->
        <div id="inst-performance" class="tab-content" style="display:none;">
            <div style="margin-bottom:16px;">
                <h2>Course Performance & Learner Progress</h2>
                <p>Summary of course performance metrics, active learners, and completion stats.</p>
            </div>
            <div class="card">
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>Course Title</th>
                                <th>Active Enrollments</th>
                                <th>Certificates Awarded</th>
                                <th>Completion Rate</th>
                                <th>Visual Progress</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (performanceStats != null && !performanceStats.isEmpty()) {
                                for (EnrollmentStats es : performanceStats) {
                            %>
                                <tr>
                                    <td><strong><%= es.getCourseTitle() %></strong></td>
                                    <td><%= es.getEnrolledCount() %> learners</td>
                                    <td><%= es.getCompletedCount() %> completed</td>
                                    <td><span class="tag approved"><%= es.getFormattedRate() %>%</span></td>
                                    <td style="width:200px;">
                                        <div class="progress-bar-bg">
                                            <div class="progress-bar-fill" style="width: <%= es.getFormattedRate() %>%;"></div>
                                        </div>
                                    </td>
                                </tr>
                            <%  }
                            } else { %>
                                <tr><td colspan="5" style="text-align:center; color:var(--text-muted);">No active enrollment stats recorded.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TAB 4: LEARNER INTERACTIONS -->
        <div id="inst-discussions" class="tab-content" style="display:none;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
                <div>
                    <h2>Learner Interactions & Q&A</h2>
                    <p>Communicate with learners, answer technical questions, and foster green tech collaboration.</p>
                </div>
                <a class="btn small" href="community">Open Full Community Forum</a>
            </div>

            <div class="grid two">
                <% if (learnerDiscussions != null && !learnerDiscussions.isEmpty()) {
                    for (Discussion disc : learnerDiscussions) { %>
                        <div class="card">
                            <span class="tag" style="background:rgba(56,189,248,0.15); color:var(--info); font-size:11px;">
                                <%= disc.getCourseTitle() != null ? disc.getCourseTitle() : "General Forum" %>
                            </span>
                            <h4 style="margin:8px 0 4px;"><%= disc.getTitle() %></h4>
                            <p style="font-size:0.9rem;"><%= disc.getMessage() %></p>
                            <small style="color:var(--text-muted);">
                                Asked by: <strong><%= disc.getUserName() %></strong> &bull; <%= disc.getReplyCount() %> replies
                            </small>
                            <div style="margin-top:12px;">
                                <a class="btn small outline" href="community?topicId=<%= disc.getId() %>">View & Reply</a>
                            </div>
                        </div>
                <%  }
                } else { %>
                    <div class="card" style="grid-column: 1 / -1; text-align:center;">
                        <p>No active learner questions at the moment.</p>
                    </div>
                <% } %>
            </div>
        </div>

    <!-- ========================================== -->
    <!--            LEARNER DASHBOARD               -->
    <!-- ========================================== -->
    <% } else if (user.isLearner()) { %>
        <!-- Learner Metrics -->
        <div class="grid four" style="margin-bottom: 24px;">
            <div class="stat-card">
                <div class="stat-icon">📚</div>
                <div class="stat-content">
                    <strong><%= enrollments != null ? enrollments.size() : 0 %></strong>
                    <span>Enrolled Courses</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: rgba(34, 197, 94, 0.15); color: var(--primary);">🏆</div>
                <div class="stat-content">
                    <strong><%= certificates != null ? certificates.size() : 0 %></strong>
                    <span>Certificates Earned</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: rgba(250, 204, 21, 0.15); color: var(--warning);">📝</div>
                <div class="stat-content">
                    <strong><%= submissions != null ? submissions.size() : 0 %></strong>
                    <span>Assignments Submitted</span>
                </div>
            </div>
            <div class="stat-card">
                <div class="stat-icon" style="background: rgba(56, 189, 248, 0.15); color: var(--info);">💬</div>
                <div class="stat-content">
                    <strong><%= communityTopics != null ? communityTopics.size() : 0 %></strong>
                    <span>Community Discussions</span>
                </div>
            </div>
        </div>

        <!-- Learner Navigation Tabs -->
        <div class="tabs">
            <button class="tab-btn active" onclick="showTab('lrn-enrollments', this)">1. Enrolled Courses & Progress</button>
            <button class="tab-btn" onclick="showTab('lrn-assignments', this)">2. Assignments & Feedback</button>
            <button class="tab-btn" onclick="showTab('lrn-certificates', this)">3. Certificates (<%= certificates != null ? certificates.size() : 0 %>)</button>
            <button class="tab-btn" onclick="showTab('lrn-recommendations', this)">4. Recommended for You</button>
            <button class="tab-btn" onclick="showTab('lrn-community', this)">5. Community Discussions</button>
        </div>

        <!-- TAB 1: ENROLLED COURSES & PROGRESS -->
        <div id="lrn-enrollments" class="tab-content">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
                <div>
                    <h2>My Enrolled Courses</h2>
                    <p>Track your modules, update your study progress, and complete green technology curriculum.</p>
                </div>
                <a class="btn small" href="courses">+ Browse More Courses</a>
            </div>

            <% if (enrollments != null && !enrollments.isEmpty()) { %>
                <div class="grid two">
                    <% for (Enrollment enr : enrollments) { %>
                        <div class="card">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="tag <%= enr.isCompleted() ? "completed" : "active" %>">
                                    <%= enr.isCompleted() ? "✓ COMPLETED" : "IN PROGRESS" %>
                                </span>
                                <span style="font-weight:700; color:var(--primary);"><%= enr.getProgress() %>%</span>
                            </div>
                            <h3 style="margin:10px 0 6px;"><%= enr.getCourseTitle() %></h3>
                            <div class="progress-bar-bg" style="margin:10px 0 16px;">
                                <div class="progress-bar-fill" style="width: <%= enr.getProgress() %>%;"></div>
                            </div>

                            <!-- Interactive Progress Update Buttons -->
                            <div style="background:var(--bg-secondary); padding:10px; border-radius:var(--radius-sm); border:1px solid var(--border);">
                                <small style="display:block; margin-bottom:6px; font-weight:600;">Update Lesson Progress:</small>
                                <form method="post" action="enroll" style="display:flex; gap:6px; flex-wrap:wrap;">
                                    <input type="hidden" name="action" value="updateProgress">
                                    <input type="hidden" name="courseId" value="<%= enr.getCourseId() %>">
                                    <button class="btn small outline" type="submit" name="progress" value="25">25%</button>
                                    <button class="btn small outline" type="submit" name="progress" value="50">50%</button>
                                    <button class="btn small outline" type="submit" name="progress" value="75">75%</button>
                                    <button class="btn small" type="submit" name="progress" value="100" style="background:#15803d; border-color:#22c55e;">100% (Complete & Certify)</button>
                                </form>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="card" style="text-align:center; padding:40px;">
                    <h3>No Active Course Enrollments</h3>
                    <p>Start your green journey by exploring our approved courses in Solar, Wind, and Circular Economy.</p>
                    <a class="btn" href="courses" style="margin-top:12px;">Browse Catalog Now</a>
                </div>
            <% } %>
        </div>

        <!-- TAB 2: ASSIGNMENTS & FEEDBACK -->
        <div id="lrn-assignments" class="tab-content" style="display:none;">
            <div style="margin-bottom:16px;">
                <h2>Assignments & Instructor Feedback</h2>
                <p>Complete project assignments and review evaluated scores with instructor comments.</p>
            </div>

            <div class="grid two">
                <!-- Submit Pending Assignment Box -->
                <div class="card">
                    <h3>Submit Assignment Solution</h3>
                    <p>Select a coursework task from your enrolled subjects and enter your answer.</p>
                    <form method="post" action="assignment" style="margin-top:12px;">
                        <input type="hidden" name="action" value="submitAssignment">
                        <label>
                            Available Assignment Task
                            <select name="assignmentId" required>
                                <% if (availableAssignments != null && !availableAssignments.isEmpty()) {
                                    for (Assignment a : availableAssignments) { %>
                                        <option value="<%= a.getId() %>"><%= a.getCourseTitle() %>: <%= a.getTitle() %> (Max: <%= a.getMaxScore() %> pts)</option>
                                <%  }
                                } else { %>
                                    <option value="">No pending assignments</option>
                                <% } %>
                            </select>
                        </label>
                        <label>
                            Your Solution & Technical Justification
                            <textarea name="submissionText" rows="4" placeholder="Detail your calculations, methodology, or proposed green design..." required></textarea>
                        </label>
                        <button class="btn full" type="submit">Submit for Instructor Evaluation</button>
                    </form>
                </div>

                <!-- Evaluated Submissions & Feedback History -->
                <div class="card">
                    <h3>My Assignment Submissions & Feedback</h3>
                    <div style="display:flex; flex-direction:column; gap:12px; margin-top:12px;">
                        <% if (submissions != null && !submissions.isEmpty()) {
                            for (Submission sub : submissions) { %>
                                <div style="background:var(--bg-secondary); padding:14px; border-radius:var(--radius-sm); border:1px solid var(--border);">
                                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
                                        <strong style="color:#ffffff;"><%= sub.getAssignmentTitle() %></strong>
                                        <span class="tag <%= sub.isGraded() ? "graded" : "submitted" %>"><%= sub.getStatus() %></span>
                                    </div>
                                    <p style="font-size:0.85rem; color:var(--text-muted); margin-bottom:6px;">
                                        <%= sub.getSubmissionText() %>
                                    </p>
                                    <% if (sub.isGraded()) { %>
                                        <div style="background:rgba(34,197,94,0.1); padding:8px 12px; border-radius:6px; border:1px solid rgba(34,197,94,0.3); font-size:0.85rem;">
                                            <span style="color:var(--primary); font-weight:700;">Score: <%= sub.getScore() %>/100</span><br>
                                            <span>Instructor Feedback: <%= sub.getFeedback() %></span>
                                        </div>
                                    <% } else { %>
                                        <small style="color:var(--warning);">Submitted &bull; Awaiting instructor evaluation</small>
                                    <% } %>
                                </div>
                        <%  }
                        } else { %>
                            <p style="color:var(--text-muted);">You have not submitted any assignments yet.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <!-- TAB 3: CERTIFICATES -->
        <div id="lrn-certificates" class="tab-content" style="display:none;">
            <div style="margin-bottom:16px;">
                <h2>Earned Certificates & Verification</h2>
                <p>Official course completion certificates issued upon reaching 100% curriculum progress.</p>
            </div>

            <% if (certificates != null && !certificates.isEmpty()) { %>
                <div class="grid two">
                    <% for (Certificate cert : certificates) { %>
                        <div class="card" style="border: 2px solid var(--primary); background: linear-gradient(135deg, #10261b, #153825);">
                            <div style="display:flex; justify-content:space-between; align-items:center;">
                                <span class="tag approved">VERIFIED CERTIFICATE</span>
                                <small style="color:var(--text-muted);"><%= cert.getIssueDate() != null ? cert.getIssueDate().toString().substring(0, 10) : "2026" %></small>
                            </div>
                            <h3 style="margin:12px 0 6px;"><%= cert.getCourseTitle() %></h3>
                            <p style="font-size:0.9rem;">
                                Awarded to: <strong><%= cert.getLearnerName() %></strong><br>
                                Certificate Verification Code: <code style="color:var(--primary); background:#0c1712; padding:2px 6px; border-radius:4px;"><%= cert.getCertificateCode() %></code>
                            </p>
                            <div style="margin-top:14px;">
                                <a class="btn small" href="certificate?code=<%= cert.getCertificateCode() %>" target="_blank">View Official Certificate 🏆</a>
                            </div>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="card" style="text-align:center; padding:40px;">
                    <h3>No Certificates Earned Yet</h3>
                    <p>Complete any enrolled course to 100% progress to earn your verified Green Technology Certificate!</p>
                </div>
            <% } %>
        </div>

        <!-- TAB 4: PERSONALIZED RECOMMENDATIONS -->
        <div id="lrn-recommendations" class="tab-content" style="display:none;">
            <div style="margin-bottom:16px;">
                <h2>Personalized Course Recommendations</h2>
                <p>Recommendations tailored dynamically to your learning history and green technology interests.</p>
            </div>

            <% if (recommendations != null && !recommendations.isEmpty()) { %>
                <div class="grid two">
                    <% for (Course rec : recommendations) { %>
                        <div class="card">
                            <span class="tag" style="background:rgba(16,185,129,0.15); color:var(--accent);">
                                <%= rec.getCategory() %>
                            </span>
                            <h3 style="margin:8px 0 6px;"><%= rec.getTitle() %></h3>
                            <p style="font-size:0.9rem;"><%= rec.getDescription() %></p>
                            <small style="color:var(--text-muted); display:block; margin-bottom:12px;">Instructor: <%= rec.getInstructor() %></small>
                            <form method="post" action="enroll" style="margin:0;">
                                <input type="hidden" name="courseId" value="<%= rec.getId() %>">
                                <button class="btn small full" type="submit">Enroll in Recommended Course</button>
                            </form>
                        </div>
                    <% } %>
                </div>
            <% } else { %>
                <div class="card" style="text-align:center; padding:40px;">
                    <p>No further recommendations at this time. Explore all offerings in the catalog.</p>
                    <a class="btn small" href="courses">Browse Catalog</a>
                </div>
            <% } %>
        </div>

        <!-- TAB 5: COMMUNITY DISCUSSIONS -->
        <div id="lrn-community" class="tab-content" style="display:none;">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
                <div>
                    <h2>Community Discussions & Interactions</h2>
                    <p>Share ideas, ask questions, and collaborate with green tech instructors and peers.</p>
                </div>
                <a class="btn small" href="community">Open Full Community Forum</a>
            </div>

            <div class="grid two">
                <!-- Ask New Question Form -->
                <div class="card">
                    <h3>Ask a Question in Community</h3>
                    <form method="post" action="community" style="margin-top:12px;">
                        <input type="hidden" name="action" value="createTopic">
                        <label>
                            Discussion Title
                            <input type="text" name="title" placeholder="e.g. Battery storage lifetime in hot climates" required>
                        </label>
                        <label>
                            Question Details
                            <textarea name="message" rows="3" placeholder="Provide background on your question..." required></textarea>
                        </label>
                        <button class="btn full" type="submit">Post to Community</button>
                    </form>
                </div>

                <!-- Recent Forum Topics -->
                <div class="card">
                    <h3>Recent Forum Topics</h3>
                    <div style="display:flex; flex-direction:column; gap:10px; margin-top:10px;">
                        <% if (communityTopics != null && !communityTopics.isEmpty()) {
                            for (Discussion dt : communityTopics) { %>
                                <div style="background:var(--bg-secondary); padding:10px; border-radius:var(--radius-sm); border:1px solid var(--border);">
                                    <strong style="color:#ffffff;"><%= dt.getTitle() %></strong><br>
                                    <small style="color:var(--text-muted);"><%= dt.getMessage() %></small>
                                    <div style="margin-top:6px; display:flex; justify-content:space-between; align-items:center;">
                                        <small>By: <%= dt.getUserName() %> (<%= dt.getReplyCount() %> replies)</small>
                                        <a href="community?topicId=<%= dt.getId() %>" class="btn small outline" style="padding:2px 8px; font-size:11px;">Join Thread</a>
                                    </div>
                                </div>
                        <%  }
                        } else { %>
                            <p style="color:var(--text-muted);">No community topics posted yet.</p>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
    <% } %>
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
</script>
</body>
</html>
