package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.merendabot.university.Merenda;
import com.merendabot.university.subjects.Professor;
import com.merendabot.university.subjects.Subject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeachersCommand extends CommandClass {
    public TeachersCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Professores");

        String fieldTitle = "";
        StringBuilder fieldValue = new StringBuilder();
        try {
            for (Professor professor : Professor.getProfessors()) {
                Subject subject = Subject.getSubjectById(professor.getSubjectId());

                if (fieldTitle.isEmpty()) {
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
