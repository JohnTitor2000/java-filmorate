package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.GetNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.util.ReviewUtil;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;

@Validated
@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewUtil reviewUtil;

    public ReviewService(ReviewStorage reviewStorage, ReviewUtil reviewUtil) {
        this.reviewStorage = reviewStorage;
        this.reviewUtil = reviewUtil;
    }

    public Review addReview(@Valid Review review) {
        reviewUtil.checkPositive(review);
        log.info("User with Id={} add review to film with Id= {}.", review.getUserId(), review.getFilmId());
        reviewStorage.addReview(review);;
        return review;
    }

    public Review updateReview(@Valid Review review) {
        reviewUtil.checkPositive(review);
        log.info("User with Id={} update review to film with Id={}.", review.getUserId(), review.getFilmId());
        reviewStorage.updateReview(review);
        return review;
    }

    public void deleteReview(@Positive Integer id) {
        log.info("Review with Id={} was deleted.", id);
        reviewStorage.deleteReview(id);
    }

    public Review getReview(@Positive Integer id) {
        Review review = reviewStorage.getReview(id);
        if (review == null) {
            log.warn("The review with Id={} not exist.", id);
            throw new GetNotFoundException("The review with Id=" + id + " not exist.");
        } else {
            log.info("The review with Id={} was requested and received.", id);
            return review;
        }
    }

    public void putLikeToReview(@Positive Integer reviewId, @Positive Integer userId) {
        log.info("User with Id={} put like to review with Id={}.", userId, reviewId);
        reviewStorage.putLikeToReview(reviewId, userId);
    }

    public void putDislikeToReview(@Positive Integer reviewId, @Positive Integer userId) {
        log.info("User with Id=" + userId + " put dislike to review with Id={}.", reviewId);
        reviewStorage.putDislikeToReview(reviewId, userId);
    }

    public void deleteLikeToReview(@Positive Integer reviewId, @Positive Integer userId) {
        log.info("User with Id=" + userId + " delete like to review with Id={}.", reviewId);
        reviewStorage.deleteLikeToReview(reviewId, userId);
    }

    public void deleteDislikeToReview(@Positive Integer reviewId, @Positive Integer userId) {
        log.info("User with Id=" + userId + " delete dislike to review with Id={}.", reviewId);
        reviewStorage.deleteDislikeToReview(reviewId, userId);
    }

    public List<Review> getAllReviews(@PositiveOrZero Integer filmId, @Positive Integer count) {
        log.info("Reviews was requested and received.");
        return reviewStorage.getAllReviews(filmId, count);
    }
}
