package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class filmController {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 11, 28);
    private Map<Integer, Film> data = new HashMap<>();
    private static int currentId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return data.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        filmDataValidate(film);
        film.setId(++currentId);
        data.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film filmUpdate(@Valid @RequestBody Film film) {
        if(!data.containsKey(film.getId())) {
            throw new ValidationException("This movie does not exist.");
        }
        filmDataValidate(film);
        data.put(film.getId(), film);
        return data.get(film.getId());
    }

    private void filmDataValidate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("The release of the film cannot be earlier than December 11, 1895.");
        }
    }
}