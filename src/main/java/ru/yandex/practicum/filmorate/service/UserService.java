package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@Validated
@Slf4j
@Service
public class UserService {

    private static int currentId = 1;
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<Film> getRecommendations(int id) {
        return userStorage.getRecommendations(id);
    }

    public Collection<User> getAllUsers() {
        log.debug("Received a request to receive all users");
        return userStorage.getAllUsers();
    }

    public void addUser(@Valid User user) {
        nameValidation(user);
        user.setId(currentId++);
        log.debug("A request to create a user was received");
        userStorage.addUser(user);
    }

    public void deleteUser(@Positive int id) {
        log.debug("Received a request to delete the user by ID: " + id);
        userStorage.deleteUserById(id);
    }

    public void updateUser(@Valid User user) {
        log.debug("User by ID: " + user.getId() + " update request received");
        nameValidation(user);
        userStorage.updateUserById(user.getId(), user);
    }

    public void deleteAllUsers() {
        userStorage.deleteAllUsers();
    }

    public User getUserById(@Positive int id) {
        log.debug("Received a request to get a user by ID " + id);
        return userStorage.getUserById(id);
    }

    public void addFriend(int id, int friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new NotFoundException("Not found user with " + id + " id");
        }
        log.debug("Received a request to add friends with IDs: " + id + ", " + friendId);
        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(@Positive int id, @Positive int friendId) {
        log.debug("Received a request to delete friends with IDs: " + id + ", " + friendId);
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriends(@Positive int id) {
        log.debug("A request was received to get all the user's friends with an ID: " + id);
        return userStorage.getFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new NotFoundException("Not found user with " + id + " id");
        }
        log.debug("A request was received to get mutual friends of users with an IDs: " + id + ", " + otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void nameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}