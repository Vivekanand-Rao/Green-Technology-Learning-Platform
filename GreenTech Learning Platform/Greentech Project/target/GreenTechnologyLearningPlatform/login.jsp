<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
    if (error == null) {
        String errParam = request.getParameter("error");
        if ("invalid".equals(errParam)) error = "Invalid email or password.";
        else if ("db".equals(errParam)) error = "Database connection error. Please verify MySQL configuration.";
        else if ("auth".equals(errParam)) error = "Please sign in to access that platform feature.";
    }
    boolean registered = "true".equals(request.getParameter("registered"));
    boolean loggedOut = "true".equals(request.getParameter("logout"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn</a>
    <nav>
        <a href="index.html">Home</a>
        <a href="courses">Courses</a>
        <a href="community">Community</a>
        <a href="register.jsp">Register</a>
        <a href="test-db">DB Status</a>
    </nav>
</header>

<main class="auth-wrap">
    <div class="auth-card">
        <span class="eyebrow">AUTHENTICATION</span>
        <h1>Welcome back</h1>
        <p>Access your green technology learning workspace.</p>

        <% if (registered) { %>
            <div class="alert success">
                <span>✓</span> Account registered successfully! Please log in below.
            </div>
        <% } %>

        <% if (loggedOut) { %>
            <div class="alert info">
                <span>ℹ</span> You have been securely logged out.
            </div>
        <% } %>

        <% if (error != null) { %>
            <div class="alert error">
                <span>✗</span> <%= error %>
            </div>
        <% } %>

        <form method="post" action="login">
            <label>
                Email Address
                <input type="email" id="emailInput" name="email" placeholder="e.g. learner@greenlearn.com" required>
            </label>
            <label>
                Password
                <input type="password" id="passInput" name="password" placeholder="••••••••" required>
            </label>
            <button class="btn full" type="submit" style="margin-top:8px;">Sign In to Platform</button>
        </form>

        <!-- Quick Demo Account Switcher for Instant Evaluation Testing -->
        <div style="margin-top:20px; padding:14px; background:var(--bg-secondary); border:1px solid var(--border); border-radius:var(--radius-sm);">
            <strong style="color:#ffffff; font-size:0.88rem; display:block; margin-bottom:8px;">Quick-Fill Demo Credentials:</strong>
            <div style="display:flex; flex-direction:column; gap:6px;">
                <button type="button" class="btn small secondary" onclick="fillCreds('admin@greenlearn.com', 'admin123')">
                    👑 Admin: admin@greenlearn.com / admin123
                </button>
                <button type="button" class="btn small secondary" onclick="fillCreds('instructor@greenlearn.com', 'instructor123')">
                    🎓 Instructor: instructor@greenlearn.com / instructor123
                </button>
                <button type="button" class="btn small secondary" onclick="fillCreds('learner@greenlearn.com', 'learner123')">
                    🌱 Learner: learner@greenlearn.com / learner123
                </button>
            </div>
        </div>

        <p style="margin-top:20px; text-align:center; font-size:0.9rem;">
            Don't have an account yet? <a href="register.jsp" style="color:var(--primary); font-weight:700;">Create one now</a>
        </p>
    </div>
</main>

<footer>
    © 2026 GreenLearn Platform — Comprehensive Green Technology Academic System
</footer>

<script>
function fillCreds(email, pass) {
    document.getElementById('emailInput').value = email;
    document.getElementById('passInput').value = pass;
}
</script>
</body>
</html>
