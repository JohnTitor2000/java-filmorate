package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMOstPopularFilms(count);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }

    public void deleteFilmById(Film film) {
        filmStorage.deleteFilmById(film.getId());
    }

    public void updateFilm(Film film) {
        filmStorage.updateFilmById(film.getId(), film);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int id, int userId) {
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        filmStorage.deleteLike(id, userId);
    }
}
