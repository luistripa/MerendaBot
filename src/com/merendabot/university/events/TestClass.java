package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class TestClass extends BaseEventClass implements Test {

    public TestClass(int id, String name, LocalDate dueDate, LocalTime dueTime, String link, Subject subject) {
        super(id, EventType.TEST, EventInterval.SINGLE, name, dueDate, (Date) null, dueTime, null, link, subject);
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
