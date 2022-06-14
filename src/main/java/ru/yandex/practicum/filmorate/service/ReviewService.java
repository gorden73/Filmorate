package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDao;
import ru.yandex.practicum.filmorate.dao.impl.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Collection;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewLikeDao reviewLikeDao,
                         UserService userService, FilmService filmService) {
        this.reviewDao = reviewDao;
        this.reviewLikeDao = reviewLikeDao;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Collection<Review> getReviews(Integer filmId, Integer count) {
        if (filmId < 1) return reviewDao.getAllReviews(count);
        return reviewDao.getReviewsByFilmId(filmId, count);
    }

    public Review getReviewById(Integer id) {
        return reviewDao.getReviewById(id);
    }

    public Review addReview(Review review) {
        userService.findUserById(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getReviewId() != null) {
            log.warn("При создании отзыва был передан id.");
            throw new ValidationException("При создании отзыва был передан id. " +
                                          "Идентификатор назначается автоматически.");
        }
        return reviewDao.addReview(review);
    }

    public Review updateReview(Review newReview) {
        getReviewById(newReview.getReviewId());
        return reviewDao.updateReview(newReview);
    }

    public void deleteReviewById(Integer id) {
        reviewDao.deleteReviewById(id);
    }

    public void addLike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        reviewLikeDao.addLike(userId, reviewId);
    }

    public void addDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        reviewLikeDao.addDislike(userId, reviewId);
    }

    public void deleteLike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        reviewLikeDao.delete(userId, reviewId);
    }

    public void deleteDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        reviewLikeDao.delete(userId, reviewId);
    }
}
