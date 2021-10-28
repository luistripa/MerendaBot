package commands.commands;

import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.events.EventClass;
import university.events.EventType;
import university.events.Test;
import university.subjects.Subject;
import university.subjects.SubjectClass;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestsCommand extends CommandClass {
    public TestsCommand(String category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Testes");
        eb.setColor(Color.WHITE);

        String fieldTitle = "Testes";
        StringBuilder fieldValue = new StringBuilder();

        try {
            ResultSet rs = EventClass.getEvents(EventType.TEST);
            while (rs.next()) {
                Test test = Test.getTestFromRS(rs);
                Subject subject = Subject.getSubjectFromRS(rs, 11);

                if (test.getStartDate().isBefore(LocalDate.now()))
                    continue;

                fieldValue.append(
                        String.format(
                                "**%s %s** - %s (%s)\n",
                                test.getName(),
                                subject.getShortName(),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                test.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );
            }
            eb.addField(fieldTitle, fieldValue.toString(), false);
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Testes", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            );
        }


    }
}
