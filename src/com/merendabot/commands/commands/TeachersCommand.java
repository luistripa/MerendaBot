package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.university.subjects.Professor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.subjects.Subject;

import java.sql.SQLException;
import java.util.List;

public class TeachersCommand extends Command {
    public TeachersCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
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
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Professores", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
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
