package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
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
        try {
            jdbcTemplate.update("DELETE FROM USERS WHERE id=?", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("User with this id %d was not found", id));
        }
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
        User user = getUserById(id);
        if(user == null) {
            throw new NotFoundException(String.format("User with this id %d was not found", id));
        }
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
}
