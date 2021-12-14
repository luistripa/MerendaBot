package com.merendabot.university.events;

import com.merendabot.Merenda;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Class extends BaseEventClass {

    public Class(int id, String guild_id, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String link, Subject subject) {
        super(id, guild_id, EventType.CLASS, EventInterval.WEEKLY, name, startDate, endDate, startTime, endTime, link, subject);
    }

    public void addToEmbed(EmbedBuilder embedBuilder) {
        super.addToEmbed(embedBuilder);
    }

    static Class getClassFromRS(ResultSet rs) throws SQLException {
        return new Class(
                rs.getInt(1),                                           // ID
                rs.getString(2),                                        // Guild ID
                rs.getString(5),                                        // Name
                rs.getDate(6).toLocalDate(),                            // Start date
                rs.getDate(7).toLocalDate(),                            // End date
                rs.getTime(8).toLocalTime(),                            // Start time
                rs.getTime(9).toLocalTime(),                            // End time
                rs.getString(10),                                       // Link
                Subject.getSubjectById(rs.getInt(11))                   // Subject ID
        );
    }

    /**
     * Gets all classes from the database.
     *
     * @return A List of Classes
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Class> getClasses() throws SQLException {
        List<Class> classes = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event ge " +
                        "inner join guild_subject gs on ge.subject_id = gs.id " +
                        "where event_type::text = ?" +
                        "order by ge.start_date, ge.start_time;"
        );
        statement.setString(1, EventType.CLASS.toString().toLowerCase());
        ResultSet rs = statement.executeQuery();
        while (rs.next())
            classes.add(Class.getClassFromRS(rs));
        return classes;
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
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * from guild_event ge\n" +
                        "inner join guild_subject gs on ge.subject_id = gs.id\n" +
                        "where ge.end_date >= now() and\n" +
                        "extract(isodow from ge.start_date) = ? and\n" +
                        "    ge.event_type::text = ?\n" +
                        "order by ge.start_date, ge.start_time"
        );
        statement.setInt(1, dayOfWeek.getValue());
        statement.setString(2, EventType.CLASS.toString().toLowerCase());
        ResultSet rs = statement.executeQuery();
        while (rs.next())
            classes.add(getClassFromRS(rs));
        return classes;
    }
}
