CREATE DATABASE local_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'hglee'@'localhost' IDENTIFIED BY 'hglee';
CREATE USER 'hglee'@'%' IDENTIFIED BY 'hglee';
GRANT ALL ON hglee.* to 'hglee'@'localhost';
GRANT ALL ON hglee.* to 'hglee'@'%';
