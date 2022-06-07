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