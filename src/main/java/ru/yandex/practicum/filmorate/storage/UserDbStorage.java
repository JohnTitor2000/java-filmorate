package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Primary
public class UserDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User getUserById(int id) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE id = ?", BeanPropertyRowMapper.newInstance(User.class), id);
            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("the user being received was not found");
        }
    }

    @Override
    public void addUser(User user) {
        jdbcTemplate.update("INSERT INTO USERS (login, name, email, birthday) VALUES (?,?,?,?)",
        new Object[] {user.getLogin(), user.getName(), user.getEmail(), user.getBirthday().format(DateTimeFormatter.ofPattern("uuuu-MM-dd"))});
    }

    @Override
    public void deleteUserById(int id) {
        jdbcTemplate.update("DELETE FROM USERS WHERE id=?", id);
    }

    @Override
    public void updateUserById(int id, User user) {
        if(getUserById(id) == null) {
            throw new NotFoundException("The user being updated was not found.");
        }
        jdbcTemplate.update("UPDATE USERS SET name=?, email=?, login=?, birthday=? WHERE id=?",
                new Object[] { user.getName(), user.getEmail(), user.getLogin(), user.getBirthday().toString(), id });
    }

    @Override
    public void deleteAllUsers() {
        jdbcTemplate.update("DELETE from USERS");
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * from USERS", BeanPropertyRowMapper.newInstance(User.class));
    }

    @Override
    public void addFriend(int id, int friendId) {
        jdbcTemplate.update("INSERT INTO FRIENDSHIP (requester_id, receiver_id, сonfirmation) VALUES (?,?,?)",
                new Object[] {id, friendId, false});
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        jdbcTemplate.update("DELETE FROM FRIENDSHIP WHERE requester_id=? AND receiver_id=?", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
            return jdbcTemplate.query("SELECT requester_id, receiver_id, сonfirmation FROM FRIENDSHIP WHERE requester_id = ? OR (receiver_id = ? AND сonfirmation = true)",
                    (rs, rowNum) -> makeUser(id, rs), id, id);
    }

    private User makeUser(int id, ResultSet rs) throws SQLException {
        if (rs.getInt("requester_id") == id) {
            return getUserById(rs.getInt("receiver_id"));
        } else {
            return getUserById(rs.getInt("requester_id"));
        }
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> firstUserFriends = getFriends(id);
        List<User> secondUserFriends = getFriends(otherId);
        List<User> result = firstUserFriends.stream()
                .distinct()
                .filter(secondUserFriends::contains)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        List<Integer> similarUserIds = findSimilarUsers(userId);
        System.out.println(similarUserIds);
        if (similarUserIds.isEmpty() || similarUserIds == null) {
            List<Film> nullResult = new ArrayList<>();
            return nullResult;
        }
        List<Film> recommendedFilms = findRecommendedFilms(userId, similarUserIds);
        if (recommendedFilms == null) {
            recommendedFilms = new ArrayList<>();
            return  recommendedFilms;
        }
        return recommendedFilms;
    }

    private List<Integer> findSimilarUsers(int userId) {
        String sql = "SELECT user2.user_id " +
                "FROM likes AS user1 " +
                "JOIN likes AS user2 ON user1.film_id = user2.film_id AND user1.user_id != user2.user_id " +
                "WHERE user1.user_id = ? " +
                "GROUP BY user2.user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 10";

        System.out.println(jdbcTemplate.queryForList(sql, Object.class, userId).stream()
                .map(obj -> obj == null ? null : Integer.valueOf(obj.toString()))
                .collect(Collectors.toList()));
        return jdbcTemplate.queryForList(sql, Object.class, userId).stream()
                .map(obj -> obj == null ? null : Integer.valueOf(obj.toString()))
                .collect(Collectors.toList());
    }

    private List<Film> findRecommendedFilms(int userId, List<Integer> similarUserIds) {
        String sql = "SELECT f.id " +
                "FROM films AS f " +
                "JOIN likes AS user1 ON user1.film_id = f.id AND user1.user_id = ? " +
                "LEFT JOIN likes AS user2 ON user2.film_id = f.id AND user2.user_id = ANY(?) " +
                "WHERE user2.user_id IS NULL " +
                "GROUP BY f.id " +
                "HAVING COUNT(*) >= 1 " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 10";

        String sql2 = "SELECT l1.film_id\n" +
                "FROM likes l1\n" +
                "LEFT JOIN likes l2 ON l1.film_id = l2.film_id AND l2.user_id = ?\n" +
                "WHERE l1.user_id = ? AND l2.user_id IS NULL;";

        Integer[] recommendedFilmsArray = similarUserIds.toArray(new Integer[0]);
        Set<Integer> result = new HashSet<>();
        for (int id : similarUserIds) {
            List<Integer> recommendedFilms = jdbcTemplate.queryForList(sql2, Object.class, userId, recommendedFilmsArray).stream()
                    .map(obj -> obj == null ? null : Integer.valueOf(obj.toString()))
                    .collect(Collectors.toList());
            result.addAll(recommendedFilms);
        }

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate, new GenreDbStorage(jdbcTemplate), new MpaDbStorage(jdbcTemplate), new DirectorDbStorage(jdbcTemplate));
        List<Film> resultFilms = new ArrayList<>();
        for (Integer filmId : result) {
            resultFilms.add(filmDbStorage.getFilmById(filmId));
        }
        return resultFilms;
    }
}
