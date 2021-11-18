package com.merendabot.university.events.newevents;

import com.merendabot.university.events.EventInterval;
import com.merendabot.university.events.EventType;
import com.merendabot.university.subjects.Subject;

import java.time.LocalDate;
import java.time.LocalTime;

public class AssignmentClass extends BaseEventClass implements Assignment {

    public AssignmentClass(int id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, EventType.ASSIGNMENT, EventInterval.SINGLE, name, dueDate, null, dueTime, null, link, subject);
    }

    @Override
    public LocalDate getDueDate() {
        return getStartDate();
    }

    @Override
    public LocalTime getDueTime() {
        return getStartTime();
    }
}
