package commands.commands;

import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.subjects.Professor;
import university.subjects.ProfessorClass;
import university.subjects.Subject;
import university.subjects.SubjectClass;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeachersCommand extends CommandClass {
    public TeachersCommand(String category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Professores");

        String fieldTitle = "";
        StringBuilder fieldValue = new StringBuilder();
        try {
            ResultSet rs = Professor.getProfessors();
            while (rs.next()) {
                Professor professor = Professor.getProfessorFromRS(rs);
                Subject subject = Subject.getSubjectFromRS(rs, 5);

                if (fieldTitle.equals("")) {
                    fieldTitle = subject.getShortName();
                    fieldValue.setLength(0);

                } else if (!fieldTitle.equals(subject.getShortName())) {
                    eb.addField(fieldTitle, fieldValue.toString(), false);
                    fieldTitle = subject.getShortName();
                    fieldValue.setLength(0);
                }
                fieldValue.append(String.format("%s - %s", professor.getName(), professor.getEmail()));
            }
            eb.addField(fieldTitle, fieldValue.toString(), false);
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Professores", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            );
        }
    }
}
