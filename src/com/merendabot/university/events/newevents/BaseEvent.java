package com.merendabot.university.events.newevents;

import com.merendabot.university.Merenda;
import com.merendabot.university.events.EventInterval;
import com.merendabot.university.events.EventType;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public interface BaseEvent {

    int getId();

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
                EventType.valueOf(rs.getString(2).toUpperCase()),       // EventType
                EventInterval.valueOf(rs.getString(3).toUpperCase()),   // EventInterval
                rs.getString(4),                                        // Name
                rs.getDate(5).toLocalDate(),                            // Start date
                rs.getDate(6).toLocalDate(),                            // End date
                rs.getTime(7).toLocalTime(),                            // Start time
                rs.getTime(8).toLocalTime(),                            // End time
                rs.getString(9),                                        // Link
                Subject.getSubjectById(rs.getInt(10))                   // Subject ID
        );
    }

    static ResultSet getEvents() throws SQLException {
        List<BaseEvent> events = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event ue " +
                        "inner join university_subject us on us.id = ue.subject_id " +
                        "order by ue.start_date, ue.start_time;"
        );
        return statement.executeQuery();
    }

    static ResultSet getEvents(EventType eventType) throws SQLException {
        List<BaseEvent> events = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event ue " +
                        "inner join university_subject us on ue.subject_id = us.id " +
                        "where event_type::text = ? " +
                        "order by ue.start_date, ue.start_time;"
        );
        statement.setString(1, eventType.toString().toLowerCase());
        return statement.executeQuery();
    }

    @Nullable
    static BaseEvent getEventById(int id) throws SQLException {
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from university_event " +
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
}
