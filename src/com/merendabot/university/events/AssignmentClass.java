package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class AssignmentClass extends BaseEventClass implements Assignment {

    public AssignmentClass(int id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, EventType.ASSIGNMENT, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
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
