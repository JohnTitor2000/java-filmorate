package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Director getDirector(int id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTS WHERE DIRECT_ID = ?", id);
        if (directorRows.next()) {
            Director director = new Director();
            director.setName(directorRows.getString("name"));
            director.setId(directorRows.getInt("direct_id"));
            return director;
        } else {
            throw new NotFoundException("The Director with this id was not found");
        }
    }

    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM DIRECTS", (rs, rowNum) -> makeDirector(rs));
    }

    public void deleteDirector(int id) {
        getDirector(id);
        jdbcTemplate.update("DELETE FROM DIRECTS WHERE direct_id=?", id);
    }

    public void deleteAllDirectors() {
        jdbcTemplate.update("DELETE FROM DIRECTS");
    }

    public Director addDirector(Director director) {
        jdbcTemplate.update("INSERT INTO DIRECTS (direct_id, name) VALUES (?,?)", director.getId(), director.getName());
        return director;
    }

    public Director updateDirector(Director director) {
        getDirector(director.getId());
        jdbcTemplate.update("UPDATE DIRECTS set direct_id=?, name = ?", director.getId(), director.getName());
        return director;
    }

    public Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("direct_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}