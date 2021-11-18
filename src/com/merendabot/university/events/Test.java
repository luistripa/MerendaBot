package com.merendabot.university.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface Test extends BaseEvent {

    LocalDate getDueDate();

    LocalTime getDueTime();

    /**
     * Gets all tests from the database.
     *
     * @return A List of Tests
     * @throws SQLException if an SQL Error occurs
     */
    static List<Test> getTests() throws SQLException {
        List<Test> tests = new ArrayList<>();
        ResultSet rs = BaseEvent.getEvents(EventType.TEST);
        while (rs.next())
            tests.add((Test) BaseEvent.getEventFromRS(rs));
        return tests;
    }

    /**
     * Gets a test from the database with the given id.
     *
     * @param id The id of the test
     * @return A Test object if test exists, null otherwise.
     * @throws SQLException If an SQL Error occurs
     */
    static Test getTestById(int id) throws SQLException {
        return (Test) BaseEvent.getEventById(id);
    }
}
