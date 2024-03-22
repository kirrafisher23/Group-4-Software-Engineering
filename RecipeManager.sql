CREATE TABLE `recipe` (
  `recipe_id` int NOT NULL AUTO_INCREMENT,
  `recipe_name` varchar(45) NOT NULL,
  `recipe_time` varchar(8) DEFAULT NULL,
  `recipe_serving_size` int DEFAULT NULL,
  PRIMARY KEY (`recipe_id`),
  UNIQUE KEY `recipe_name_UNIQUE` (`recipe_name`)
);
CREATE TABLE `recipe_ingredients` (
  `recipeID` int DEFAULT NULL,
  `ingredientsID` int DEFAULT NULL,
  `quantityID` int DEFAULT NULL,
  `measurementsID` int DEFAULT NULL,
  KEY `recipe_id_idx` (`recipeID`),
  KEY `ingredientsID_idx` (`ingredientsID`),
  KEY `quantityID_idx` (`quantityID`),
  KEY `measurementsID_idx` (`measurementsID`),
  CONSTRAINT `ingredientsID` FOREIGN KEY (`ingredientsID`) REFERENCES `ingredients` (`ingredient_id`),
  CONSTRAINT `measurementsID` FOREIGN KEY (`measurementsID`) REFERENCES `measurements` (`measurement_id`),
  CONSTRAINT `quantityID` FOREIGN KEY (`quantityID`) REFERENCES `quantity` (`quantity_id`),
  CONSTRAINT `recipeID_ingredient_table` FOREIGN KEY (`recipeID`) REFERENCES `recipe` (`recipe_id`)
);
CREATE TABLE `recipe_steps` (
  `steps_id` int NOT NULL AUTO_INCREMENT,
  `steps` text,
  `recipeID` int DEFAULT NULL,
  PRIMARY KEY (`steps_id`),
  KEY `recipe_id_idx` (`recipeID`),
  CONSTRAINT `recipeID_steps` FOREIGN KEY (`recipeID`) REFERENCES `recipe` (`recipe_id`)
);
CREATE TABLE `recipe_tags` (
  `recipeID` int DEFAULT NULL,
  `categoryID` int DEFAULT NULL,
  `tagID` int DEFAULT NULL,
  KEY `recipeID_tags_idx` (`recipeID`),
  KEY `categoryID_idx` (`categoryID`),
  KEY `tagID_idx` (`tagID`),
  CONSTRAINT `categoryID` FOREIGN KEY (`categoryID`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `recipeID_tags` FOREIGN KEY (`recipeID`) REFERENCES `recipe` (`recipe_id`),
  CONSTRAINT `tagID` FOREIGN KEY (`tagID`) REFERENCES `tags` (`tag_id`)
);
CREATE TABLE `tags` (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `tag` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `tag_UNIQUE` (`tag`)
);
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_UNIQUE` (`category`)
);
CREATE TABLE `ingredients` (
  `ingredient_id` int NOT NULL AUTO_INCREMENT,
  `ingredient_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`ingredient_id`)
);
CREATE TABLE `measurements` (
  `measurement_id` int NOT NULL AUTO_INCREMENT,
  `measurement` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`measurement_id`)
);
CREATE TABLE `quantity` (
  `quantity_id` int NOT NULL AUTO_INCREMENT,
  `quantity` float DEFAULT NULL,
  PRIMARY KEY (`quantity_id`)
);
