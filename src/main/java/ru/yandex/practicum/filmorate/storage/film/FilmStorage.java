package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    public Film getFilmById(int id);

    public void addFilm(Film film);

    public void deleteFilmById(int id);

    public void updateFilmById(int id, Film film);

    public void deleteAllFilms();

    public List<Film> getMostPopularFilms(int count);

    public Collection<Film> getAllFilms();

    public void addLike(int id, int userId);

    public void deleteLike(int id, int userId);
}
