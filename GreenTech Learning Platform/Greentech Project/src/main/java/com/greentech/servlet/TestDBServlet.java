package com.greentech.servlet;

import com.greentech.util.DBConnection;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test-db")
public class TestDBServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String result = DBConnection.testConnection();
        boolean isSuccess = result.startsWith("SUCCESS");

        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Database Diagnostic | GreenLearn</title>");
            out.println("<link rel='stylesheet' href='css/style.css'>");
            out.println("<style>");
            out.println(".diag-box { max-width: 600px; margin: 50px auto; padding: 25px; border-radius: 12px; background: #16201b; border: 1px solid #273930; color: #e4ece7; }");
            out.println(".status-pill { display: inline-block; padding: 6px 14px; border-radius: 999px; font-weight: bold; font-size: 14px; margin-bottom: 15px; }");
            out.println(".status-ok { background: #1e3a2b; color: #4ade80; border: 1px solid #22c55e; }");
            out.println(".status-err { background: #3b181b; color: #f87171; border: 1px solid #ef4444; }");
            out.println(".code-pre { background: #0c120f; padding: 12px; border-radius: 8px; font-family: monospace; overflow-x: auto; color: #a3e635; margin: 15px 0; }");
            out.println("</style></head><body>");
            out.println("<header class='navbar'><a class='brand' href='index.html'>🌱 GreenLearn</a><nav><a href='index.html'>Home</a><a href='login.jsp'>Login</a></nav></header>");
            out.println("<main><div class='diag-box'>");
            out.println("<h2>Database Diagnostic Status</h2>");
            if (isSuccess) {
                out.println("<div class='status-pill status-ok'>✓ Database Connected</div>");
            } else {
                out.println("<div class='status-pill status-err'>✗ Connection Failed</div>");
            }
            out.println("<p><strong>Current JDBC URL:</strong> " + DBConnection.getUrl() + "</p>");
            out.println("<p><strong>DB User:</strong> " + DBConnection.getUser() + "</p>");
            out.println("<div class='code-pre'>" + result + "</div>");

            if (!isSuccess) {
                out.println("<div class='alert' style='background:#2a1b1b; border-color:#ef4444; color:#fca5a5;'>");
                out.println("<strong>Troubleshooting Tips:</strong><br>");
                out.println("1. Verify MySQL Service is running.<br>");
                out.println("2. Confirm password in <code>src/main/resources/db.properties</code>.<br>");
                out.println("3. Ensure database <code>greentech_db</code> is created via MySQL Workbench.");
                out.println("</div>");
            } else {
                out.println("<p style='color:#4ade80;'>All database tables and connections are operational!</p>");
                out.println("<a class='btn' href='courses'>View Courses</a> <a class='btn outline' href='login.jsp'>Login</a>");
            }

            out.println("<hr style='border:0; border-top:1px solid #273930; margin:20px 0;'>");
            out.println("<h3>Test New Credentials Instantly:</h3>");
            out.println("<form method='post' action='test-db'>");
            out.println("<label>JDBC URL<input name='url' value='" + DBConnection.getUrl() + "' required></label>");
            out.println("<label>User<input name='user' value='" + DBConnection.getUser() + "' required></label>");
            out.println("<label>Password<input type='password' name='password' placeholder='Enter MySQL password'></label>");
            out.println("<button class='btn full' type='submit'>Update & Re-Test</button>");
            out.println("</form>");

            out.println("</div></main></body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getParameter("url");
        String user = req.getParameter("user");
        String pass = req.getParameter("password");
        DBConnection.setCredentials(url, user, pass);
        resp.sendRedirect(req.getContextPath() + "/test-db");
    }
}
