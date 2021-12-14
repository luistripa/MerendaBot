package com.merendabot.university.events;

import com.merendabot.Merenda;
import com.merendabot.university.subjects.Subject;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Assignment extends BaseEventClass {

    public Assignment(int id, String guild_id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, guild_id, EventType.ASSIGNMENT, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
    }

    public LocalDate getDueDate() {
        return getStartDate();
    }

    public LocalTime getDueTime() {
        return getStartTime();
    }

    static Assignment getAssignmentFromRS(ResultSet rs) throws SQLException {
        return new Assignment(
                rs.getInt(1),                                           // ID
                rs.getString(2),                                        // Guild ID
                rs.getString(5),                                        // Name
                rs.getDate(6).toLocalDate(),                            // Start date (Due date)
                rs.getTime(8).toLocalTime(),                            // Start time (Due time)
                rs.getString(10),                                       // Link
                Subject.getSubjectById(rs.getInt(11))                   // Subject ID
        );
    }

    /**
     * Gets all assignments from the database.
     *
     * @return A List of Assignments
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Assignment> getAssignments() throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event ge " +
                        "inner join guild_subject gs on ge.subject_id = gs.id " +
                        "where event_type::text = ?" +
                        "order by ge.start_date, ge.start_time;"
        );
        statement.setString(1, EventType.ASSIGNMENT.toString().toLowerCase());
        ResultSet rs = statement.executeQuery();
        while (rs.next())
            assignments.add(getAssignmentFromRS(rs));
        return assignments;
    }
}
