package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.commands.exceptions.InvalidDateTimeFormatException;
import com.merendabot.commands.exceptions.MissingParameterException;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.events.exceptions.AssignmentNotFoundException;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.exceptions.SubjectNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AssignmentsEventCommandProcessor {

    static void processAssignment(GuildManager guild, SlashCommandEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.reply("Error. Subcommand Name is invalid").setEphemeral(true).queue();
            return;
        }
        switch (event.getSubcommandName()) {
            case "listar" -> processAssignmentList(guild, event);
            case "novo" -> processAssignmentAdd(guild, event);
            case "editar" -> processAssignmentEdit(guild, event);
            case "apagar" -> processAssignmentRemove(guild, event);
        }
    }

    static void processAssignmentList(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de trabalhos");

            List assignmentList = session.createQuery("from Assignment").list();

            for (Object o : assignmentList) {
                Assignment c = (Assignment) o;


                eb.addField(
                        c.getName() + " " + c.getSubject().getShortName() +
                                " (" + c.getDate().toLocalDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-PT")) + ")",
                        "ID: " + c.getId(),
                        false
                );
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).queue();

        }
    }

    static void processAssignmentAdd(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession();) {
            tx = session.beginTransaction();

            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping subjectMapping = event.getOption("disciplina");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping timeMapping = event.getOption("hora");
            OptionMapping linkMapping = event.getOption("link");

            if (nameMapping == null || subjectMapping == null || dateMapping == null || timeMapping == null || linkMapping == null)
                throw new MissingParameterException();

            Assignment assignment = new Assignment();

            assignment.setGuild(guild);
            assignment.setName(event.getOption("nome").getAsString());
            try {
                assignment.setDate(Date.valueOf(event.getOption("data").getAsString()));
                assignment.setTime(Time.valueOf(event.getOption("hora").getAsString()+":00"));
            } catch (IllegalArgumentException e) {
                throw new InvalidDateTimeFormatException();
            }
            assignment.setLink(event.getOption("link").getAsString());

            Subject subject = Subject.getSubjectByShortName(session, subjectMapping.getAsString());

            assignment.setSubject(subject);

            session.persist(assignment);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Trabalho", "Sucesso", "Trabalho adicionado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | SubjectNotFoundException | InvalidDateTimeFormatException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();
        }
    }

    static void processAssignmentEdit(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Assignment assignment = Assignment.getAssignmentById(session, id);

            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping timeMapping = event.getOption("hora");
            OptionMapping linkMapping = event.getOption("link");
            OptionMapping subjectMapping = event.getOption("disciplina");

            if (nameMapping != null)
                assignment.setName(nameMapping.getAsString());

            if (dateMapping != null)
                assignment.setDate(Date.valueOf(dateMapping.getAsString()));

            if (timeMapping != null)
                assignment.setTime(Time.valueOf(timeMapping.getAsString() + ":00"));

            if (linkMapping != null)
                assignment.setLink(linkMapping.getAsString());

            if (subjectMapping != null) {
                Subject subject = Subject.getSubjectByShortName(session, subjectMapping.getAsString());
                assignment.setSubject(subject);
            }

            session.update(assignment);
            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Trabalho", "Sucesso", "Trabalho editado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | AssignmentNotFoundException | SubjectNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();

        }
    }

    static void processAssignmentRemove(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Assignment c = Assignment.getAssignmentById(session, id);
            session.remove(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Trabalho", "Sucesso", "Trabalho removido com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | AssignmentNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();

        }
    }
}
