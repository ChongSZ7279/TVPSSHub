# TVPSSHub

TVPSSHub is a Spring MVC + Hibernate web app with MySQL.

## Quick Start

### 1. Prerequisites

- JDK 11+ (tested with 21)
- Maven 3.8+
- MySQL 5.7 or 8.x

This project uses javax.servlet (Servlet 4), so use Jetty 10 or Tomcat 9.

### 2. Setup Database

Run the SQL script from project root:

```bash
mysql -u root -p < src/main/webapp/TVPSShub.sql
```

Then check database credentials in src/main/resources/hibernate.cfg.xml.

### 3. Build

```bash
mvn clean package -DskipTests
```

Output: target/TVPSSHub.war

### 4. Run (Recommended)

```bash
mvn jetty:run
```

Open: http://localhost:8081/TVPSSHub/

Change port if needed:

```bash
mvn jetty:run "-Djetty.http.port=9090"
```

### 5. Verify

- Login: http://localhost:8081/TVPSSHub/user/login
- Register: http://localhost:8081/TVPSSHub/user/register

## Common Issues

- Port already in use: run with a different jetty.http.port.
- Unknown database TVPSShub: rerun src/main/webapp/TVPSShub.sql.
- Access denied for MySQL user: update hibernate.cfg.xml credentials.
- Running on Tomcat 10+ or Jetty 11+: switch to Tomcat 9 or Jetty 10.
