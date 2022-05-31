CREATE TABLE IF NOT EXISTS users(
	user_id INT PRIMARY KEY AUTO_INCREMENT,
	email varchar(255) NOT NULL,
	login varchar(255) NOT NULL,
	name varchar(255),
	birthday Date NOT NULL);

CREATE TABLE IF NOT EXISTS films(
	film_id INT PRIMARY KEY AUTO_INCREMENT,
	name varchar(100) NOT NULL,
	description varchar(200),
	release_date Date CHECK (release_date > '1895-12-28'),
	duration INT CHECK (duration > 0),
	mpa varchar(45));

CREATE TABLE IF NOT EXISTS genres(
	genre_id INT PRIMARY KEY,
	name varchar(45));

CREATE TABLE IF NOT EXISTS film_genre(
	film_genre_id INT PRIMARY KEY AUTO_INCREMENT,
	film_id INT,
	genre_id INT,
	FOREIGN KEY (film_id) REFERENCES films(film_id),
	FOREIGN KEY (genre_id) REFERENCES genres(genre_id));

CREATE TABLE IF NOT EXISTS friends(
	friendship_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT,
	friend_id INT,
	status BOOLEAN DEFAULT(0),
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (friend_id) REFERENCES users(user_id));

CREATE TABLE IF NOT EXISTS likes(
	likes_id INT PRIMARY KEY AUTO_INCREMENT,
	user_id INT,
	film_id INT,
	FOREIGN KEY (user_id) REFERENCES users(user_id),
	FOREIGN KEY (film_id) REFERENCES films(film_id));



	