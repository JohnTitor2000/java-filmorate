package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GetNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final UserStorage userStorage;
    TreeSet<Film> mostPopularFilms = new TreeSet<>(Comparator.comparingInt(Film::getcountLikes).thenComparing(Film::getId).reversed());
    Map<Integer, Film> films = new HashMap<>();

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new GetNotFoundException("There is no such film.");
        }
        return films.get(id);
    }

    @Override
    public void addFilm(Film film) {
        films.put(film.getId(), film);
        mostPopularFilms.add(film);
    }

    @Override
    public void deleteFilmById(int id) {
        if (!films.containsKey(id)) {
            throw  new NotFoundException("There is no such film.");
        }
        films.remove(id);
        mostPopularFilms.remove(films.get(id));
    }

    @Override
    public void updateFilmById(int id, Film film) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("There is no such film.");
        }
        mostPopularFilms.remove(films.get(film.getId()));
        films.put(id, film);
        mostPopularFilms.add(film);
    }

    @Override
    public void deleteAllFilms() {
        films.clear();
        mostPopularFilms.clear();
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        if (count < 0) {
            throw new ValidationException("You can only output a positive number of popular movies.");
        }
        return mostPopularFilms.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public void addLike(int id, int userId) {
        if (!films.containsKey(id)) {
            throw new GetNotFoundException("There is no such film.");
        }
        films.get(id).getLikes().add(userId);
    }

    public void deleteLike(int id, int userId) {
        if (!films.containsKey(id) || !userStorage.getUsers().containsKey(userId)) {
            throw new GetNotFoundException("There is no such film.");
        }
        userStorage.getUserById(userId);
        films.get(id).getLikes().remove(userId);
    }
}