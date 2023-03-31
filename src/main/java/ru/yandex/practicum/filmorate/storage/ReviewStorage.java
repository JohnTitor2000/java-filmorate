package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "insert into REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) values (?, ?, ?, ?)",
                    new String[]{"REVIEW_ID"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getFilmId());
            return statement; }, keyHolder);
        review.setUseful(0);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return review;
    }

    public Review updateReview(Review review) {
        jdbcTemplate.update("update REVIEWS set CONTENT = ?, IS_POSITIVE = ? where REVIEW_ID = ?",
                review.getContent(), review.getIsPositive(), review.getReviewId());
        // Тесты в Postman отправляют PUT запрос с некорректными полями, но требуют возвращения корректных значений
        /*
          {
	        "reviewId": 1,
            "content": "This film is not too bad.",
            "isPositive": true,
            "userId": 2,  -  incorrect field !!!
            "filmId": 2,  -  incorrect field !!!
            "useful": 10  -  incorrect field !!!
          }
         */
        // Из-за этого пришлось делать дополнительный запрос к БД методом getReview(), чтобы получить корректные значения
        Review updatedReview = getReview(review.getReviewId());
        review.setUserId(updatedReview.getUserId());
        review.setFilmId(updatedReview.getFilmId());
        review.setUseful(updatedReview.getUseful());
        return review;
    }

    public void deleteReview(Integer id) {
        jdbcTemplate.update("delete from REVIEWS where REVIEW_ID = ?", id);
    }

    public Review getReview(Integer id) {
        String sql = "select r.REVIEW_ID, r.CONTENT, r.IS_POSITIVE, r.USER_ID, r.FILM_ID, r.USEFUL " +
                "from REVIEWS r where REVIEW_ID = ?";
        List<Review> list = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id);
        Review review = null;
        if (list.size() == 1) {
            review = list.stream().findFirst().get();
            review.setUseful(makeUseful(id));
        }
        return review;
    }

    public void putLikeToReview(Integer reviewId, Integer userId) {
        jdbcTemplate.update("INSERT INTO REVIEWS_LIKES VALUES (?, ?, 1)", reviewId, userId);
    }

    public void putDislikeToReview(Integer reviewId, Integer userId) {
        jdbcTemplate.update("INSERT INTO REVIEWS_LIKES VALUES (?, ?, -1)", reviewId, userId);
    }

    public void deleteLikeToReview(Integer reviewId, Integer userId) {
        jdbcTemplate.update("DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?", reviewId, userId);
    }

    public void deleteDislikeToReview(Integer reviewId, Integer userId) {
        jdbcTemplate.update("DELETE FROM REVIEWS_LIKES WHERE REVIEW_ID = ? AND USER_ID = ?", reviewId, userId);
    }

    public List<Review> getAllReviews(Integer filmId, Integer count) {
        String sqlAllFilms = "SELECT * FROM REVIEWS ORDER BY USEFUL DESC, FILM_ID LIMIT ?";
        String sqlOneFilm = "SELECT * FROM REVIEWS WHERE FILM_ID = ? ORDER BY USEFUL DESC, FILM_ID LIMIT ?";
        List<Review> reviews;
        if (filmId == 0) {
            reviews = jdbcTemplate.query(sqlAllFilms, (rs, rowNum) -> makeReview(rs), count);
        } else {
            reviews = jdbcTemplate.query(sqlOneFilm, (rs, rowNum) -> makeReview(rs), filmId, count);
        }
        if (reviews != null) {
            setUsefulToReview(reviews);
            reviews.sort(Comparator.comparingInt(Review::getUseful).reversed());
        }
        return Objects.requireNonNullElseGet(reviews, ArrayList::new);
    }

    static Review makeReview(ResultSet rs) throws SQLException {
        Review review = Review.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .isPositive(Boolean.parseBoolean(rs.getString("IS_POSITIVE")))
                .userId(rs.getInt("USER_ID"))
                .filmId(rs.getInt("FILM_ID"))
                .build();
        return review;
    }

    private int makeUseful(Integer reviewId) {
        String sql = "SELECT SUM(USEFUL_VALUE) AS USEFUL_VALUE FROM REVIEWS_LIKES GROUP BY REVIEW_ID HAVING REVIEW_ID = ?";
        AtomicReference<Integer> result = new AtomicReference<>(0);
        jdbcTemplate.query(sql, rs -> {
            result.set(rs.getInt("USEFUL_VALUE"));
        }, reviewId);
        return result.get();
    }

    private void setUsefulToReview(List<Review> reviews) {
        String sql = "SELECT REVIEW_ID, SUM (USEFUL_VALUE) AS USEFUL_VALUE FROM REVIEWS_LIKES GROUP BY REVIEW_ID";
        Map<Integer, Integer> map = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            map.put(rs.getInt("REVIEW_ID"), rs.getInt("USEFUL_VALUE"));
        });
        for (Review r: reviews) {
            if (map.containsKey(r.getReviewId())) {
                r.setUseful(map.get(r.getReviewId()));
            }
        }
    }
}
