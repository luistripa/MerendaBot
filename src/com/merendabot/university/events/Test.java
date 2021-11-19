package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Test extends BaseEventClass {

    public Test(int id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, EventType.TEST, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
    }


    public LocalDate getDueDate() {
        return getStartDate();
    }

    public LocalTime getDueTime() {
        return getStartTime();
    }

    /**
     * Gets all tests from the database.
     *
     * @return A List of Tests
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Test> getTests() throws SQLException {
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
    public static Test getTestById(int id) throws SQLException {
        return (Test) BaseEvent.getEventById(id);
    }
}
