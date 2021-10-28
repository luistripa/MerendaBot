package university.events;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Assignment extends Event {

    static Assignment getAssignmentFromRS(ResultSet rs) throws SQLException {
        return EventClass.getEventFromRS(rs);
    }

    static ResultSet getAssignments() throws SQLException {
        return EventClass.getEvents(EventType.ASSIGNMENT);
    }
}
