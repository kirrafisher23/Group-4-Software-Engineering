create table users(
userID integer not null auto_increment, 
userName varchar(255) not null,
pass varchar(255) not null,
primary key(userID)
);


create table recipes(
recipeId integer not null auto_increment,
recipeName varchar(255) unique,
creationDate date not null,
modifyDate date not null,
userID integer not null,
recipeType varchar(255) not null,
primary key(recipeId),
FOREIGN KEY (userID) REFERENCES users(userID)
);

 create table ingredients(
ingredientID integer not null auto_increment,
ingredientType varchar(255) not null unique,
ingredientName varchar(255) not null,
primary key (ingredientID)
 )
 
 create table recipesIngredients(
 recipeId integer not null,
 ingredientID integer not null,
 quantity  integer not null,
 unit varchar(255) not null,
 FOREIGN KEY (recipeId) REFERENCES recipes(recipeId),
 FOREIGN KEY (ingredientID) REFERENCES ingredients(ingredientID)
 );
  DROP TABLE recipesIngredients;
 
DELIMITER $$
CREATE PROCEDURE addnewUser (IN username VARCHAR(255), IN password VARCHAR(255))
BEGIN
    INSERT INTO users (userName, password) VALUES (username, password);
END $$


DELIMITER $$
CREATE PROCEDURE loginCreds (IN username VARCHAR(255), IN password VARCHAR(255))
BEGIN
    SELECT * FROM users WHERE userName = username AND password = password;
END$$ 
