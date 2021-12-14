package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.university.events.Test;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class TestsCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "Testes";

    private static final Logger logger = Logger.getLogger("main-log");

    public TestsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(COMMAND_FRIENDLY_NAME);
        eb.setColor(Color.WHITE);

        StringBuilder fieldValue = new StringBuilder();

        try {
            for (Test test : Test.getTests()) {
                Subject subject = test.getSubject();

                if (subject == null) {
                    logger.severe("Could not find subject with id "+subject.getId());
                    event.replyEmbeds(
                            getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "Ocorreu um erro. Contacta um administrador.")
                    ).setEphemeral(true).queue();
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

            if (fieldValue.length() == 0)
                fieldValue.append("Não existem testes.");

            eb.addField(COMMAND_FRIENDLY_NAME, fieldValue.toString(), false);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).setEphemeral(true).queue();
        }
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Um botão foi pressionado, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Uma seleção foi feita, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }
}
