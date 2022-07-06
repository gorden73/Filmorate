MERGE INTO genres(genre_id, name)
    VALUES (1, 'COMEDY'),
           (2, 'DRAMA'),
           (3, 'CARTOON'),
           (4, 'THRILLER'),
           (5, 'DOCUMENTARY'),
           (6, 'ACTION');

MERGE INTO mpa(id, name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

INSERT INTO users(email, login, name, birthday)
VALUES('mail', 'login', 'name', '1990-03-08');

INSERT INTO films(name, description, release_date, duration, mpa)
VALUES ('The Rock', 'Starring Nicolas Cage and Sean Connery', '1996-06-07', '136', '1');