package com.merendabot.university.events;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Test extends Event {

    static Test getTestFromRS(ResultSet rs) throws SQLException {
        return EventClass.getEventFromRS(rs);
    }

    static ResultSet getTests(Connection connection) throws SQLException {
        return EventClass.getEvents(EventType.TEST);
    }
}
