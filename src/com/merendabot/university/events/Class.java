package com.merendabot.university.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public interface Class extends BaseEvent {

    /**
     * Gets all classes from the database.
     *
     * @return A List of Classes
     * @throws SQLException if an SQL Error occurs
     */
    static List<Class> getClasses() throws SQLException {
        List<Class> classes = new ArrayList<>();
        ResultSet rs = BaseEvent.getEvents(EventType.ASSIGNMENT);
        while (rs.next())
            classes.add((Class) BaseEvent.getEventFromRS(rs));
        return classes;
    }

    /**
     * Gets a class from the database with the given id.
     *
     * @param id The id of the class
     * @return A Class object if class exists, null otherwise.
     * @throws SQLException If an SQL Error occurs
     */
    static Class getClassById(int id) throws SQLException {
        return (Class) BaseEvent.getEventById(id);
    }

    /**
     * Gets a List of classes from a given week day.
     *
     * @param dayOfWeek The day of the week
     * @return A List of Classes
     * @throws SQLException If a SQL Error occurs
     */
    static List<Class> getClassesByWeekDay(DayOfWeek dayOfWeek) throws SQLException {
        List<Class> classes = new ArrayList<>();
        ResultSet rs = BaseEvent.getEventsByWeekday(dayOfWeek, EventType.CLASS);
        while (rs.next())
            classes.add((Class) BaseEvent.getEventFromRS(rs));
        return classes;
    }
}
