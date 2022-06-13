# Filmorate project
Educational project on creating a service
<br>for selecting films based on their rating

(5 сторипоинтов)
<br>Добавлена функция получения рекомендаций к просмотру фильмов для определенного пользователя.
<br>API - GET /users/{id}/recommendations?page=&size= 

(2 сторипоинта)
<br>Добавлена функция получения общих фильмов у друзей
<br>Пример запроса на API с пагинацией
<br>API - GET /films/common?userId=1&friendId=3&count=1 (запрос 1 общего фильма)
<br>API - GET films/common?userId=1&friendId=3&page=2&count=2 (запрос 2-х фильмов со второй страницы)

(4 сторипоинта)
<br>Добавлена новая функциональность - "Отзывы". В нее входит:
<br>API - POST /reviews - добавление нового отзыва.
<br>API - PUT /reviews - редактирование уже имеющегося отзыва.
<br>API - DELETE /reviews/{id} - удаление уже имеющегося отзыва.
<br>API - GET /reviews/{id} - получение отзыва по идентификатору.
<br>API - GET /reviews?filmId={filmId}&count={count}
<br>Получение всех отзывов по идентификатору фильма, если фильм не указа но все. 
<br>Если кол-во не указано то 10.
<br>API - PUT /reviews/{id}/like/{userId}  — пользователь ставит лайк отзыву.
<br>API - PUT /reviews/{id}/dislike/{userId}  — пользователь ставит дизлайк отзыву.
<br>API - DELETE /reviews/{id}/like/{userId}  — пользователь удаляет лайк/дизлайк отзыву.
<br>API - DELETE /reviews/{id}/dislike/{userId}  — пользователь удаляет дизлайк отзыву.

(4 сторипоинта)
<br>Добавлена новая функциональность добавления/создания/удаления/изменения режиссера
<br>API - GET /directors - все режиссеры
<br>API - GET /directors/{id} - режиссер по id
<br>API - POST /directors - создание режиссера
<br>API - PUT /directors - изменение режиссера
<br>API - DELETE /directors - удаление режиссера
<br>Вывод всех фильмов режиссёра, отсортированных по количеству лайков.
<br>API - GET /films/director/{directorId}?sortBy=likes
<br>Вывод всех фильмов режиссёра, отсортированных по годам.
<br>API - GET /films/director/{directorId}?sortBy=year

<br>![ER diagram](/er-filmorate/ER-filmorate.png)
<br>Примеры запросов:
1. Выгрузка всех пользователей:
   <br>SELECT *
   <br>FROM user;

2. Выгрузка одного пользователя (n - id пользователя):
   <br>SELECT *
   <br>FROM user
   <br>WHERE user_id = n;

3. Выгрузка друзей пользователя (n - id пользователя):
   <br>SELECT friend_id
   <br>FROM friend
   <br>WHERE user_id = n;

4. Выгрузка общих друзей
с другим пользователем: <br>(n - id 1 пользователя)
   <br>(m - id 2 пользователя)		
   <br>SELECT friend_id
   <br>FROM friend
   <br>WHERE user_id = n
   <br>AND user_id = m
   <br>GROUP BY friend_id;

5. Выгрузка всех фильмов:
   <br>SELECT *
   <br>FROM film;

6. Выгрузка одного фильма (n - id фильма):
   <br>SELECT *
   <br>FROM film
   <br>WHERE film_id = n;

7. Выгрузка N-популярных фильмов:
   <br>SELECT film_id
   <br>FROM like
   <br>GROUP BY film_id
   <br>ORDER BY COUNT(user_id) DESC
   <br>LIMIT N;