package com.merendabot.university.events;

import java.sql.SQLException;
import java.util.List;

public interface Assignment extends Event {

    static List<Assignment> getAssignments() throws SQLException {

        return (List<Assignment>) (List<? extends Event>) Event.getEvents(EventType.ASSIGNMENT);
    }
}
