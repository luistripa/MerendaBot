package com.merendabot.university.events.newevents;

import com.merendabot.university.events.EventInterval;
import com.merendabot.university.events.EventType;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BaseEventClass implements BaseEvent {

    private int id;
    private EventType type;
    private EventInterval interval;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String link;
    private Subject subject;

    public BaseEventClass(int id, EventType type, EventInterval interval, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String link, Subject subject) {
        this.id = id;
        this.type = type;
        this.interval = interval;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.link = link;
        this.subject = subject;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public EventInterval getInterval() {
        return interval;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    @Override
    public boolean isNow() {
        LocalDateTime now = LocalDateTime.now();
        if (this.getInterval().equals(EventInterval.WEEKLY)) {
            return now.toLocalTime().isAfter(this.getStartTime()) &&
                    now.toLocalTime().isBefore(this.getEndTime()) &&
                    (now.toLocalDate().isAfter(this.startDate) || now.toLocalDate().isEqual(this.startDate)) &&
                    (now.toLocalDate().isBefore(this.endDate) || now.toLocalDate().isEqual(this.endDate)) &&
                    now.getDayOfWeek().equals(this.startDate.getDayOfWeek());

        } else if (this.getInterval().equals(EventInterval.SINGLE)) {
            return now.toLocalDate().equals(this.getStartDate()) &&
                    now.toLocalTime().isAfter(startTime) &&
                    now.toLocalTime().isBefore(endTime);

        } else {
            return false;
        }
    }

    @Override
    public void addToEmbed(EmbedBuilder embedBuilder) {
        embedBuilder.addField(
                String.format("Evento %s", subject.getShortName()),
                String.format(
                        "%s - %s (%s - %s)",
                        this.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        this.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                ),
                false
        );
    }
}
