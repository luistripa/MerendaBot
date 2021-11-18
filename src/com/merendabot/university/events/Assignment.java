package com.merendabot.university.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface Assignment extends BaseEvent {

    LocalDate getDueDate();

    LocalTime getDueTime();

    /**
     * Gets all assignments from the database.
     *
     * @return A List of Assignments
     * @throws SQLException if an SQL Error occurs
     */
    static List<Assignment> getAssignments() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        ResultSet rs = BaseEvent.getEvents(EventType.ASSIGNMENT);
        while (rs.next())
            assignments.add((Assignment) BaseEvent.getEventFromRS(rs));
        return assignments;
    }

    /**
     * Gets an assignment from the database with the given id.
     *
     * @param id The id of the assignment
     * @return An Assignment object if assignment exists, null otherwise.
     * @throws SQLException If an SQL Error occurs
     */
    static Assignment getAssignmentById(int id) throws SQLException {
        return (Assignment) BaseEvent.getEventById(id);
    }
}
