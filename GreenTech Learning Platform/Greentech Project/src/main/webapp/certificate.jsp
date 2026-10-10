<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.greentech.model.Certificate" %>
<%@ page import="com.greentech.model.User" %>
<%
    Certificate cert = (Certificate) request.getAttribute("certificate");
    String error = (String) request.getAttribute("error");
    User currentUser = (User) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Official Certificate of Completion | GreenLearn</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<header class="navbar">
    <a class="brand" href="index.html">🌱 GreenLearn <span>Certifications</span></a>
    <nav>
        <a href="index.html">Home</a>
        <a href="courses">Courses</a>
        <% if (currentUser != null) { %>
            <a href="<%= currentUser.getDashboardUrl() %>">Dashboard</a>
        <% } %>
        <a class="btn small outline" href="javascript:window.print()">Print / Save PDF</a>
    </nav>
</header>

<main class="section" style="max-width:850px; margin:0 auto;">
    <% if (error != null) { %>
        <div class="alert error">
            <%= error %>
        </div>
        <div style="text-align:center; margin-top:20px;">
            <a class="btn" href="dashboard">Return to Dashboard</a>
        </div>
    <% } else if (cert != null) { %>
        <div class="cert-frame">
            <div class="seal">🌱</div>
            <span class="eyebrow" style="color:#86efac; font-size:0.85rem; letter-spacing:3px;">GREEN TECHNOLOGY ACADEMY</span>
            <h1 style="font-size:2.4rem; margin:10px 0; color:#ffffff;">Certificate of Completion</h1>
            <p style="font-size:1.05rem; color:#d1e7dd; margin-bottom:20px;">This officially certifies that</p>
            
            <h2 style="font-size:2.2rem; color:#4ade80; text-decoration:underline; text-decoration-color:#16a34a; margin-bottom:14px;">
                <%= cert.getLearnerName() %>
            </h2>
            
            <p style="font-size:1.05rem; color:#d1e7dd; max-width:600px; margin:0 auto 20px;">
                has successfully completed all modules, practical assignments, and rigorous curriculum requirements for
            </p>
            
            <h3 style="font-size:1.6rem; color:#ffffff; margin-bottom:24px;">
                <%= cert.getCourseTitle() %>
            </h3>
            
            <div style="display:flex; justify-content:space-around; align-items:center; border-top:1px solid #234d36; padding-top:24px; margin-top:30px; font-size:0.88rem; color:#9cbdae;">
                <div>
                    <strong>Date of Issuance:</strong><br>
                    <span><%= cert.getIssueDate() != null ? cert.getIssueDate().toString().substring(0, 10) : "2026-10-09" %></span>
                </div>
                <div>
                    <strong>Certificate Verification ID:</strong><br>
                    <code style="color:#86efac; font-size:1rem;"><%= cert.getCertificateCode() %></code>
                </div>
                <div>
                    <strong>Verification Authority:</strong><br>
                    <span>GreenLearn Platform</span>
                </div>
            </div>
        </div>

        <div style="text-align:center; margin-top:30px;">
            <a class="btn outline" href="dashboard">&larr; Back to Dashboard</a>
            <button class="btn" onclick="window.print()" style="margin-left:10px;">Download / Print Certificate</button>
        </div>
    <% } %>
</main>

<footer>
    © 2026 GreenLearn Platform — Verified Green Technology Credential
</footer>
</body>
</html>
