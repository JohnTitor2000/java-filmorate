package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film getFilmById(int id);

    void addFilm(Film film);

    void deleteFilmById(int id);

    void updateFilmById(int id, Film film);

    void deleteAllFilms();

    List<Film> getMostPopularFilms(int count);

    Collection<Film> getAllFilms();

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);
}
