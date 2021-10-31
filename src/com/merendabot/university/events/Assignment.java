package com.merendabot.university.events;

import java.sql.SQLException;
import java.util.List;

public interface Assignment extends Event {

    static List<Event> getAssignments() throws SQLException {

        return EventClass.getEvents(EventType.ASSIGNMENT);
    }
}
