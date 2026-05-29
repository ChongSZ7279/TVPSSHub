# TVPSSHub

TVPSSHub is a **Spring Boot** web application (Thymeleaf, Spring Security, Hibernate) backed by **MySQL**.

## Project layout

```
src/main/java/com/example/
  TvpsShubApplication.java     Application entry point
  controller/                  Web controllers
  config/                      Security & Hibernate config
  dao/                         Data access (Hibernate sessions)
  model/                       Entity / view models
src/main/java/bdUtil/
  HibernateCF.java             SessionFactory helper (injected by Spring)
src/main/resources/
  application.properties       Server port & context path
  hibernate.cfg.xml            Database connection settings
  templates/                   Thymeleaf HTML views
  static/resources/            CSS and images (URL: /resources/...)
  TVPSShub.sql                 Database schema & seed data
```

## Prerequisites

- JDK 11 or newer
- Maven 3.8+
- MySQL 5.7 or 8.x

## Database setup

### phpMyAdmin (recommended if `mysql` is not on PATH)

1. Start MySQL (XAMPP Control Panel → **Start** MySQL).
2. Open http://localhost/phpmyadmin
3. Click database **`tvpsshub`** in the left sidebar (create it first with **New** → name `TVPSShub` if missing).
4. Open the **SQL** tab.
5. **Do not use** `DROP DATABASE TVPSShub` — it often fails with:
   `#1010 - can't rmdir '.\tvpsshub', errno: 41 Directory not empty`
6. Instead: **File → Open** (or paste) → run:
   `src/main/resources/db/reset-tvpsshub-phpmyadmin.sql`
7. Click **Go** / execute. All tables are recreated (existing data is deleted).

For only a broken `feedback` table, run `src/main/resources/db/repair-feedback.sql` in the same SQL tab.

### Command line (if `mysql` is on PATH)

**PowerShell:**

```powershell
Get-Content src\main\resources\db\reset-tvpsshub-phpmyadmin.sql | mysql -u root -p
```

**CMD:**

```cmd
mysql -u root -p < src\main\resources\db\reset-tvpsshub-phpmyadmin.sql
```

Fresh install (new database only): `src\main\resources\TVPSShub.sql`

Edit `src/main/resources/hibernate.cfg.xml` if your MySQL username or password differs from `root` / empty password.

## Build and run

```powershell
cd C:\UTM\Semester8
mvn clean package -DskipTests
mvn spring-boot:run
```

Or run the JAR:

```powershell
java -jar target\TVPSSHub.jar
```

Open in a browser:

| Page     | URL |
|----------|-----|
| Home     | http://localhost:8081/TVPSSHub/ |
| Login    | http://localhost:8081/TVPSSHub/user/login |
| Register | http://localhost:8081/TVPSSHub/user/register |

Change port:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=9090"
```

## Common issues

| Symptom | Cause | Fix |
|---------|--------|-----|
| `The '<' operator is reserved` | PowerShell does not support `< file.sql` | Use `Get-Content ... \| mysql` or CMD |
| `not enough space on the disk` | Drive full (often `C:` or `.m2`) | Free disk space; delete `target\` if needed |
| `mysql-connector-java ... version is missing` | Old artifact not in Spring Boot BOM | Use `com.mysql:mysql-connector-j` in `pom.xml` |
| `Configuration is ambiguous` | Hibernate vs Spring class name clash | Use `org.hibernate.cfg.Configuration` explicitly in `HibernateConfig` |
| Unknown database `TVPSShub` | DB not created | Run `TVPSShub.sql` |
| Access denied for user | Wrong MySQL credentials | Update `hibernate.cfg.xml` |
| `feedback' doesn't exist in engine` | Corrupt InnoDB `feedback` table | Run `db/repair-feedback.sql` or full `db/reset-tvpsshub-phpmyadmin.sql` |
| `#1010 can't rmdir tvpsshub` (phpMyAdmin) | `DROP DATABASE` blocked by leftover files | Use `db/reset-tvpsshub-phpmyadmin.sql` (no DROP DATABASE) |
| `mysql` not recognized | MySQL CLI not on PATH | Use phpMyAdmin steps above |
| `Connection refused` | MySQL not running | Start MySQL (XAMPP/service), then retry |

## Tech stack

- Spring Boot 2.7
- Spring MVC + Thymeleaf
- Spring Security
- Hibernate 5 (manual `SessionFactory`, not JPA)
- MySQL
