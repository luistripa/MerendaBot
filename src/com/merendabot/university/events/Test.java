package com.merendabot.university.events;

import java.sql.SQLException;
import java.util.List;

public interface Test extends Event {

    static List<Test> getTests() throws SQLException {
        return (List<Test>) (List<? extends Event>) EventClass.getEvents(EventType.TEST);
    }
}
