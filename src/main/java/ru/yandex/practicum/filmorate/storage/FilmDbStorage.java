package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final DirectorDbStorage directorDbStorage;

    public List<Film> getFilmSearch(String query, String by) {
        String sqlQuery;
        if (by.equals("title")) {
            sqlQuery = "SELECT DISTINCT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASEDATE, FILMS.DURATION, " +
                    "FILMS.MPA, COUNT(LIKES.USER_ID) FROM FILMS LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                    "WHERE FILMS.NAME ILIKE ('%" + query + "%') " +
                    "GROUP BY FILMS.ID ORDER BY COUNT(LIKES.USER_ID) DESC";
        } else if (by.equals("director")) {
            sqlQuery = "SELECT DISTINCT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASEDATE, FILMS.DURATION, " +
                    "FILMS.MPA, COUNT(LIKES.USER_ID) FROM FILMS LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                    "LEFT JOIN FILM_DIRECT ON FILM_DIRECT.FILM_ID = FILMS.ID " +
                    "LEFT JOIN DIRECTS ON DIRECTS.DIRECT_ID = FILM_DIRECT.DIRECT_ID " +
                    "WHERE DIRECTS.NAME ILIKE ('%" + query + "%') " +
                    "GROUP BY FILMS.ID , FILM_DIRECT.DIRECT_ID ORDER BY COUNT(LIKES.USER_ID) DESC";
        } else if (by.equals("title,director") || by.equals("director,title")) {
            sqlQuery = "SELECT DISTINCT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASEDATE, FILMS.DURATION, " +
                    "FILMS.MPA, COUNT(LIKES.USER_ID) FROM FILMS LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                    "LEFT JOIN FILM_DIRECT ON FILM_DIRECT.FILM_ID = FILMS.ID " +
                    "LEFT JOIN DIRECTS ON DIRECTS.DIRECT_ID = FILM_DIRECT.DIRECT_ID " +
                    "WHERE FILMS.NAME ILIKE ('%" + query + "%') OR " +
                    "DIRECTS.NAME ILIKE ('%" + query + "%') " +
                    "GROUP BY FILMS.ID, FILM_DIRECT.DIRECT_ID ORDER BY COUNT(LIKES.USER_ID) DESC";
        } else if (by.isEmpty()) {
            sqlQuery = "SELECT DISTINCT FILMS.ID, FILMS.NAME, FILMS.DESCRIPTION, FILMS.RELEASEDATE, FILMS.DURATION,  " +
                    "FILMS.MPA, COUNT(LIKES.USER_ID) FROM FILMS LEFT JOIN LIKES ON FILMS.ID = LIKES.FILM_ID " +
                    "GROUP BY FILMS.ID ORDER BY COUNT(LIKES.USER_ID) DESC";
        } else {
            throw new NotFoundException("Incorrect request parameter - " + by);
        }
        List<Film> films = new ArrayList<>();
        try {
            films.addAll(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs)));
        } catch (Exception ignored) {}
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILMS WHERE id = ?", id);
        SqlRowSet filmGenreRow = jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ? ORDER BY GENRE_ID", id);
        SqlRowSet filmDirectorRow = jdbcTemplate.queryForRowSet("SELECT DIRECT_ID FROM FILM_DIRECT WHERE FILM_ID = ? ORDER BY DIRECT_ID", id);

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
            ArrayList<Director> directors = new ArrayList<>();
            while (filmDirectorRow.next()) {
                Director director = new Director();
                director.setId(filmDirectorRow.getInt("direct_id"));
                director.setName(directorDbStorage.getDirector(director.getId()).getName());
                directors.add(director);
            }
            film.setGenres(genres);
            film.setDirectors(directors);
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
        if (film.getDirectors().size() != 0) {
            film.getDirectors().forEach(director -> jdbcTemplate.update("INSERT INTO FILM_DIRECT (FILM_ID, DIRECT_ID) VALUES (?,?)", filmIdRow.getInt("id"), director.getId()));
        }
    }

    @Override
    public void deleteFilmById(int id) {
        jdbcTemplate.update("DELETE FROM FILMS WHERE id=?", id);
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", id);
        jdbcTemplate.update("DELETE FROM FILM_DIRECTS WHERE FILM_ID=?", id);
    }

    @Override
    public void updateFilmById(int id, Film film) {
        film.setGenres((ArrayList<Genre>) film.getGenres().stream().distinct().collect(Collectors.toList()));
        film.setDirectors((ArrayList<Director>) film.getDirectors().stream().distinct().collect(Collectors.toList()));
        getFilmById(film.getId());
        jdbcTemplate.update("UPDATE FILMS SET name=?, description=?, releaseDate=?, duration=?, MPA=? WHERE id=?",
                film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), id);
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID=?", id);
        film.getGenres().forEach(o -> jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?,?)", id, o.getId()));
        jdbcTemplate.update("DELETE FROM FILM_DIRECT WHERE FILM_ID=?", id);
        film.getDirectors().forEach(o -> jdbcTemplate.update("INSERT INTO FILM_DIRECT (FILM_ID, DIRECT_ID) VALUES (?,?)", id, o.getId()));
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM FILMS_DIRECTS");
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return jdbcTemplate.query("SELECT FILMS.id FROM FILMS LEFT JOIN LIKES ON FILMS.id = LIKES.film_id GROUP BY FILMS.id ORDER BY COUNT(LIKES.user_id) DESC LIMIT ?", (rs, rowNum) -> getFilmById(rs.getInt("id")), count);
    }

    @Override
    public List<Film> getFilmsByDirectIdSortedByYear(int directId) {
        directorDbStorage.getDirector(directId);
        String sqlRequest = "SELECT * FROM FILMS " +
                "LEFT JOIN LIKES ON FILMS.id = LIKES.film_id " +
                "LEFT JOIN FILM_GENRES ON FILM_GENRES.FILM_ID = FILMS.ID " +
                "LEFT JOIN FILM_DIRECT on FILMS.ID = FILM_DIRECT.FILM_ID " +
                "WHERE FILM_DIRECT.DIRECT_ID = ? " +
                "order by RELEASEDATE;";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> getFilmById(rs.getInt("id")), directId);
    }

    public List<Film> getFilmsByDirectIdSortedByLikes(int directId) {
        directorDbStorage.getDirector(directId);
        return jdbcTemplate.query("SELECT FILMS.id FROM FILMS LEFT JOIN FILM_DIRECT ON FILMS.ID=FILM_DIRECT.FILM_ID LEFT JOIN LIKES ON FILMS.id = LIKES.film_id WHERE FILM_DIRECT.DIRECT_ID=?  GROUP BY FILMS.id ORDER BY COUNT(LIKES.user_id) DESC ", (rs, rowNum) -> getFilmById(rs.getInt("id")), directId);

    }

    @Override
    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM FILMS", (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        SqlRowSet filmGenreRow = jdbcTemplate.queryForRowSet("SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = ?", rs.getInt("id"));
        SqlRowSet filmDirectorRow = jdbcTemplate.queryForRowSet("SELECT DIRECT_ID FROM FILM_DIRECT WHERE FILM_ID = ?", rs.getInt("id"));
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
        ArrayList<Director> directors = new ArrayList<>();
        while (filmDirectorRow.next()) {
            directors.add(directorDbStorage.getDirector(filmDirectorRow.getInt("DIRECT_ID")));
        }
        film.setDirectors(directors);
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