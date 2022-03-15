package com.merendabot.commands.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.university.events.Test;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.subjects.Subject;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class TestsCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "Testes";

    private static final Logger logger = Logger.getLogger("main-log");

    public TestsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(COMMAND_FRIENDLY_NAME);
        eb.setColor(Color.WHITE);

        StringBuilder fieldValue = new StringBuilder();

        try {
            tx = session.beginTransaction();

            for (Test test : Test.getTests(session)) {
                Subject subject = test.getSubject();

                if (subject == null) {
                    logger.severe("Could not find subject for test with id "+test.getId());
                    event.replyEmbeds(
                            getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "Ocorreu um erro. Contacta um administrador.")
                    ).setEphemeral(true).queue();
                    return;
                }

                if (test.getDate().toLocalDate().isBefore(LocalDate.now()))
                    continue;

                fieldValue.append(
                        String.format(
                                "**%s %s** - %s (%s)%n",
                                test.getName(),
                                subject.getShortName(),
                                test.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                test.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );
            }

            if (fieldValue.length() == 0) {
                eb.setDescription("Não existem testes para mostrar.");

            } else {
                eb.addField(COMMAND_FRIENDLY_NAME, fieldValue.toString(), false);
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).setEphemeral(true).queue();

        } finally {
            session.close();
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
