package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewDao {
    Review getReviewById(Integer id);

    Collection<Review> getAllReviews(Integer count);

    Collection<Review> getReviewsByFilmId(Integer filmId, Integer count);

    Review addReview(Review review);

    Review updateReview(Review newReview);

    Integer deleteReviewById(Integer id);
}
