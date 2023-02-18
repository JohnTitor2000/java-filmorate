package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private static int currentId = 1;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Received a request to receive all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable @Positive int id) {
        log.debug("Received a request to get a user by ID " + id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("A request to create a user was received");
        nameValidation(user);
        user.setId(currentId++);
        userService.addUser(user);
        return user;
    }

    @PutMapping
    public User userUpdate(@Valid @RequestBody User user) {
        log.debug("User by ID: " + user.getId() + " update request received");
        nameValidation(user);
        userService.updateUser(user);
        return userService.getUserById(user.getId());
    }

    @DeleteMapping("/{id}")
    public void userRemove(@PathVariable @Positive int id) {
        log.debug("Received a request to delete the user by ID: " + id);
        userService.deleteUser(userService.getUserById(id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        log.debug("Received a request to add friends with IDs: " + id + ", " + friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        log.debug("Received a request to delete  friends with IDs: " + id + ", " + friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable @Positive int id) {
        log.debug("A request was received to get all the user's friends with an ID: " + id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@Positive @PathVariable int id,
                                       @Positive @PathVariable int otherId) {
        log.debug("A request was received to get mutual friends of users with an IDs: " + id + ", " + otherId);
        return  userService.getCommonFriends(id, otherId);
    }

    private void nameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}