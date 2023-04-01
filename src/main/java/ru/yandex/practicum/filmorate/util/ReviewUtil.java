package ru.yandex.practicum.filmorate.util;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotPositiveFieldException;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewUtil {
    public void checkPositive(Review review) {
        if (review.getUserId() < 1 || review.getFilmId() < 1) {
            throw new NotPositiveFieldException("Fields userId and filmId must be positive.");
        }
    }
}