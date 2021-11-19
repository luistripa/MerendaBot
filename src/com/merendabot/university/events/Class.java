package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Class extends BaseEventClass {

    public Class(int id, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String link, Subject subject) {
        super(id, EventType.CLASS, EventInterval.WEEKLY, name, startDate, endDate, startTime, endTime, link, subject);
    }

    public void addToEmbed(EmbedBuilder embedBuilder) {
        super.addToEmbed(embedBuilder);
    }

    /**
     * Gets all classes from the database.
     *
     * @return A List of Classes
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Class> getClasses() throws SQLException {
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
    public static Class getClassById(int id) throws SQLException {
        return (Class) BaseEvent.getEventById(id);
    }

    /**
     * Gets a List of classes from a given week day.
     *
     * @param dayOfWeek The day of the week
     * @return A List of Classes
     * @throws SQLException If a SQL Error occurs
     */
    public static List<Class> getClassesByWeekDay(DayOfWeek dayOfWeek) throws SQLException {
        List<Class> classes = new ArrayList<>();
        ResultSet rs = BaseEvent.getEventsByWeekday(dayOfWeek, EventType.CLASS);
        while (rs.next())
            classes.add((Class) BaseEvent.getEventFromRS(rs));
        return classes;
    }
}
