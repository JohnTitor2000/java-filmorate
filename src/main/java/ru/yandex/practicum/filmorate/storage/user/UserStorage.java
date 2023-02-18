package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    public User getUserById(int id);

    public void addUser(User user);

    public void deleteUserById(int id);

    public void updateUserById(int id, User user);

    public void deleteAllUsers();

    public Collection<User> getAllUsers();

    public void addFriend(int id, int friendId);

    public void deleteFriend(int id, int friendId);

    public List<User> getFriends(int id);

    public List<User> getCommonFriends(int id, int otherId);

    public Map<Integer, User> getUsers();
}
