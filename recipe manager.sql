create schema recipeManager;

create table users(
userId integer not null unique auto_increment,
userName varchar(255) not null,
password varchar(255) not null
);

DELIMITER //
CREATE PROCEDURE addnewUser (IN username VARCHAR(255), IN password VARCHAR(255))
BEGIN
    INSERT INTO users (userName, password) VALUES (username, password);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE loginCreds (IN username VARCHAR(255), IN password VARCHAR(255))
BEGIN
    SELECT * FROM users WHERE userName = username AND password = password;
END //
DELIMITER ;




