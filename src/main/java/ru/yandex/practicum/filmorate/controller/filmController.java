package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class filmController {
    private static final LocalDate VALID_DATA = LocalDate.of(1895, 11, 28);
    Map<Integer, Film> data = new HashMap<>();
    private static int currentId = 0;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return data.values();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getName() == null || film.getDescription() == null ||film.getName().isBlank() || film.getDuration() < 1) {
            throw new ValidationException("Ошибка ввода.");
        }
        if (film.getReleaseDate().isBefore(VALID_DATA)) {
            throw new ValidationException("Релиз фильма не может быть раньше 11 декабря 1895 года.");
        }
        film.setId(++currentId);
        data.put(film.getId(), film);
        return film;
    }

    @PutMapping("/films")
    public Film filmUpdate(@Valid @RequestBody Film film) throws ValidationException {
        if(data.containsKey(film.getId())) {
            data.put(film.getId(), film);
            return data.get(film.getId());
        } else {
            throw new ValidationException("Данного фильма не существует.");
        }
    }
}