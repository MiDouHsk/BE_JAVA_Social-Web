CREATE TABLE users (
                       id CHAR(36) PRIMARY KEY,
                    username VARCHAR(50) NOT NULL,
                    password VARCHAR(50) NOT NULL,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    role VARCHAR(50) NOT NULL,
                    gender BOOLEAN NOT NULL,
                    phone_number VARCHAR(50) NOT NULL,
                    date_of_birth TIMESTAMP NOT NULL,
                    mail VARCHAR(50) NOT NULL,
                    enable BOOLEAN NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


DROP TABLE IF EXISTS `post`;
CREATE TABLE post (
                    id CHAR(36) PRIMARY KEY,
                    title VARCHAR(50) NOT NULL,
                    body TEXT NOT NULL ,
                    status VARCHAR(100) NOT NULL ,
                    total_like INT NOT NULL ,
                    total_comment INT NOT NULL ,
                    total_share INT NOT NULL ,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_by CHAR(36),
                    FOREIGN KEY (created_by) REFERENCES users(id));


DROP TABLE IF EXISTS medias;
CREATE TABLE medias (
                    id CHAR(36) PRIMARY KEY,
                    base_name VARCHAR(50) NOT NULL,
                    public_url VARCHAR(50) NOT NULL,
                    post_id CHAR(36),
                    FOREIGN KEY (post_id) REFERENCES post(id));


DROP TABLE IF EXISTS favorites;
CREATE TABLE favorites (
                           post_id CHAR(36),
                           user_id CHAR(36),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (post_id),
                           FOREIGN KEY (post_id) REFERENCES post(id),
                           FOREIGN KEY (user_id) REFERENCES users(id)
);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE comment (
                         id CHAR(36) PRIMARY KEY,
                         content VARCHAR(50) NOT NULL,
                         post_id CHAR(36),
                         total_like INT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by CHAR(36),
                         FOREIGN KEY (created_by) REFERENCES users(id),
                         FOREIGN KEY (post_id) REFERENCES post(id));


DROP TABLE IF EXISTS `reactions`;
CREATE TABLE reactions (
                        id CHAR(36) PRIMARY KEY,
                        object_type VARCHAR(100) NOT NULL,
                        object_id CHAR(36) NOT NULL,
                        type INT NOT NULL,
                        created_by CHAR(36) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (created_by) REFERENCES users(id));


DROP TABLE IF EXISTS `follows`;
CREATE TABLE follows (
                         following_user_id CHAR(36),
                         user_id CHAR(36),
                         FOREIGN KEY (following_user_id) REFERENCES users(id),
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);