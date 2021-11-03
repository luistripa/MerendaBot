package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.events.Event;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.subjects.Subject;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AssigmentsCommand extends CommandClass {

    private static final Logger logger = Logger.getLogger("main-log");

    public AssigmentsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Trabalhos");

        String fieldTitle = "Trabalhos";
        StringBuilder fieldValue = new StringBuilder();

        // Used to avoid creation of unnecessary SubjectClass duplicates
        Map<Integer, Subject> subjectCache = new HashMap<>();

        try {
            for (Event assignment : Assignment.getAssignments()) {
                Subject subject;
                subject = subjectCache.get(assignment.getSubjectId());

                if (subject == null) {
                    subject = Subject.getSubjectById(assignment.getSubjectId());

                    if (subject == null) {
                        logger.severe("Could not find subject with id " + assignment.getSubjectId());
                        event.getChannel().sendMessageEmbeds(
                                getErrorEmbed("Assignments", "Erro", "Ocorreu um erro. Contacta um administrador.")
                        ).queue();
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
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Assignments", "Erro SQL", "Ocorreu um erro. Contacta o admininstrador.")
            ).queue();
        }
    }
}
