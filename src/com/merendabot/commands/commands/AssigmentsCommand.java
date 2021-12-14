package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.subjects.Subject;

import java.sql.SQLException;
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
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Trabalhos");

        String fieldTitle = "Trabalhos";
        StringBuilder fieldValue = new StringBuilder();

        // Used to avoid creation of unnecessary SubjectClass duplicates
        Map<Integer, Subject> subjectCache = new HashMap<>();

        try {
            for (Assignment assignment : Assignment.getAssignments()) {
                Subject subject;
                subject = subjectCache.get(assignment.getSubject().getId());

                if (subject == null) {
                    subject = Subject.getSubjectById(assignment.getSubject().getId());

                    if (subject == null) {
                        logger.severe("Could not find subject with id " + assignment.getSubject().getId());
                        event.replyEmbeds(
                                getErrorEmbed("Assignments", "Erro", "Ocorreu um erro. Contacta um administrador.")
                        ).setEphemeral(true).queue();
                        return;
                    }
                    subjectCache.put(subject.getId(), subject);
                }

                if (assignment.getStartDate().isBefore(LocalDate.now()))
                    continue;

                fieldValue.append(String.format(
                                "**%s %s** - %s (%s)%n",
                                assignment.getName(),
                                subject.getShortName(),
                                assignment.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                assignment.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                );
            }
            if (fieldValue.length() == 0)
                fieldValue.append("Não existem trabalhos.");

            eb.addField(fieldTitle, fieldValue.toString(), false);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Assignments", "Erro SQL", "Ocorreu um erro. Contacta o admininstrador.")
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
