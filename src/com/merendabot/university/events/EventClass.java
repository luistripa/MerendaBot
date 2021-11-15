package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an event.
 *
 * Events are points or intervals in time where something happens, like a class, test or assignment delivery.
 */
public class EventClass implements Event, Test, Assignment {

    private int id;
    private EventType eventType;
    private EventInterval eventInterval;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String link;
    private int subjectId;

    public EventClass(
            int id,
            EventType eventType,
            EventInterval eventInterval,
            String name,
            LocalDate startDate,
            Date endDate,
            LocalTime startTime,
            Time endTime,
            String link,
            int subjectId) {
        this.id = id;
        this.eventType = eventType;
        this.eventInterval = eventInterval;
        this.name = name;
        this.startDate = startDate;
        if (endDate == null)
            this.endDate = null;
        else
            this.endDate = endDate.toLocalDate();
        this.startTime = startTime;
        if (endTime == null)
            this.endTime = null;
        else
            this.endTime = endTime.toLocalTime();
        this.link = link;
        this.subjectId = subjectId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public EventInterval getEventInterval() {
        return eventInterval;
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
    public int getSubjectId() {
        return subjectId;
    }

    @Override
    public boolean isNow() {
        LocalDateTime now = LocalDateTime.now();
        if (this.getEventInterval().equals(EventInterval.WEEKLY)) {
            return now.toLocalTime().isAfter(this.getStartTime()) &&
                    now.toLocalTime().isBefore(this.getEndTime()) &&
                    (now.toLocalDate().isAfter(this.startDate) || now.toLocalDate().isEqual(this.startDate)) &&
                    (now.toLocalDate().isBefore(this.endDate) || now.toLocalDate().isEqual(this.endDate)) &&
                    now.getDayOfWeek().equals(this.startDate.getDayOfWeek());

        } else if (this.getEventInterval().equals(EventInterval.SINGLE)) {
            return now.toLocalDate().equals(this.getStartDate()) &&
                    now.toLocalTime().isAfter(startTime) &&
                    now.toLocalTime().isBefore(endTime);

        } else {
            return false;
        }

    }

    /**
     * Generates the embed for a given event
     *
     * @return A MessageEmbed object
     */
    @Override
    public void addToEmbed(EmbedBuilder embedBuilder) {
        embedBuilder.addField(
                String.format("%s %s", this.getName(), Subject.getSubjectById(this.subjectId).getShortName()),
                String.format(
                        "%s - %s",
                        this.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        this.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                ),
                false
        );
    }
}
