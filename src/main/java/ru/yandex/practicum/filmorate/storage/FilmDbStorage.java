package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class FilmDbStorage implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage){
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE id = ?", id);
        SqlRowSet filmGenreRow = jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ? ORDER BY GENRE_ID", id);
        if (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getInt("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setDuration(filmRows.getInt("duration"));
            film.setReleaseDate(filmRows.getDate("releaseDate").toLocalDate());
            film.setMpa(mpaDbStorage.getMpa(filmRows.getInt("mpa")));
            ArrayList<Genre> genres = new ArrayList<>();
            while (filmGenreRow.next()) {
                Genre genre = new Genre();
                genre.setId(filmGenreRow.getInt("genre_id"));
                genre.setName(genreDbStorage.getGenre(genre.getId()).getName());
                genres.add(genre);
            }
            film.setGenres(genres);
            return film;
        } else {
            throw new NotFoundException("The movie with this id was not found");
        }
    }

    @Override
    public void addFilm(Film film) {
        jdbcTemplate.update("INSERT INTO FILMS (name, duration, description, releaseDate, MPA) VALUES (?,?,?,?,?)",
                film.getName(), film.getDuration(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getMpa().getId());
        SqlRowSet filmIdRow = jdbcTemplate.queryForRowSet("SELECT id FROM FILMS WHERE name = ?", film.getName());
        filmIdRow.next();
        if (film.getGenres().size() != 0) {
            film.getGenres().forEach(genre -> jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?,?)", filmIdRow.getInt("id"), genre.getId()));
        }
    }

    @Override
    public void deleteFilmById(int id) {
        jdbcTemplate.update("DELETE FROM FILMS WHERE id=?", id);
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", id);
    }

    @Override
    public void updateFilmById(int id, Film film) {
        film.setGenres((ArrayList<Genre>) film.getGenres().stream().distinct().collect(Collectors.toList()));
        getFilmById(film.getId());
        jdbcTemplate.update("UPDATE FILMS SET name=?, description=?, releaseDate=?, duration=?, MPA=? WHERE id=?",
                film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), id);
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", id);
        film.getGenres().forEach(o -> jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?,?)", id, o.getId()));
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return jdbcTemplate.query("SELECT FILMS.id FROM FILMS LEFT JOIN LIKES ON FILMS.id = LIKES.film_id GROUP BY FILMS.id ORDER BY COUNT(LIKES.user_id) DESC LIMIT ?", (rs, rowNum) -> getFilmById(rs.getInt("id")),count);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        SqlRowSet filmGenreRow = jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?", rs.getInt("id"));
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setDuration(rs.getInt("duration"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa"));
        mpa.setName(mpaDbStorage.getMpa(mpa.getId()).getName());
        film.setMpa(mpa);
        ArrayList<Genre> genres = new ArrayList<>();
        while (filmGenreRow.next()) {
            genres.add(genreDbStorage.getGenre(filmGenreRow.getInt("GENRE_ID")));
        }
        film.setGenres(genres);
        return film;
    }

    @Override
    public void addLike(int id, int userId) {
        jdbcTemplate.update("INSERT INTO LIKES (user_id, film_id) VALUES (?,?)", userId, id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE user_id=? AND film_id=?", userId, id);
    }
}