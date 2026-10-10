package com.greentech.servlet;

import com.greentech.dao.CertificateDAO;
import com.greentech.exception.DatabaseException;
import com.greentech.model.Certificate;
import com.greentech.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Controller for displaying and verifying earned Course Certificates.
 */
@WebServlet("/certificate")
public class CertificateServlet extends HttpServlet {
    private final CertificateDAO certificateDAO = new CertificateDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        req.setAttribute("currentUser", user);

        String code = req.getParameter("code");
        String idParam = req.getParameter("id");

        try {
            Certificate certificate = null;
            if (code != null && !code.trim().isEmpty()) {
                certificate = certificateDAO.getByCode(code.trim());
            } else if (idParam != null && !idParam.trim().isEmpty()) {
                certificate = certificateDAO.findById(Integer.parseInt(idParam.trim()));
            }

            if (certificate != null) {
                req.setAttribute("certificate", certificate);
            } else {
                req.setAttribute("error", "Certificate not found or verification code is invalid.");
            }
        } catch (DatabaseException e) {
            req.setAttribute("error", "Database error looking up certificate: " + e.getMessage());
        }

        req.getRequestDispatcher("/certificate.jsp").forward(req, resp);
    }
}
