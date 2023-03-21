package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenre(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRES WHERE GENRE_ID = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre();
            genre.setName(genreRows.getString("name"));
            genre.setId(genreRows.getInt("genre_id"));
            return genre;
        } else {
            throw new NotFoundException("The genre with this id was not found");
        }
    }

    public List<Genre> getAllGenre() {
        return jdbcTemplate.query("SELECT * FROM GENRES", (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setName(rs.getString("name"));
        genre.setId(rs.getInt("genre_id"));
        return genre;
    }
}