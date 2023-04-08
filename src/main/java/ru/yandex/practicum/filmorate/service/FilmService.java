package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Validated
@Slf4j
@Service
public class FilmService {

    private static int currentId = 1;
    private static final LocalDate FIRST_FILM_RELEASE = LocalDate.of(1895, 11, 28);
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> getMostPopularFilms(int count) {
        log.debug("Received a request for the most popular movies");
        return filmStorage.getMostPopularFilms(count);
    }

    public Film getFilmById(@Positive int id) {
        log.debug("Received a request to receive a movie with ID: " + id);
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(@Valid Film film) {
        filmDataValidate(film);
        film.setId(currentId++);
        log.debug("Received a request to create a movie.");
        filmStorage.addFilm(film);
        return film;
    }


    public void deleteFilmById(Film film) {
        filmStorage.deleteFilmById(film.getId());
    }

    public void deleteFilmById(int id) {
        filmStorage.deleteFilmById(id);
    }

    public void updateFilm(@Valid Film film) {
        filmDataValidate(film);
        if (film.getId() <= 0) {
            throw new NotFoundException("The movie with this id was not found.");
        }
        log.debug("Received a request to update the movie with ID: " + film.getId());
        filmStorage.updateFilmById(film.getId(), film);
    }

    public Collection<Film> getAllFilms() {
        log.debug("Received a request to receive all movies.");
        return filmStorage.getAllFilms();
    }

    public void addLike(@Positive int id, @Positive int userId) {
        log.debug("Received a request to add a like to the movie " + id + " from a user with an ID: " + userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        if (id <= 0) {
            throw new NotFoundException("No movie with this id was found");
        }
        if (userId <= 0) {
            throw new NotFoundException("No user with this id was found");
        }
        log.debug("Received a request to delete a like to the movie " + id + " from a user with an ID: " + userId);
        filmStorage.deleteLike(id, userId);
    }

    private void filmDataValidate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE)) {
            throw new ValidationException("The release of the film cannot be earlier than December 11, 1895.");
        }
    }

    public List<Film> getFilmsByDirectIdSortedByYear(int directId) {
        log.debug("Received a request to get all films od director with id: {} sorted by year", directId);
        return filmStorage.getFilmsByDirectIdSortedByYear(directId);
    }

    public List<Film> getFilmsByDirectIdSortedByLikes(int directId) {
        log.debug("Received a request to get all films od director with id: {} sorted by count of likes", directId);
        return filmStorage.getFilmsByDirectIdSortedByLikes(directId);
    }

    public List<Film> getFilmSearch(String query, String by) {
        return filmStorage.getFilmSearch(query, by);
    }
}