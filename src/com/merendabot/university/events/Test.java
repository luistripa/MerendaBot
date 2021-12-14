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

public class Test extends BaseEventClass {

    public Test(int id, String guild_id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, guild_id, EventType.TEST, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
    }


    public LocalDate getDueDate() {
        return getStartDate();
    }

    public LocalTime getDueTime() {
        return getStartTime();
    }

    static Test getTestFromRS(ResultSet rs) throws SQLException {
        return new Test(
                rs.getInt(1),                                           // ID
                rs.getString(2),                                        // Guild ID
                rs.getString(5),                                        // Name
                rs.getDate(6).toLocalDate(),                            // Start date
                rs.getTime(8).toLocalTime(),                            // Start time
                rs.getString(10),                                       // Link
                Subject.getSubjectById(rs.getInt(11))                   // Subject ID
        );
    }

    /**
     * Gets all tests from the database.
     *
     * @return A List of Tests
     * @throws SQLException if an SQL Error occurs
     */
    public static List<Test> getTests() throws SQLException {
        List<Test> tests = new ArrayList<>();
        PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_event ge " +
                        "inner join guild_subject gs on gs.id = ge.subject_id " +
                        "order by ge.start_date, ge.start_time;"
        );
        ResultSet rs = statement.executeQuery();
        while (rs.next())
            tests.add(getTestFromRS(rs));
        return tests;
    }
}
