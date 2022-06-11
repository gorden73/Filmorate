INSERT INTO genres(name)
VALUES ('COMEDY'),
       ('DRAMA'),
       ('CARTOON'),
       ('THRILLER'),
       ('DOCUMENTARY'),
       ('ACTION');

INSERT INTO mpa(name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO users(email, login, name, birthday)
VALUES('mail', 'login', 'name', '1990-03-08');

INSERT INTO films(name, description, release_date, duration, mpa)
VALUES ('The Rock', 'Starring Nicolas Cage and Sean Connery', '1996-06-07', '136', '1');

INSERT INTO directors(name)
VALUES ('Jon Scott');

INSERT INTO directors(name)
VALUES ('Jack Tomson');

INSERT INTO FILM_DIRECTOR(director_id, film_id) VALUES ( '1', '3' );
INSERT INTO FILM_DIRECTOR(director_id, film_id) VALUES ( '1', '2' );
INSERT INTO FILM_DIRECTOR(director_id, film_id) VALUES ( '2', '3' );