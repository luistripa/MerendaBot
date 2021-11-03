package com.merendabot.university.events;


import com.merendabot.university.Merenda;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface Event {

    /**
     * Gets the id of the event.
     * This id is the primary key of the database.
     *
     * @return The id of the event
     */
    int getId();

    /**
     * Gets the event type of this event.
     *
     * @return An EventType Object
     */
    EventType getEventType();

    /**
     * Gets the interval type of this event.
     *
     * @return An EventInterval Object
     */
    EventInterval getEventInterval();

    /**
     * Gets the name of the event.
     *
     * @return The name of the event
     */
    String getName();

    /**
     * Gets the start date of the event.
     * {@link EventInterval#SINGLE Single Events} have the same start date and end date.
     *
     * @return A LocalTime object
     */
    LocalDate getStartDate();

    /**
     * Gets the end date of the event.
     * {@link EventInterval#SINGLE Single Events} have the same start date and end date.
     *
     * @return A LocalTime object
     */
    LocalDate getEndDate();

    /**
     * Gets the start time of the event.
     *
     * @return A LocalTime Object
     */
    LocalTime getStartTime();

    /**
     * Gets the end time of the event.
     * {@link Assignment Assignments} do not have an end time
     *
     * @return A LocalTime Object
     */
    LocalTime getEndTime();

    /**
     * Gets the link of the event.
     *
     * @return The link of the event
     */
    String getLink();

    /**
     * Gets the id of the subject linked to this event.
     * This id is the primary key of the subject in the database.
     *
     * @return 0 if no subject linked, other positive integer otherwise.
     */
    int getSubjectId();

    /**
     * Checks if the event is happening now.
     *
     * @return True if now, False otherwise.
     */
    boolean isNow();

    /**
     * Gets a new event from a given ResultSet.
     * The ResultSet must be positioned using the next() method before calling this method.
     *
     * @param rs The ResultSet to fetch from.
     * @return An EventClass Object
     * @throws SQLException If any SQL database errors occur.
     */
    static EventClass getEventFromRS(ResultSet rs) throws SQLException {
        return new EventClass(
                rs.getInt(1),                                           // ID
                EventType.valueOf(rs.getString(2).toUpperCase()),       // EventType
                EventInterval.valueOf(rs.getString(3).toUpperCase()),   // EventInterval
                rs.getString(4),                                        // Name
                rs.getDate(5).toLocalDate(),                            // Start date
                rs.getDate(6),                                          // End date
                rs.getTime(7).toLocalTime(),                            // Start time
                rs.getTime(8),                                          // End time
                rs.getString(9),                                        // Link
                rs.getInt(10)                                           // Subject ID
        );
    }

    /**
     * Gets all events from the database.
     *
     * @return A List of Events
     * @throws SQLException if an SQL Error occurs
     */
    static List<Event> getEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event ue " +
                        "inner join university_subject us on us.id = ue.subject_id " +
                        "order by ue.start_date, ue.start_time;"
        )) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                events.add(getEventFromRS(rs));
            }
            return events;
        }
    }

    /**
     * Gets all events that have a specified type.
     *
     * @param eventType The type of the event
     * @return A List of Events
     * @throws SQLException if an SQL Error occurs
     */
    static List<Event> getEvents(EventType eventType) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event ue " +
                        "inner join university_subject us on ue.subject_id = us.id " +
                        "where event_type::text = ? " +
                        "order by ue.start_date, ue.start_time;"
        )) {
            statement.setString(1, eventType.toString().toLowerCase());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                events.add(getEventFromRS(rs));
            }
            return events;
        }
    }

    /**
     * Gets an Event by id.
     *
     * @param id The id of the event
     * @return An Event object if event is found, null otherwise.
     * @throws SQLException If an SQL Error occurs
     */
    @Nullable
    static Event getEventById(int id) throws SQLException {
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event " +
                        "order by start_date, start_time, id=?"
        )) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getEventFromRS(rs);
            return null;
        }
    }

    /**
     * Gets all events on the current day.
     *
     * @return A List of Events
     * @throws SQLException if an SQL Error occurs
     */
    static List<Event> getEventsToday() throws SQLException {
        List<Event> events = new ArrayList<>();
        LocalDateTime dayStart = LocalDateTime.now().withSecond(0).withMinute(0).withHour(0);
        LocalDateTime dayEnd = LocalDateTime.now().withSecond(59).withMinute(59).withHour(23);

        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event " +
                        "where start_date >= ? and start_date <= ? " +
                        "order by start_date, start_time;"
        )) {
            statement.setTimestamp(1, Timestamp.valueOf(dayStart));
            statement.setTimestamp(2, Timestamp.valueOf(dayEnd));
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                events.add(getEventFromRS(rs));
            return events;
        }
    }

    /**
     * Gets all events on the current day with a specific type.
     *
     * @param type The type of the event
     * @return A List of Events
     * @throws SQLException if an SQL Error occurs
     */
    static List<Event> getEventsToday(EventType type) throws SQLException {
        List<Event> events = new ArrayList<>();
        LocalDateTime dayStart = LocalDateTime.now().withSecond(0).withMinute(0).withHour(0);
        LocalDateTime dayEnd = LocalDateTime.now().withSecond(59).withMinute(59).withHour(23);

        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event " +
                        "where start_date >= ? and start_date <= ? and event_type = ? " +
                        "order by start_date, start_time;"
        )) {
            statement.setTimestamp(1, Timestamp.valueOf(dayStart));
            statement.setTimestamp(2, Timestamp.valueOf(dayEnd));
            statement.setString(3, type.toString().toLowerCase());
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                events.add(getEventFromRS(rs));
            return events;
        }
    }

    /**
     * Gets all events for a given day of the week and by type.
     *
     * @param dayOfWeek A DayOfWeek object
     * @param eventType The event type
     * @return A List of Events
     * @throws SQLException if an SQL Error occurs
     */
    static List<Event> getEventsByWeekday(DayOfWeek dayOfWeek, EventType eventType) throws SQLException {
        List<Event> events = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * from university_event ue\n" +
                        "inner join university_subject us on ue.subject_id = us.id\n" +
                        "where ue.end_date >= now() and\n" +
                        "map_weekday(extract(dow from ue.start_date)) = ? and\n" +
                        "    ue.event_type::text = ?\n" +
                        "order by ue.start_date, ue.start_time"
        )) {
            statement.setInt(1, dayOfWeek.getValue());
            statement.setString(2, eventType.toString().toLowerCase());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                events.add(Event.getEventFromRS(rs));
            }
            return events;
        }
    }
}
