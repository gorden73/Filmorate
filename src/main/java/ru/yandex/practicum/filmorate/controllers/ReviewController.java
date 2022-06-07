package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public Collection<Review> getReviews(@RequestParam(defaultValue = "0") Integer filmId,
                                         @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review newReview) {
        return reviewService.updateReview(newReview);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable Integer id) {
        reviewService.deleteReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLike(userId, reviewId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLike(userId, reviewId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.addDislike(userId, reviewId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteDislike(userId, reviewId);
    }
}
