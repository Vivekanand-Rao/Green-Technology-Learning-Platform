## 🌱 Green Technology Learning Platform
An online learning platform designed to promote green technology education, environmental awareness, and sustainable development through online courses, certifications, and community interaction.

## About the Project
The Green Technology Learning Platform provides a digital environment where learners can explore green technology topics, access educational courses, track their learning progress, and engage with a community focused on sustainability.

Administrators can manage users and course content, while instructors can contribute to the learning experience by managing courses and reviewing learner submissions.

## Objectives
Promote awareness of green technology and sustainability.
Provide access to online courses related to environmental technologies.
Support learner progress tracking and course completion.
Enable certifications for completed learning activities.
Encourage interaction through discussions and community features.
Provide administrative tools for managing users and courses.

## User Roles
1. Admin
Manage user accounts.
Manage and maintain course content.
Oversee platform settings and activities.

2. Instructor
Manage assigned courses.
Review learner submissions.
Monitor learner performance and progress.

3. Learner
Browse available courses.
Enroll in courses and access learning materials.
Track learning progress.
Participate in discussions and community activities.
Access certifications when eligible.

## ✨ Key Features
User authentication and role-based access.
Green technology course management.
Course enrollment and learning progress tracking.
Instructor dashboard and learner performance monitoring.
Learner submissions and review workflow.
Community discussions.
Certification support.
Admin dashboard for platform management.

## File Structure
GreenTech Learning Platform/
└── Greentech Project/
    ├── database/
    │   └── greentech_db.sql
    ├── pom.xml
    ├── README.md
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── com/greentech/
        │   │       ├── dao/
        │   │       │   ├── AssignmentDAO.java
        │   │       │   ├── CertificateDAO.java
        │   │       │   ├── CourseDAO.java
        │   │       │   ├── DiscussionDAO.java
        │   │       │   ├── IAssignmentDAO.java
        │   │       │   ├── ICertificateDAO.java
        │   │       │   ├── ICourseDAO.java
        │   │       │   ├── IDiscussionDAO.java
        │   │       │   ├── IGenericDAO.java
        │   │       │   ├── ISystemSettingDAO.java
        │   │       │   ├── IUserDAO.java
        │   │       │   ├── SystemSettingDAO.java
        │   │       │   └── UserDAO.java
        │   │       ├── exception/
        │   │       │   ├── AppException.java
        │   │       │   ├── AuthenticationException.java
        │   │       │   ├── AuthorizationException.java
        │   │       │   ├── DatabaseException.java
        │   │       │   └── ValidationException.java
        │   │       ├── filter/
        │   │       │   └── AuthFilter.java
        │   │       ├── model/
        │   │       │   ├── AdminUser.java
        │   │       │   ├── Assignment.java
        │   │       │   ├── BaseEntity.java
        │   │       │   ├── Certificate.java
        │   │       │   ├── Course.java
        │   │       │   ├── Discussion.java
        │   │       │   ├── Enrollment.java
        │   │       │   ├── EnrollmentStats.java
        │   │       │   ├── InstructorUser.java
        │   │       │   ├── LearnerUser.java
        │   │       │   ├── Submission.java
        │   │       │   ├── SystemSetting.java
        │   │       │   └── User.java
        │   │       ├── service/
        │   │       │   ├── AnalyticsService.java
        │   │       │   └── RecommendationService.java
        │   │       ├── servlet/
        │   │       │   ├── AdminServlet.java
        │   │       │   ├── AssignmentServlet.java
        │   │       │   ├── CertificateServlet.java
        │   │       │   ├── CommunityServlet.java
        │   │       │   ├── CoursesServlet.java
        │   │       │   ├── CreateCourseServlet.java
        │   │       │   ├── DashboardServlet.java
        │   │       │   ├── EnrollServlet.java
        │   │       │   ├── HelloServlet.java
        │   │       │   ├── LoginServlet.java
        │   │       │   ├── LogoutServlet.java
        │   │       │   ├── RegisterServlet.java
        │   │       │   └── TestDBServlet.java
        │   │       └── util/
        │   │           ├── DBConnection.java
        │   │           ├── PasswordUtil.java
        │   │           ├── ServiceResult.java
        │   │           └── ValidationUtil.java
        │   ├── resources/
        │   │   └── db.properties
        │   ├── webapp/
        │   │   ├── WEB-INF/
        │   │   │   └── web.xml
        │   │   ├── css/
        │   │   │   └── style.css
        │   │   ├── js/
        │   │   │   └── app.js
        │   │   ├── admin.jsp
        │   │   ├── certificate.jsp
        │   │   ├── community.jsp
        │   │   ├── courses.jsp
        │   │   ├── dashboard.jsp
        │   │   ├── index.html
        │   │   ├── login.html
        │   │   ├── login.jsp
        │   │   ├── register.html
        │   │   └── register.jsp
        └── test/
            └── java/


## Database Setup

1. Start your local MySQL service.
2. Execute `database/greentech_db.sql` in MySQL Workbench or the MySQL CLI to create `greentech_db` with all tables, constraints, and seed data.
3. Verify or edit your MySQL credentials in `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/greentech_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.user=root
   db.password="Enter your pass"
   ```

---

## Build & Deployment
Build the WAR using Maven:
```powershell
mvn clean package
```
Deploy the resulting WAR:
```text
target/GreenTechnologyLearningPlatform.war -> Tomcat 10.1+ /webapps/
```

'''Tomcat/bin/startup.bat(double click to run)'''

Open in your browser:
```text
http://localhost:8080/GreenTechnologyLearningPlatform/
```

---

## Pre-Seeded Demonstration Accounts
| Role | Email | Password |
|---|---|---|
| **Administrator** | `admin@greenlearn.com` | `admin123` |
| **Instructor** | `instructor@greenlearn.com` | `instructor123` |
| **Learner** | `learner@greenlearn.com` | `learner123` |
*(The login page includes single-click demo autofill buttons for convenience during evaluation).*
