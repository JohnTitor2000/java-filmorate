package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User getUserById(int id);

    void addUser(User user);

    void deleteUserById(int id);

    void updateUserById(int id, User user);

    void deleteAllUsers();

    Collection<User> getAllUsers();

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);

    List<Film> getRecommendations(int id);
}
