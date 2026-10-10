# Green Technology Learning Platform (GreenLearn)


---

### 1. Problem Understanding & Solution Design
- **Multi-Role User Architecture**: Implemented distinct user workflows for **Admin**, **Instructor**, and **Learner** matching all PRD inputs, outputs, and functionalities.
- **Modular Layered Architecture**: Strict separation of concerns across packages:
  - `com.greentech.model`: Domain entities & DTOs extending `BaseEntity`
  - `com.greentech.dao`: Generic and specialized Data Access Objects (`IGenericDAO`, `IUserDAO`, `ICourseDAO`, `IAssignmentDAO`, `IDiscussionDAO`, `ISystemSettingDAO`, `ICertificateDAO`)
  - `com.greentech.service`: Concurrency-safe analytics and recommendation services (`AnalyticsService`, `RecommendationService`)
  - `com.greentech.filter`: Server-side role-based access control (`AuthFilter`)
  - `com.greentech.servlet`: Jakarta HTTP Servlets for controllers
  - `com.greentech.util`: Reusable utilities (`DBConnection`, `PasswordUtil`, `ValidationUtil`, `ServiceResult<T>`)
  - `com.greentech.exception`: Domain exception hierarchy (`AppException`, `ValidationException`, `AuthenticationException`, `AuthorizationException`, `DatabaseException`)
- **Dashboard Interfaces**:
  - **Admin Dashboard** (`admin.jsp`): User management (CRUD), course content approvals, system configuration settings, graphical enrollment & completion stats, and community moderation.
  - **Instructor Dashboard** (`dashboard.jsp`): Course creation & management, coursework assignment creation & grading panel with numerical scores & feedback, learner interactions, and course performance telemetry.
  - **Learner Dashboard** (`dashboard.jsp`): Course enrollment tracking with progress controls (0-100%), assignment submission & feedback history, community discussions, official certificate issuance, and personalized course recommendations.

### 2. Core Java Concepts
- **Object-Oriented Programming (OOP)**:
  - **Inheritance & Polymorphism**: `BaseEntity` extended by `User`, `Course`, `Enrollment`, `Assignment`, `Submission`, `Discussion`, and `Certificate`. Specialized user classes `AdminUser`, `InstructorUser`, and `LearnerUser` override role permissions polymorphically (`canCreateCourse()`, `canGradeAssignments()`, `canEnrollInCourses()`).
  - **Encapsulation**: Private/protected state fields, getters/setters, immutable lists where appropriate, and factory methods (`User.createSpecialized()`).
  - **Abstraction & Interfaces**: Generic base `IGenericDAO<T, ID>` and interface contracts for all DAO layers decoupling data persistence from controllers.
- **Custom Exception Handling**: Custom typed exceptions (`ValidationException`, `AuthenticationException`, `AuthorizationException`, `DatabaseException`) preventing unhandled raw runtime errors and shielding internal details.
- **Collections & Generics**: Extensive use of Java Collections (`List`, `Map`, `Set`, `ConcurrentHashMap`), Java Streams API (`filter`, `map`, `distinct`, `sorted`, `collect`), and Generic class wrapper `ServiceResult<T>`.
- **Multithreading & Synchronization**: `AnalyticsService` singleton utilizes double-checked locking (`volatile`, `synchronized`), thread-safe `ConcurrentHashMap`, and `AtomicInteger` for thread-safe telemetry caching across concurrent user sessions.

### 3. Database Integration (JDBC)
- **Robust Relational Schema**: 8 normalized tables in `database/greentech_db.sql`: `users`, `system_settings`, `courses`, `enrollments`, `assignments`, `submissions`, `discussions`, and `certificates` with foreign keys and cascade rules.
- **Zero SQL Injection**: 100% of parameterized SQL statements use `PreparedStatement` with bound parameters.
- **Resource Management**: Strict `try-with-resources` statements on all `Connection`, `PreparedStatement`, and `ResultSet` instances ensuring zero connection leaks.
- **External Configuration**: Database credentials externalized in `src/main/resources/db.properties` with fallback support for system properties and environment variables.
- **Password Security**: Cryptographic salted SHA-256 password hashing with random salt generation in `PasswordUtil`, plus backward-compatible verification for pre-seeded demonstration accounts.

