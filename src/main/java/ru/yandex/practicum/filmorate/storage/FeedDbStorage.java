package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getUserFeed(Integer userId) {
        return jdbcTemplate.query("SELECT * FROM FEED WHERE USER_ID = ?",
                (rs, rowNum) -> makeEvent(rs),
                userId);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("EVENT_ID"));
        event.setUserId(rs.getInt("USER_ID"));
        event.setEntityId(rs.getInt("ENTITY_ID"));
        event.setEventType(rs.getString("EVENT_TYPE"));
        event.setOperation(rs.getString("OPERATION"));
        event.setTimestamp(rs.getLong("TMSTMP"));
        return event;
    }

    @Override
    public void addEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FEED")
                .usingGeneratedKeyColumns("EVENT_ID");
        Integer id = simpleJdbcInsert.executeAndReturnKey(eventToMap(event)).intValue();
        event.setEventId(id);
    }

    private Map<String, Object> eventToMap(Event event) {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", event.getUserId());
        values.put("ENTITY_ID", event.getEntityId());
        values.put("EVENT_TYPE", event.getEventType());
        values.put("OPERATION", event.getOperation());
        values.put("TMSTMP", event.getTimestamp());
        return values;
    }
}
