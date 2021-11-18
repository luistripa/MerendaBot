package com.merendabot.university.events.newevents;

import com.merendabot.university.events.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
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
}
