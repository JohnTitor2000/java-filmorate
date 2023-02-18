package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 11, 28);
    private final FilmService filmService;
    private static int currentId = 1;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Received a request to receive all movies.");
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Received a request to create a movie.");
        filmDataValidate(film);
        film.setId(currentId++);
        filmService.addFilm(film);
        return film;
    }

    @PutMapping
    public Film filmUpdate(@Valid @RequestBody Film film) {
        log.debug("Received a request to update the movie with ID: " + film.getId());
        filmDataValidate(film);
        filmService.updateFilm(film);
        return filmService.getFilmById(film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        log.debug("Received a request to add a like to the movie " + id + " from a user with an ID: " + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        log.debug("Received a request to delete a like to the movie " + id + " from a user with an ID: " + userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam Optional<Integer> count) {
        log.debug("Received a request for the most popular movies");
        return filmService.getMostPopularFilms(count.orElse(10));
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @Positive int id) {
        log.debug("Received a request to receive a movie with ID: " + id);
        return filmService.getFilmById(id);
    }

    private void filmDataValidate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("The release of the film cannot be earlier than December 11, 1895.");
        }
    }
}