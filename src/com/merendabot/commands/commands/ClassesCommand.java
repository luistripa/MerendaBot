package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Event;
import com.merendabot.university.events.EventClass;
import com.merendabot.university.events.EventType;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class ClassesCommand extends CommandClass {

    public ClassesCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas");
        eb.setColor(Color.WHITE);

        try {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                String fieldTitle = dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("PT"));
                StringBuilder fieldValue = new StringBuilder();

                for (Event e : EventClass.getEventsByWeekday(dayOfWeek, EventType.CLASS)) {
                    Subject subject = Subject.getSubjectById(e.getSubjectId());
                    fieldValue.append(
                            String.format(
                                    "**%s %s** (%s - %s) - [Link](%s)%n",
                                    e.getName(),
                                    subject.getShortName(),
                                    e.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    e.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    e.getLink()));
                }
                if (fieldValue.length() > 0) // Filter days that have no classes
                    eb.addField(fieldTitle, fieldValue.toString(), false);
            }
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Aulas", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            );
        }
    }
}