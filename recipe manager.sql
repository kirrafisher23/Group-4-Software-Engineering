create schema recipeManager;

create table users(
userId integer not null unique auto_increment,
userName varchar(255) not null,
password varchar(255) not null
);