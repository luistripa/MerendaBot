package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.events.Class;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
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
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas");
        eb.setColor(Color.WHITE);

        try {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                String fieldTitle = dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("PT"));
                StringBuilder fieldValue = new StringBuilder();

                for (Class c : Class.getClassesByWeekDay(dayOfWeek)) {
                    fieldValue.append(
                            String.format(
                                    "**%s %s** (%s - %s) - [Link](%s)%n",
                                    c.getName(),
                                    c.getSubject().getShortName(),
                                    c.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    c.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    c.getLink()));
                }
                if (fieldValue.length() > 0) // Filter days that have no classes
                    eb.addField(fieldTitle, fieldValue.toString(), false);
            }
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Aulas", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }
    }
}
