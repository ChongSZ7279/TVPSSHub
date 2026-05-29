package com.example.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.model.ActivityViewModel;
import com.example.model.Feedback;
import com.example.model.Resource;
import com.example.model.School;
import com.example.model.UserViewModel;

@Configuration
public class HibernateConfig {

    @Bean
    public SessionFactory sessionFactory() {
        // #region agent log
        agentLog("H4", "sessionFactory build start", "pre");
        // #endregion
        try {
            org.hibernate.cfg.Configuration config = new org.hibernate.cfg.Configuration();
            config.configure("hibernate.cfg.xml");
            config.addAnnotatedClass(UserViewModel.class);
            config.addAnnotatedClass(ActivityViewModel.class);
            config.addAnnotatedClass(Feedback.class);
            config.addAnnotatedClass(School.class);
            config.addAnnotatedClass(Resource.class);
            SessionFactory factory = config.buildSessionFactory();
            // #region agent log
            agentLog("H4", "sessionFactory build success", "ok");
            // #endregion
            return factory;
        } catch (Exception e) {
            // #region agent log
            agentLog("H4", "sessionFactory build failed", e.getClass().getSimpleName() + ":" + rootMessage(e));
            // #endregion
            throw e;
        }
    }

    // #region agent log
    private static String rootMessage(Throwable t) {
        Throwable c = t;
        while (c.getCause() != null) {
            c = c.getCause();
        }
        return c.getMessage() != null ? c.getMessage().replace("\"", "'") : "unknown";
    }

    private static void agentLog(String hypothesisId, String message, String data) {
        try (java.io.FileWriter fw = new java.io.FileWriter("debug-6c9dcf.log", true)) {
            fw.write(String.format(
                    "{\"sessionId\":\"6c9dcf\",\"runId\":\"post-fix\",\"hypothesisId\":\"%s\",\"location\":\"HibernateConfig\",\"message\":\"%s\",\"data\":{\"detail\":\"%s\"},\"timestamp\":%d}%n",
                    hypothesisId, message, data, System.currentTimeMillis()));
        } catch (Exception ignored) {
        }
    }
    // #endregion
}
