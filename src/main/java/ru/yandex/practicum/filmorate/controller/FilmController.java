package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.aop.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmService.addFilm(film);
        return film;
    }

    @PutMapping
    public Film filmUpdate(@RequestBody Film film) {
        filmService.updateFilm(film);
        return filmService.getFilmById(film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    @Feed
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Feed
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam Optional<Integer> count) {
        return filmService.getMostPopularFilms(count.orElse(10));
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/director/{id}")
    public List<Film> getFilmsByDirectIdSortedByYear(@PathVariable int id, @RequestParam String sortBy) {
        if (sortBy.equals("year")) {
            return filmService.getFilmsByDirectIdSortedByYear(id);
        } else if (sortBy.equals("likes")) {
            return filmService.getFilmsByDirectIdSortedByLikes(id);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    void deleteFilmById(@PathVariable int id) {
        filmService.deleteFilmById(id);
    }

    @GetMapping("/search")
    public List<Film> getFilmSearch(@RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "") String by) {
        return filmService.getFilmSearch(query, by);
    }
}