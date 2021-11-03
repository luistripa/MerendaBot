package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
import com.merendabot.university.subjects.Professor;
import com.merendabot.university.subjects.Subject;

import java.sql.SQLException;

public class TeachersCommand extends CommandClass {
    public TeachersCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
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
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Professores", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }
    }
}
