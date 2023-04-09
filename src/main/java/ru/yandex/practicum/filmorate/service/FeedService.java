package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserService userService;

    public FeedService(FeedStorage feedStorage, UserService userService) {
        this.feedStorage = feedStorage;
        this.userService = userService;
    }

    public void addEvent(Event event) {
        feedStorage.addEvent(event);
    }

    public List<Event> getUserFeed(Integer userId) {
        userService.getUserById(userId);
        return feedStorage.getUserFeed(userId);
    }
}
