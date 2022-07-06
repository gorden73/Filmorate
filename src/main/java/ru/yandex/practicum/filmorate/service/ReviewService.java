package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Slf4j
@Service
public class ReviewService {
    private final ReviewDao reviewDao;
    private final ReviewLikeDao reviewLikeDao;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedDao feedDbStorage;

    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewLikeDao reviewLikeDao,
                         UserService userService, FilmService filmService,
                         FeedDao feedDbStorage) {
        this.reviewDao = reviewDao;
        this.reviewLikeDao = reviewLikeDao;
        this.userService = userService;
        this.filmService = filmService;
        this.feedDbStorage = feedDbStorage;
    }

    public Collection<Review> getReviews(Integer filmId, Integer count) {
        if (filmId < 1) return reviewDao.getAllReviews(count);
        filmService.getFilm(filmId);
        return reviewDao.getReviewsByFilmId(filmId, count);
    }

    public Review getReviewById(Integer id) {
        return reviewDao.getReviewById(id);
    }

    public Review addReview(Review review) {
        userService.findUserById(review.getUserId());
        filmService.getFilm(review.getFilmId());
        if (review.getId() != null) {
            log.warn("При создании отзыва был передан id.");
            throw new ValidationException("При создании отзыва был передан id. " +
                    "Идентификатор назначается автоматически.");
        }
        Review newReview = reviewDao.addReview(review);
        feedDbStorage.addFeed(new Feed(review.getUserId(), "REVIEW", "ADD", newReview.getId()));
        return newReview;
    }

    public Review updateReview(Review newReview) {
        getReviewById(newReview.getId());
        feedDbStorage.addFeed(new Feed(newReview.getUserId(), "REVIEW", "UPDATE", newReview.getId()));
        return reviewDao.updateReview(newReview);
    }

    public Integer deleteReviewById(Integer id) {
        Review review = getReviewById(id);
        feedDbStorage.addFeed(new Feed(review.getUserId(), "REVIEW", "REMOVE", id));
        return reviewDao.deleteReviewById(id);
    }

    public Integer addLike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        return reviewLikeDao.addLike(userId, reviewId);
    }

    public Integer addDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        return reviewLikeDao.addDislike(userId, reviewId);
    }

    public Integer deleteLike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        return reviewLikeDao.delete(userId, reviewId);
    }

    public Integer deleteDislike(Integer userId, Integer reviewId) {
        getReviewById(reviewId);
        userService.findUserById(userId);
        return reviewLikeDao.delete(userId, reviewId);
    }
}
