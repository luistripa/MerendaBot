package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Assignment extends BaseEventClass {

    public Assignment(int id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, EventType.ASSIGNMENT, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
    }

    public LocalDate getDueDate() {
        return getStartDate();
    }

    public LocalTime getDueTime() {
        return getStartTime();
    }

    /**
     * Gets all assignments from the database.
     *
     * @return A List of Assignments
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Assignment> getAssignments() throws SQLException {
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
    public static Assignment getAssignmentById(int id) throws SQLException {
        return (Assignment) BaseEvent.getEventById(id);
    }
}