### 4. Servlets & Web Integration
- **Jakarta Servlets 6.0**: Annotation-driven servlets (`@WebServlet`) handling GET and POST actions with input sanitization and validation.
- **Server-Side Authorization**: `AuthFilter` (`@WebFilter`) verifies user authentication and enforces role permissions, blocking privilege escalation and securing `/admin` routes.
- **Registration Security**: `RegisterServlet` prevents unauthorized public registration of administrator accounts; administrator accounts can only be created by an authenticated administrator.
- **Session Management**: Secure session lifecycle, invalidation upon logout (`session.invalidate()`), session fixation mitigation upon login, and HTTP-only cookie configuration in `web.xml`.
- **Rich Responsive UI**: Semantic HTML5, CSS3 design system with custom properties, glassmorphism, responsive tables, interactive progress controls, and visual SVG/CSS graphical chart bars for enrollment statistics.

---

## Technology Stack
- Java 17+
- Jakarta Servlets 6.0 (Jakarta EE 10)
- Apache Tomcat 10.1+
- Apache Maven
- MySQL 8.0+
- JDBC (mysql-connector-j)
- HTML5, CSS3, JavaScript

---

## Directory Structure
```text
Greentech Project/
├── pom.xml
├── README.md
├── database/
│   └── greentech_db.sql
└── src/main/
    ├── java/com/greentech/
    │   ├── dao/
    │   │   ├── IGenericDAO.java
    │   │   ├── IUserDAO.java & UserDAO.java
    │   │   ├── ICourseDAO.java & CourseDAO.java
    │   │   ├── IAssignmentDAO.java & AssignmentDAO.java
    │   │   ├── IDiscussionDAO.java & DiscussionDAO.java
    │   │   ├── ISystemSettingDAO.java & SystemSettingDAO.java
    │   │   └── ICertificateDAO.java & CertificateDAO.java
    │   ├── exception/
    │   │   ├── AppException.java
    │   │   ├── ValidationException.java
    │   │   ├── AuthenticationException.java
    │   │   ├── AuthorizationException.java
    │   │   └── DatabaseException.java
    │   ├── filter/
    │   │   └── AuthFilter.java
    │   ├── model/
    │   │   ├── BaseEntity.java
    │   │   ├── User.java, AdminUser.java, InstructorUser.java, LearnerUser.java
    │   │   ├── Course.java, Enrollment.java, EnrollmentStats.java
    │   │   ├── Assignment.java, Submission.java
    │   │   ├── Discussion.java, Certificate.java, SystemSetting.java
    │   ├── service/
    │   │   ├── AnalyticsService.java
    │   │   └── RecommendationService.java
    │   ├── servlet/
    │   │   ├── AdminServlet.java
    │   │   ├── DashboardServlet.java
    │   │   ├── CoursesServlet.java
    │   │   ├── CreateCourseServlet.java
    │   │   ├── EnrollServlet.java
    │   │   ├── AssignmentServlet.java
    │   │   ├── CommunityServlet.java
    │   │   ├── CertificateServlet.java
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── LogoutServlet.java
    │   │   └── TestDBServlet.java
    │   └── util/
    │       ├── DBConnection.java
    │       ├── PasswordUtil.java
    │       ├── ValidationUtil.java
    │       └── ServiceResult.java
    ├── resources/
    │   └── db.properties
    └── webapp/
        ├── css/style.css
        ├── js/app.js
        ├── WEB-INF/web.xml
        ├── index.html
        ├── login.jsp
        ├── register.jsp
        ├── admin.jsp
        ├── dashboard.jsp
        ├── courses.jsp
        ├── community.jsp
        └── certificate.jsp
```

---

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
