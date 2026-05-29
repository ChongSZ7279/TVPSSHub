package bdUtil;

import org.hibernate.SessionFactory;

import org.hibernate.cfg.Configuration;

import com.example.model.UserViewModel;
import com.example.model.ActivityViewModel;
import com.example.model.Feedback;
import com.example.model.School;
import com.example.model.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class HibernateCF {
    static SessionFactory sessionFactory = null;

    private static boolean isMissingOrBrokenTable(SQLException error) {
        return "42S02".equals(error.getSQLState()) || error.getErrorCode() == 1932 || error.getErrorCode() == 1146;
    }

    private static void ensureTableReadable(Statement statement, String tableName, String createSql) throws SQLException {
        statement.executeUpdate(createSql);
        try {
            statement.executeQuery("SELECT 1 FROM " + tableName + " LIMIT 1");
        } catch (SQLException metadataError) {
            if (isMissingOrBrokenTable(metadataError)) {
                statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
                statement.executeUpdate(createSql);
            } else {
                throw metadataError;
            }
        }
    }

    private static void ensureCoreTablesHealthy(Properties properties) {
        String url = properties.getProperty("hibernate.connection.url");
        String username = properties.getProperty("hibernate.connection.username");
        String password = properties.getProperty("hibernate.connection.password");

        String createUsersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(100) NOT NULL UNIQUE, "
                + "password VARCHAR(255) NOT NULL, "
                + "date_of_birth DATE DEFAULT NULL, "
                + "school VARCHAR(100) DEFAULT NULL, "
                + "school_id BIGINT(20) DEFAULT NULL, "
                + "identity_card_number VARCHAR(20) NOT NULL UNIQUE, "
                + "role INT(11) NOT NULL"
                + ")";

        String createSchoolsTableSql = "CREATE TABLE IF NOT EXISTS schools ("
                + "id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "district VARCHAR(100) DEFAULT NULL, "
                + "representative VARCHAR(100) DEFAULT NULL, "
                + "code VARCHAR(50) DEFAULT NULL, "
                + "address VARCHAR(255) DEFAULT NULL, "
                + "postcode VARCHAR(20) DEFAULT NULL, "
                + "city VARCHAR(100) DEFAULT NULL, "
                + "state VARCHAR(100) DEFAULT NULL, "
                + "phone VARCHAR(30) DEFAULT NULL, "
                + "studio TINYINT(1) NOT NULL DEFAULT 0, "
                + "school_recording TINYINT(1) NOT NULL DEFAULT 0, "
                + "upload_youtube TINYINT(1) NOT NULL DEFAULT 0, "
                + "recording TINYINT(1) NOT NULL DEFAULT 0, "
                + "collaborate TINYINT(1) NOT NULL DEFAULT 0, "
                + "greenscreen TINYINT(1) NOT NULL DEFAULT 0"
                + ")";

        String createActivityTableSql = "CREATE TABLE IF NOT EXISTS activity ("
                + "id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "title VARCHAR(255) NOT NULL, "
                + "organizer VARCHAR(255) NOT NULL, "
                + "status VARCHAR(50) DEFAULT NULL, "
                + "date DATE DEFAULT NULL, "
                + "venue VARCHAR(255) DEFAULT NULL, "
                + "district VARCHAR(255) DEFAULT NULL, "
                + "targetLanguage VARCHAR(100) DEFAULT NULL, "
                + "competitionLevel VARCHAR(100) DEFAULT NULL, "
                + "description TEXT DEFAULT NULL, "
                + "fileUpload VARCHAR(255) DEFAULT NULL, "
                + "programDuration INT(11) DEFAULT NULL, "
                + "participants_primary INT(11) DEFAULT 0, "
                + "participants_secondary INT(11) DEFAULT 0, "
                + "participants_open INT(11) DEFAULT 0, "
                + "creator_id BIGINT(20) DEFAULT NULL"
                + ")";

        String createFeedbackTableSql = "CREATE TABLE IF NOT EXISTS feedback ("
                + "feedback_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "activity_id INT(11) NOT NULL, "
                + "user_id BIGINT(20) NOT NULL, "
                + "feedback_text TEXT NOT NULL, "
                + "rating INT(11) DEFAULT NULL, "
                + "date DATE NOT NULL"
                + ")";

        String createResourceTableSql = "CREATE TABLE IF NOT EXISTS resource ("
                + "id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "request VARCHAR(255) NOT NULL, "
                + "school VARCHAR(255) DEFAULT NULL, "
                + "state VARCHAR(100) DEFAULT NULL, "
                + "updated_date DATE DEFAULT NULL, "
                + "level INT(11) DEFAULT NULL, "
                + "type VARCHAR(100) DEFAULT NULL, "
                + "description TEXT DEFAULT NULL, "
                + "reply TEXT DEFAULT NULL"
                + ")";

        try (Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement()) {
            ensureTableReadable(statement, "users", createUsersTableSql);
            ensureTableReadable(statement, "schools", createSchoolsTableSql);
            ensureTableReadable(statement, "activity", createActivityTableSql);
            ensureTableReadable(statement, "feedback", createFeedbackTableSql);
            ensureTableReadable(statement, "resource", createResourceTableSql);
        } catch (Exception e) {
            System.err.println("Failed to ensure core tables are healthy: " + e.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration config = new Configuration();
            config.configure("hibernate.cfg.xml");  // Load configuration file
            ensureCoreTablesHealthy(config.getProperties());
            // Add annotated classes for all the entities used in your project
            config.addAnnotatedClass(UserViewModel.class);
            config.addAnnotatedClass(ActivityViewModel.class);
            config.addAnnotatedClass(Feedback.class);
            config.addAnnotatedClass(School.class);
            config.addAnnotatedClass(Resource.class);

            sessionFactory = config.buildSessionFactory();
        }
        return sessionFactory;
    }
}
