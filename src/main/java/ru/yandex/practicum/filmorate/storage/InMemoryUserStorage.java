package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GetNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    private final static int DEFAULT_MATRIX_SIZE = 10;
    private final static int FRIENDSHIP = 1;
    private final static int NO_FRIENDSHIP = 0;

    private int[][] friends = new int[DEFAULT_MATRIX_SIZE][DEFAULT_MATRIX_SIZE];

    public Map<Integer, User> getUsers() {
        return users;
    }

    public void addFriend(int id, int friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("There is no such user");
        }
        friends[id][friendId] = FRIENDSHIP;
        friends[friendId][id] = FRIENDSHIP;
    }

    public void deleteFriend(int id, int friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("There is no such user");
        }
        friends[id][friendId] = NO_FRIENDSHIP;
        friends[friendId][id] = NO_FRIENDSHIP;
    }

    @Override
    public List<User> getFriends(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.valueOf(id) + ": There is no such user");
        }
        List<User> result = new ArrayList<>();
        for (int i = 1; i < friends[0].length; i++) {
            if (friends[id][i] != 0) {
                result.add(users.get(i));
            }
        }
        return result;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        if(!users.containsKey(id) || !users.containsKey(otherId)) {
            throw new NotFoundException("There is no such user");
        }
        List<User> result = new ArrayList<>();
        for (int i = 1; i < friends[0].length; i++) {
            if (friends[id][i] != 0 && friends[otherId][i] != 0) {
                result.add(users.get(i));
            }
        }
        return result;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(int id) {
        if (!users.containsKey(id)) {
            throw new GetNotFoundException("There is no such user.");
        }
        return users.get(id);
    }

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
        expandAdjacencyMatrix();
    }

    @Override
    public void deleteUserById(int id) {
        if (!users.containsKey(id)) {
            return;
        }
        users.remove(id);
    }

    @Override
    public void updateUserById(int id, User user) {
        if (!users.containsKey(id)) {
            throw new UpdateNotFoundException("There is no such user.");
        }
        users.put(id, user);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
        friends = new int[DEFAULT_MATRIX_SIZE][DEFAULT_MATRIX_SIZE];
    }

    private void expandAdjacencyMatrix() {
        if (users.size() / (double) friends.length < 0.7) {
            return;
        }
        int[][] newMatrix = new int[friends.length * 2][friends[0].length * 2];
        for (int i = 0; i < friends.length; i++) {
            for (int j = 0; j < friends[0].length; j++) {
                newMatrix[i][j] = friends[i][j];
            }
        }
        friends = newMatrix;
    }
}