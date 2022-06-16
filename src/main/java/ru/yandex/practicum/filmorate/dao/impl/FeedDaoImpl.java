package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FeedDaoImpl implements FeedDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_FEED_BY_USER_ID = "" +
            "SELECT time_stamp, user_id, event_type, operation, event_id, entity_id " +
            "FROM feeds " +
            "WHERE user_id IN (SELECT friend_id " +
            "   FROM friends " +
            "   WHERE user_id = ?)";
    private static final String SQL_GET_OP_ID = "SELECT id FROM operations " +
            "WHERE name = ?";
    private static final String SQL_GET_OP_NAME = "SELECT name FROM operations " +
            "WHERE id = ?";
    private static final String SQL_GET_EV_ID = "SELECT id FROM event_types " +
            "WHERE name = ?";
    private static final String SQL_GET_EV_NAME = "SELECT name FROM event_types " +
            "WHERE id = ?";
    private static final String SQL_ADD_FEED = "" +
            "INSERT INTO feeds(time_stamp, user_id, event_type, operation, entity_id) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_GET_USER = "SELECT user_id FROM users WHERE user_id=?";

    @Autowired
    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeedByUserId(Integer id) {
        return jdbcTemplate.query(SQL_GET_FEED_BY_USER_ID, (rs, rowNum) -> makeFeed(rs), id);
    }

    @Override
    public void addFeed(Feed feed) {
        Integer eventTypeId = jdbcTemplate.queryForObject(SQL_GET_EV_ID,
                (rs, rowNum) -> rs.getInt("id"),
                feed.getEventType().toString());
        Integer operationId = jdbcTemplate.queryForObject(SQL_GET_OP_ID,
                (rs, rowNum) -> rs.getInt("id"),
                feed.getOperation().toString());
        jdbcTemplate.update(SQL_ADD_FEED, feed.getTimestamp(),
                feed.getUserId(), eventTypeId, operationId, feed.getEntityId());
    }

    private Feed makeFeed(ResultSet rs) throws SQLException {
        long timestamp = rs.getInt("time_stamp");
        Integer userId = rs.getInt("user_id");
        Integer eventTypeId = rs.getInt("event_type");
        Integer operationId = rs.getInt("operation");
        Integer eventId = rs.getInt("event_id");
        Integer entityId = rs.getInt("entity_id");
        String eventType = jdbcTemplate.queryForObject(SQL_GET_EV_NAME,
                (rs_ev, rowNum) -> rs_ev.getString("name"), eventTypeId);
        String operation = jdbcTemplate.queryForObject(SQL_GET_OP_NAME,
                (rs_op, rowNum) -> rs_op.getString("name"), operationId);
        return new Feed(timestamp, userId, eventType,
                operation, eventId, entityId);
    }
}
