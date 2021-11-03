package com.merendabot.university.events;

import java.sql.SQLException;
import java.util.List;

public interface Test extends Event {

    /**
     * Gets all Tests from the database.
     *
     * @return A List of Tests
     * @throws SQLException if an SQL Error occurs
     */
    static List<Test> getTests() throws SQLException {
        return (List<Test>) (List<? extends Event>) Event.getEvents(EventType.TEST);
    }
}
