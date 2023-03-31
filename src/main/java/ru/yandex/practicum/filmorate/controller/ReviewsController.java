package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        reviewService.addReview(review);
        return review;
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        reviewService.updateReview(review);
        return review;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") Integer id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable("id") Integer id) {
        return reviewService.getReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putLikeToReview(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        reviewService.putLikeToReview(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void putDislikeToReview(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        reviewService.putDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToReview(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        reviewService.deleteLikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeToReview(@PathVariable("id") Integer reviewId, @PathVariable("userId") Integer userId) {
        reviewService.deleteDislikeToReview(reviewId, userId);
    }

    @GetMapping
    // @RequestParam(defaultValue = "0") Integer filmId  -  если значение будет 0, то в список попадут все фильмы
    public List<Review> getAllReviews(@RequestParam(defaultValue = "0") Integer filmId,
                                      @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getAllReviews(filmId, count);
    }
}
