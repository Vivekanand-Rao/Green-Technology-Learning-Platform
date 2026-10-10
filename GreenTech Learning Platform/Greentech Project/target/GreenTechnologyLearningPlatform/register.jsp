<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
    if (error == null && "true".equals(request.getParameter("error"))) {
        error = "Registration failed. Please check form values.";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Account | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn</a>
    <nav>
        <a href="index.html">Home</a>
        <a href="courses">Courses</a>
        <a href="community">Community</a>
        <a href="login.jsp">Login</a>
        <a href="test-db">DB Status</a>
    </nav>
</header>

<main class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">JOIN THE COMMUNITY</span>
        <h1>Create an Account</h1>
        <p>Register as a learner or course instructor to begin.</p>

        <% if (error != null) { %>
            <div class="alert error">
                <span>✗</span> <%= error %>
            </div>
        <% } %>

        <form method="post" action="register">
            <label>
                Full Name
                <input type="text" name="name" placeholder="e.g. Maya Patel" required>
            </label>
            <label>
                Email Address
                <input type="email" name="email" placeholder="e.g. maya@example.com" required>
            </label>
            <label>
                Password
                <input type="password" name="password" minlength="6" placeholder="Minimum 6 characters" required>
            </label>
            <label>
                Account Role
                <select name="role">
                    <option value="LEARNER">Learner (Enroll in courses & earn certifications)</option>
                    <option value="INSTRUCTOR">Instructor (Design & publish courses)</option>
                </select>
                <small style="color:var(--text-muted); display:block; margin-top:4px;">
                    🛡️ Administrator accounts are protected and can only be provisioned by existing platform administrators.
                </small>
            </label>
            <button class="btn full" type="submit" style="margin-top:12px;">Complete Registration</button>
        </form>

        <p style="margin-top:20px; text-align:center; font-size:0.9rem;">
            Already have an account? <a href="login.jsp" style="color:var(--primary); font-weight:700;">Sign in here</a>
        </p>
    </div>
</main>

<footer>
    © 2026 GreenLearn Platform — Comprehensive Green Technology Academic System
</footer>
</body>
</html>
