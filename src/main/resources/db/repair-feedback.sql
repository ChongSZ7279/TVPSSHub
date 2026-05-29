-- Fixes: Table 'tvpsshub.feedback' doesn't exist in engine
-- Run when MySQL shows a broken/corrupt feedback table during app startup.
USE TVPSShub;

DROP TABLE IF EXISTS feedback;

CREATE TABLE feedback (
    feedback_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    activity_id INT(11) NOT NULL,
    user_id BIGINT(20) NOT NULL,
    feedback_text TEXT NOT NULL,
    rating INT(11) DEFAULT NULL,
    date DATE NOT NULL,
    INDEX (activity_id),
    INDEX (user_id),
    CONSTRAINT fk_feedback_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_feedback_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
