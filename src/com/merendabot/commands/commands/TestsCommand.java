package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Test;
import com.merendabot.university.subjects.Subject;

import java.awt.Color;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class TestsCommand extends CommandClass {

    private static final String COMMAND_FRIENDLY_NAME = "Testes";

    private static final Logger logger = Logger.getLogger("main-log");

    public TestsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(COMMAND_FRIENDLY_NAME);
        eb.setColor(Color.WHITE);

        StringBuilder fieldValue = new StringBuilder();

        try {
            for (Test test : Test.getTests()) {
                Subject subject = Subject.getSubjectById(test.getSubjectId());

                if (subject == null) {
                    logger.severe("Could not find subject with id "+test.getSubjectId());
                    event.getChannel().sendMessageEmbeds(
                            getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "Ocorreu um erro. Contacta um administrador.")
                    ).queue();
                    return;
                }

                if (test.getStartDate().isBefore(LocalDate.now()))
                    continue;

                fieldValue.append(
                        String.format(
                                "**%s %s** - %s (%s)%n",
                                test.getName(),
                                subject.getShortName(),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                test.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );
            }
            eb.addField(COMMAND_FRIENDLY_NAME, fieldValue.toString(), false);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }


    }
}
