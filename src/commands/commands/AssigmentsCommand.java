package commands.commands;

import commands.CommandCategory;
import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.events.Assignment;
import university.subjects.Subject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AssigmentsCommand extends CommandClass {
    public AssigmentsCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Trabalhos");

        String fieldTitle = "Trabalhos";
        StringBuilder fieldValue = new StringBuilder();

        // Used to avoid creation of unnecessary SubjectClass duplicates
        Map<Integer, Subject> subjectCache = new HashMap<>();

        try {
            ResultSet rs = Assignment.getAssignments();
            while (rs.next()) {
                Assignment assignment = Assignment.getAssignmentFromRS(rs);
                Subject subject = subjectCache.get(assignment.getSubjectId());

                if (subject == null) { // Subject not found in cache
                    subject = Subject.getSubjectFromRS(rs, 11);
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
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Assignments", "Erro SQL", "Ocorreu um erro. Contacta o admininstrador.")
            );
        }
    }
}
