package com.merendabot.commands.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.subjects.Subject;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AssigmentsCommand extends Command {

    private static final Logger logger = Logger.getLogger("main-log");

    public AssigmentsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Trabalhos");

        String fieldTitle = "Trabalhos";
        StringBuilder fieldValue = new StringBuilder();

        // Used to avoid creation of unnecessary SubjectClass duplicates
        Map<Integer, Subject> subjectCache = new HashMap<>();

        try {
            tx = session.beginTransaction();

            for (Assignment assignment : Assignment.getAssignments(session)) {
                Subject subject;
                subject = assignment.getSubject();

                if (subject == null) {
                    logger.severe("Could not find subject with id " + assignment.getSubject().getId());
                    event.replyEmbeds(
                            getErrorEmbed("Assignments", "Erro", "Ocorreu um erro. Contacta um administrador.")
                    ).setEphemeral(true).queue();
                    return;
                }
                subjectCache.put(subject.getId(), subject);

                if (assignment.getDate().toLocalDate().isBefore(LocalDate.now()))
                    continue;

                fieldValue.append(String.format(
                                "**%s %s** - %s (%s)%n",
                                assignment.getName(),
                                subject.getShortName(),
                                assignment.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                assignment.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );
            }
            if (fieldValue.length() == 0) {
                eb.setDescription("Não existem trabalhos para mostrar.");

            } else {
                eb.addField(fieldTitle, fieldValue.toString(), false);
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Assignments", "Erro SQL", "Ocorreu um erro. Contacta o admininstrador.")
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
