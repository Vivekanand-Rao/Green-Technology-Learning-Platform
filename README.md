# 🌱 Green Technology Learning Platform

**An Online Learning Management System for Green Technology and Sustainable Development**

The Green Technology Learning Platform is a web-based learning management system designed to promote environmental awareness, green technology education, and sustainable development. It enables learners to explore courses, track their progress, participate in community discussions, and access certificates, while instructors and administrators manage learning activities and platform resources.

---

## 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Objectives](#-project-objectives)
- [User Roles](#-user-roles)
- [Key Features](#-key-features)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Database Setup](#-database-setup)
- [Installation and Configuration](#-installation-and-configuration)
- [Build and Deployment](#-build-and-deployment)
- [Demo Accounts](#-demo-accounts)
- [Future Enhancements](#-future-enhancements)

---

## 🌍 About the Project

Environmental sustainability and green technologies are becoming increasingly important. This project provides a digital platform where users can learn about sustainable practices and environmental technologies through structured online courses.

The system follows a role-based architecture with dedicated functionality for administrators, instructors, and learners.

## 🎯 Project Objectives

- Promote awareness of green technology and environmental sustainability.
- Provide accessible online learning resources related to environmental technologies.
- Enable course enrollment and learning progress tracking.
- Support assignments, learner submissions, and instructor reviews.
- Facilitate community discussions and knowledge sharing.
- Provide certificate management for eligible learners.
- Simplify user, course, and system management through administrative tools.

## 👥 User Roles

### 1. Administrator
- Manage user accounts and access.
- Oversee course content and platform activities.
- Manage system settings.
- Monitor platform operations.

### 2. Instructor
- Manage assigned courses.
- Review learner submissions.
- Monitor learner performance and progress.
- Support course-related learning activities.

### 3. Learner
- Browse available green technology courses.
- Enroll in courses and access learning materials.
- Track learning progress.
- Submit assignments where supported.
- Participate in community discussions.
- Access certificates when eligible.

## ✨ Key Features

| Feature | Description |
|---|---|
| 🔐 Authentication | User registration, login, and role-based access |
| 📚 Course Management | Organize and manage green technology courses |
| 🎓 Course Enrollment | Allow learners to enroll in available courses |
| 📊 Progress Tracking | Monitor learning progress and enrollment statistics |
| 📝 Assignment Management | Support learner submissions and instructor reviews |
| 💬 Community | Enable discussions and knowledge sharing |
| 🏆 Certificates | Support certificate management for eligible learners |
| 🛡️ Admin Dashboard | Provide administrative user and platform management |
| ⚙️ System Settings | Support platform configuration |
| 🔒 Security Utilities | Authentication filters, password utilities, and validation components |

*Feature availability depends on the implemented application logic and database configuration.*

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend application logic |
| Java Servlets | Handle HTTP requests and application workflows |
| JSP | Generate dynamic web pages |
| HTML5 | Structure web content |
| CSS3 | Styling and responsive presentation |
| JavaScript | Client-side interactions |
| MySQL | Relational database management |
| JDBC | Database connectivity |
| Maven | Dependency management and project build |
| Apache Tomcat 10.1+ | Servlet and JSP application server |
| Git and GitHub | Version control and source code hosting |

## 📁 Project Structure

```text
GreenTech_Learning_Platform/
└── Greentech Project/
    ├── database/
    │   └── greentech_db.sql
    │
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/
    │   │   │       └── greentech/
    │   │   │           ├── dao/
    │   │   │           │   ├── AssignmentDAO.java
    │   │   │           │   ├── CertificateDAO.java
    │   │   │           │   ├── CourseDAO.java
    │   │   │           │   ├── DiscussionDAO.java
    │   │   │           │   ├── SystemSettingDAO.java
    │   │   │           │   └── UserDAO.java
    │   │   │           │
    │   │   │           ├── exception/
    │   │   │           │   ├── AppException.java
    │   │   │           │   ├── AuthenticationException.java
    │   │   │           │   ├── AuthorizationException.java
    │   │   │           │   ├── DatabaseException.java
    │   │   │           │   └── ValidationException.java
    │   │   │           │
    │   │   │           ├── filter/
    │   │   │           │   └── AuthFilter.java
    │   │   │           │
    │   │   │           ├── model/
    │   │   │           │   ├── AdminUser.java
    │   │   │           │   ├── Assignment.java
    │   │   │           │   ├── BaseEntity.java
    │   │   │           │   ├── Certificate.java
    │   │   │           │   ├── Course.java
    │   │   │           │   ├── Discussion.java
    │   │   │           │   ├── Enrollment.java
    │   │   │           │   ├── EnrollmentStats.java
    │   │   │           │   ├── InstructorUser.java
    │   │   │           │   ├── LearnerUser.java
    │   │   │           │   ├── Submission.java
    │   │   │           │   ├── SystemSetting.java
    │   │   │           │   └── User.java
    │   │   │           │
    │   │   │           ├── service/
    │   │   │           │   ├── AnalyticsService.java
    │   │   │           │   └── RecommendationService.java
    │   │   │           │
    │   │   │           ├── servlet/
    │   │   │           │   ├── AdminServlet.java
    │   │   │           │   ├── AssignmentServlet.java
    │   │   │           │   ├── CertificateServlet.java
    │   │   │           │   ├── CommunityServlet.java
    │   │   │           │   ├── CoursesServlet.java
    │   │   │           │   ├── CreateCourseServlet.java
    │   │   │           │   ├── DashboardServlet.java
    │   │   │           │   ├── EnrollServlet.java
    │   │   │           │   ├── HelloServlet.java
    │   │   │           │   ├── LoginServlet.java
    │   │   │           │   ├── LogoutServlet.java
    │   │   │           │   ├── RegisterServlet.java
    │   │   │           │   └── TestDBServlet.java
    │   │   │           │
    │   │   │           └── util/
    │   │   │               ├── DBConnection.java
    │   │   │               ├── PasswordUtil.java
    │   │   │               ├── ServiceResult.java
    │   │   │               └── ValidationUtil.java
    │   │   │
    │   │   ├── resources/
    │   │   │   └── db.properties
    │   │   │
    │   │   └── webapp/
    │   │       ├── WEB-INF/
    │   │       │   └── web.xml
    │   │       ├── css/
    │   │       │   └── style.css
    │   │       ├── js/
    │   │       │   └── app.js
    │   │       ├── admin.jsp
    │   │       ├── certificate.jsp
    │   │       ├── community.jsp
    │   │       ├── courses.jsp
    │   │       ├── dashboard.jsp
    │   │       ├── index.html
    │   │       ├── login.html
    │   │       ├── login.jsp
    │   │       ├── register.html
    │   │       └── register.jsp
    │   │
    │   └── test/
    │       └── java/
    │
    ├── pom.xml
    └── README.md
```

*Note: The tree highlights the main application components. DAO interfaces such as `ICourseDAO`, `IUserDAO`, and other interfaces are also part of the DAO package.*

## 💻 Prerequisites

Install the following software before running the application:

- **JDK:** A Java version compatible with the project's Maven configuration.
- **Apache Maven:** For dependency management and packaging.
- **MySQL Server:** For storing application data.
- **Apache Tomcat 10.1+:** For deploying the web application.
- **Git:** Optional, for version control.

Verify your installations:

```bash
java -version
mvn -version
git --version
```

## 🗄️ Database Setup

### Step 1: Start MySQL

Ensure that your MySQL server is running.

### Step 2: Create the database

Execute the SQL script provided in the project:

```text
database/greentech_db.sql
```

You can import it using MySQL Workbench or the MySQL command-line client.

Example:

```bash
mysql -u root -p < database/greentech_db.sql
```

Run this command from the directory containing the `database` folder. If the SQL script is inside the nested project directory, change to that directory first.

### Step 3: Configure database credentials

Open:

```text
src/main/resources/db.properties
```

Configure the values for your local MySQL instance:

```properties
db.url=jdbc:mysql://localhost:3306/greentech_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

Replace `YOUR_MYSQL_PASSWORD` with your local MySQL password.

**Security note:** Never commit real database passwords, API keys, or other secrets to a public GitHub repository.

### Step 4: Verify the database

Confirm that the `greentech_db` database and its required tables exist before starting the application.

## ⚙️ Installation and Configuration

### 1. Clone the repository

```bash
git clone https://github.com/Vivekanand-Rao/Green-Technology-Learning-Platform.git
```

### 2. Navigate to the Maven project

```bash
cd Green-Technology-Learning-Platform
```

If `pom.xml` is inside the `Greentech Project` subfolder, navigate into that folder:

```bash
cd "Greentech Project"
```

Run Maven commands from the directory containing `pom.xml`.

### 3. Configure the database

Complete the [Database Setup](#-database-setup) steps and verify your credentials.

## 🚀 Build and Deployment

### Step 1: Build the application

From the directory containing `pom.xml`, run:

```bash
mvn clean package
```

Maven will compile the project, resolve dependencies, and package the application if the build succeeds.

### Step 2: Locate the WAR file

The expected deployment artifact is:

```text
target/GreenTechnologyLearningPlatform.war
```

The actual WAR filename depends on the Maven configuration in `pom.xml`.

### Step 3: Deploy to Apache Tomcat

Copy the generated WAR file into Tomcat's `webapps` directory.

For example, on Windows:

```text
apache-tomcat-10.1.x/
└── webapps/
    └── GreenTechnologyLearningPlatform.war
```

Start Tomcat using `bin/startup.bat` on Windows, or use the startup script appropriate for your operating system.

### Step 4: Open the application

If Tomcat is running on the default port `8080`, open:

```text
http://localhost:8080/GreenTechnologyLearningPlatform/
```

The context path depends on the deployed WAR filename and your Tomcat configuration.

## 🔑 Demo Accounts

The following demonstration accounts are listed for evaluation, provided that the SQL seed data creates these accounts and their passwords are configured as shown.

| Role | Email | Password |
|---|---|---|
| Administrator | `admin@greenlearn.com` | `admin123` |
| Instructor | `instructor@greenlearn.com` | `instructor123` |
| Learner | `learner@greenlearn.com` | `learner123` |

The login page may also provide demo-account autofill buttons.

**Important:** These are demonstration credentials. Verify them against your database seed data before relying on them. Do not use these passwords for a production deployment.


**Developed to support green technology education, environmental awareness, and sustainable development. 🌱**

