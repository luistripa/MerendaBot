package com.merendabot.commands.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.university.subjects.Professor;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class TeachersCommand extends Command {

    public TeachersCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Professores");

        String fieldTitle = "";
        StringBuilder fieldValue = new StringBuilder();
        try {
            tx = session.beginTransaction();

            Subject currentSubject = null;
            for (Professor professor : Professor.getProfessors(session)) {
                Subject subject = professor.getSubject();

                if (currentSubject == null) { // First item
                    currentSubject = subject;
                    fieldTitle = subject.getShortName() + " - " + subject.getFullName();
                    fieldValue.append(professor.getName()).append(" - ").append(professor.getEmail()).append('\n');

                } else if (currentSubject.getShortName().equals(subject.getShortName())) { // Current item has same subject as previous
                    currentSubject = subject;
                    fieldValue.append(professor.getName()).append(" - ").append(professor.getEmail()).append('\n');

                } else { // Subject is different
                    eb.addField(fieldTitle, fieldValue.toString(), false);
                    currentSubject = subject;
                    fieldTitle = subject.getShortName() + " - " + subject.getFullName();
                    fieldValue.setLength(0);
                    fieldValue.append(professor.getName()).append(" - ").append(professor.getEmail()).append('\n');
                }
            }

            if (fieldValue.isEmpty())
                eb.setDescription("Não existem professores para mostrar.");
            else
                eb.addField(fieldTitle, fieldValue.toString(), false);

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Professores", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
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
