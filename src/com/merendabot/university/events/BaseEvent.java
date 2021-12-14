package com.merendabot.university.events;

import com.merendabot.Merenda;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface BaseEvent {

    int getId();

    String getGuildId();

    EventType getType();

    EventInterval getInterval();

    String getName();

    LocalDate getStartDate();

    LocalDate getEndDate();

    LocalTime getStartTime();

    LocalTime getEndTime();

    String getLink();

    Subject getSubject();

    boolean isNow();

    void addToEmbed(EmbedBuilder embedBuilder);

    static BaseEventClass getEventFromRS(ResultSet rs) throws SQLException {
        return new BaseEventClass(
                rs.getInt(1),                                           // ID
                rs.getString(2),                                        // Guild ID
                EventType.valueOf(rs.getString(3).toUpperCase()),       // EventType
                EventInterval.valueOf(rs.getString(4).toUpperCase()),   // EventInterval
                rs.getString(5),                                        // Name
                rs.getDate(6).toLocalDate(),                            // Start date
                rs.getDate(7),                                          // End date
                rs.getTime(8).toLocalTime(),                            // Start time
                rs.getTime(9),                                          // End time
                rs.getString(10),                                       // Link
                Subject.getSubjectById(rs.getInt(11))                   // Subject ID
        );
    }

    static ResultSet getEvents() throws SQLException {
        List<BaseEvent> events = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event ge " +
                        "inner join guild_subject gs on gs.id = ge.subject_id " +
                        "order by ge.start_date, ge.start_time;"
        );
        return statement.executeQuery();
    }

    static ResultSet getEvents(EventType eventType) throws SQLException {
        List<BaseEvent> events = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event ge " +
                        "inner join guild_subject gs on ge.subject_id = gs.id " +
                        "where event_type::text = ?" +
                        "order by ge.start_date, ge.start_time;"
        );
        statement.setString(1, eventType.toString().toLowerCase());
        return statement.executeQuery();
    }

    @Nullable
    static BaseEvent getEventById(int id) throws SQLException {
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event " +
                        "where id=? "+
                        "order by start_date, start_time, id"
        )) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getEventFromRS(rs);
            return null;
        }
    }

    static ResultSet getEventsByWeekday(DayOfWeek dayOfWeek, EventType eventType) throws SQLException {
        List<BaseEvent> events = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * from guild_event ge\n" +
                        "inner join guild_subject gs on ge.subject_id = gs.id\n" +
                        "where ge.end_date >= now() and\n" +
                        "extract(isodow from ge.start_date) = ? and\n" +
                        "    ge.event_type::text = ?\n" +
                        "order by ge.start_date, ge.start_time"
        );
        statement.setInt(1, dayOfWeek.getValue());
        statement.setString(2, eventType.toString().toLowerCase());
        return statement.executeQuery();
    }
}
