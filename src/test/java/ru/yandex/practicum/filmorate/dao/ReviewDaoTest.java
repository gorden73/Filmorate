package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDao;
import ru.yandex.practicum.filmorate.dao.impl.ReviewLikeDao;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReviewDaoTest {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private Review review1 = new Review(null, "Great", true, 1, 1, 0);
    private Review review2 = new Review(null, "Great", true, 1, 2, 0);
    private Review reviewUPD = new Review(1, "Terrible", false, 1, 1, 0);
    private User user = new User("test@mail.ru", "test", "Test", LocalDate.of(2000, 1, 1));
    private Film film = new Film("Test", "Test", LocalDate.of(2000, 1, 1), 90, new Mpa(1));
    private Film film2 = new Film("Test2", "Test2", LocalDate.of(2000, 1, 1), 90, new Mpa(1));


    @Test
    void shouldReturnEmptyCollection() {
        Collection<Review> reviews = reviewDao.getAllReviews(10);
        assertThat(reviews).isEmpty();
    }

    @Test
    void shouldReturnListWithReview() {
        userDbStorage.addUser(user);
        filmDbStorage.addFilm(film);
        reviewDao.addReview(review1);
        Collection<Review> reviews = reviewDao.getAllReviews(10);
        assertThat(reviews).hasSize(1);
        Review review = new ArrayList<>(reviews).get(0);
        assertEquals(1, review.getReviewId());
        assertEquals(1, review.getUserId());
        assertEquals(1, review.getFilmId());
        assertEquals("Great", review.getContent());
        assertTrue(review.isPositive());
        assertEquals(0, review.getUseful());
    }

    @Test
    void shouldReturnListWithOnlyOneReview() {
        userDbStorage.addUser(user);
        filmDbStorage.addFilm(film);
        filmDbStorage.addFilm(film2);
        reviewDao.addReview(review1);
        reviewDao.addReview(review2);
        Collection<Review> reviews = reviewDao.getAllReviews(1);
        assertThat(reviews).hasSize(1);
    }

    @Test
    void shouldReturnUsefulReviewFirst() {
        userDbStorage.addUser(user);
        filmDbStorage.addFilm(film);
        reviewDao.addReview(review1);
        reviewDao.addReview(review2);
        reviewLikeDao.addLike(1, 2);
        Collection<Review> reviews = reviewDao.getAllReviews(10);
        assertThat(reviews).hasSize(2);
        Review usefulReview = new ArrayList<>(reviews).get(0);
        assertEquals(2, usefulReview.getReviewId());
        assertEquals(1, usefulReview.getUserId());
        assertEquals(2, usefulReview.getFilmId());
        assertEquals("Great", usefulReview.getContent());
        assertTrue(usefulReview.isPositive());
        assertEquals(1, usefulReview.getUseful());
    }

    @Test
    void shouldReturnReviewById() {
        reviewDao.addReview(review1);
        Review review = reviewDao.getReviewById(1);
        assertEquals(1, review.getReviewId());
        assertEquals(1, review.getUserId());
        assertEquals(1, review.getFilmId());
        assertEquals("Great", review.getContent());
        assertTrue(review.isPositive());
        assertEquals(0, review.getUseful());
    }

    @Test
    void shouldReturnUpdatedReview() {
        reviewDao.addReview(review1);
        reviewDao.updateReview(reviewUPD);
        Review review = reviewDao.getReviewById(1);
        assertEquals("Terrible", review.getContent());
        assertFalse(review.isPositive());
    }
}
