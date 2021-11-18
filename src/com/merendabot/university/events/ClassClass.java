package com.merendabot.university.events;

import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClassClass extends BaseEventClass implements Class {

    public ClassClass(int id, String name, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, String link, Subject subject) {
        super(id, EventType.CLASS, EventInterval.WEEKLY, name, startDate, endDate, startTime, endTime, link, subject);
    }

    @Override
    public void addToEmbed(EmbedBuilder embedBuilder) {
        super.addToEmbed(embedBuilder);
    }
}
