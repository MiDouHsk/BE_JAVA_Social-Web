CREATE TABLE favorites (
                           post_id CHAR(36),
                           user_id CHAR(36),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id)
);
