package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpa(int id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE MPA_ID = ?", id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa();
            mpa.setName(mpaRows.getString("name"));
            mpa.setId(mpaRows.getInt("mpa_id"));
            return mpa;
        } else {
            throw new NotFoundException("The mpa with this id was not found");
        }
    }

    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM MPA", (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setName(rs.getString("name"));
        mpa.setId(rs.getInt("mpa_id"));
        return mpa;
    }
}