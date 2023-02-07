    package ru.yandex.practicum.filmorate.controller;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import ru.yandex.practicum.filmorate.model.User;
    import ru.yandex.practicum.filmorate.model.ValidationException;

    import javax.validation.Valid;
    import java.util.Collection;
    import java.util.HashMap;
    import java.util.Map;

    @Slf4j
    @RestController
    @RequestMapping("/films")
    public class userController {
        private Map<Integer, User> data = new HashMap<>();
        private static int currentId = 0;

        @GetMapping
        public Collection<User> findAll() {
            return data.values();
        }

        @PostMapping
        public User create(@Valid @RequestBody User user) {
            nameValidation(user);
            user.setId(++currentId);
            data.put(user.getId(), user);
            return user;
        }

        @PutMapping
        public User userUpdate(@Valid @RequestBody User user) {
            if(!data.containsKey(user.getId())) {
                throw new ValidationException("This user does not exist.");
            }
            nameValidation(user);
            data.put(user.getId(), user);
            return data.get(user.getId());
        }

        private void nameValidation(User user) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        }
    }