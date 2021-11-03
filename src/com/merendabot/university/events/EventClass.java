package com.merendabot.university.events;

import org.jetbrains.annotations.Nullable;
import com.merendabot.university.Merenda;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event.
 *
 * Events are points or intervals in time where something happens, like a class, test or assignment delivery.
 */
public class EventClass implements Event, Test, Assignment {

    private int id;
    private EventType eventType;
    private EventInterval eventInterval;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String link;
    private int subjectId;

    public EventClass(
            int id,
            EventType eventType,
            EventInterval eventInterval,
            String name,
            LocalDate startDate,
            Date endDate,
            LocalTime startTime,
            Time endTime,
            String link,
            int subjectId) {
        this.id = id;
        this.eventType = eventType;
        this.eventInterval = eventInterval;
        this.name = name;
        this.startDate = startDate;
        if (endDate == null)
            this.endDate = null;
        else
            this.endDate = endDate.toLocalDate();
        this.startTime = startTime;
        if (endTime == null)
            this.endTime = null;
        else
            this.endTime = endTime.toLocalTime();
        this.link = link;
        this.subjectId = subjectId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public EventInterval getEventInterval() {
        return eventInterval;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public int getSubjectId() {
        return subjectId;
    }

    @Override
    public boolean isNow() {
        LocalDateTime now = LocalDateTime.now();
        if (this.getEventInterval().equals(EventInterval.WEEKLY)) {
            return now.toLocalTime().isAfter(this.getStartTime()) &&
                    now.toLocalTime().isBefore(this.getEndTime()) &&
                    (now.toLocalDate().isAfter(this.startDate) || now.toLocalDate().isEqual(this.startDate)) &&
                    (now.toLocalDate().isBefore(this.endDate) || now.toLocalDate().isEqual(this.endDate)) &&
                    now.getDayOfWeek().equals(this.startDate.getDayOfWeek());

        } else if (this.getEventInterval().equals(EventInterval.SINGLE)) {
            return now.toLocalDate().equals(this.getStartDate()) &&
                    now.toLocalTime().isAfter(startTime) &&
                    now.toLocalTime().isBefore(endTime);

        } else {
            return false;
        }

    }

    /**
     * Gets a new event from a given ResultSet.
     * The ResultSet must be positioned using the next() method before calling this method.
     *
     * @param rs The ResultSet to fetch from.
     * @return An EventClass Object
     * @throws SQLException If any SQL database errors occur.
     */
    public static EventClass getEventFromRS(ResultSet rs) throws SQLException {
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
     */
    public static List<Event> getEvents() throws SQLException {
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

    public static List<Event> getEvents(EventType eventType) throws SQLException {
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

    @Nullable public static Event getEventById(int id) throws SQLException {
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
     * Gets event on the current day.
     *
     * @return A ResultSet Object
     */
    public static List<Event> getEventsToday() throws SQLException {
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

    public static List<Event> getEventsToday(EventType type) throws SQLException {
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

    public static List<Event> getEventsByWeekday(DayOfWeek dayOfWeek, EventType eventType) throws SQLException {
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
                events.add(EventClass.getEventFromRS(rs));
            }
            return events;
        }
    }
}
