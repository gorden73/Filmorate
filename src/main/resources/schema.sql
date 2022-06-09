CREATE TABLE IF NOT EXISTS users(
	user_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email varchar(255) NOT NULL,
	login varchar(255) NOT NULL,
	name varchar(255),
	birthday Date NOT NULL);

CREATE TABLE IF NOT EXISTS mpa(
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS films(
	film_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(100) NOT NULL,
	description varchar(200) NOT NULL ,
	release_date Date NOT NULL CHECK (release_date > '1895-12-28'),
	duration INT NOT NULL CHECK (duration > 0),
	mpa INT NOT NULL,
    FOREIGN KEY (mpa) REFERENCES mpa(id));


CREATE TABLE IF NOT EXISTS genres(
	genre_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(15) NOT NULL);

CREATE TABLE IF NOT EXISTS film_genre(
	film_genre_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	film_id INT NOT NULL,
	genre_id INT NOT NULL,
	FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
	FOREIGN KEY (genre_id) REFERENCES genres(genre_id));

CREATE TABLE IF NOT EXISTS friends(
	friendship_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user_id INT NOT NULL ,
	friend_id INT NOT NULL,
	status BOOLEAN DEFAULT(0),
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS likes(
	likes_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	user_id INT NOT NULL,
	film_id INT NOT NULL,
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE);



	