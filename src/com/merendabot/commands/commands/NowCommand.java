package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.MessageDispatcher;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Event;
import com.merendabot.university.events.EventClass;
import com.merendabot.university.events.EventType;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NowCommand extends CommandClass {
    public NowCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas a decorrer agora");

        LocalDateTime now = LocalDateTime.now();
        String fieldTitle = "Aulas";
        String fieldValue = "Não existem aulas a decorrer.";

        try {
            for (Event e : Event.getEventsByWeekday(now.getDayOfWeek(), EventType.CLASS)) {
                Subject subject = Subject.getSubjectById(e.getSubjectId());
                if (e.isNow()) {
                    fieldValue = String.format(
                            "%s %s (%s - %s) - [Link](%s)",
                            e.getName(),
                            subject.getShortName(),
                            e.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            e.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            e.getLink()
                    );
                    break;
                }
            }
            eb.addField(fieldTitle, fieldValue, false);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Agora", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }
    }
}
