package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.commands.exceptions.InvalidDateTimeFormatException;
import com.merendabot.commands.exceptions.MissingParameterException;
import com.merendabot.university.events.Class;
import com.merendabot.university.events.exceptions.ClassNotFoundException;
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

public class ClassesEventCommandProcessor {

    static void processClass(GuildManager guild, SlashCommandEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.reply("Error. Subcommand Name is invalid").setEphemeral(true).queue();
            return;
        }
        switch (event.getSubcommandName()) {
            case "listar" -> processClassList(guild, event);
            case "novo" -> processClassAdd(guild, event);
            case "editar" -> processClassEdit(guild, event);
            case "apagar" -> processClassRemove(guild, event);
        }
    }

    static void processClassList(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de aulas");

            List classList = session.createQuery("from Class").list();

            for (Object o : classList) {
                Class c = (Class) o;


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

    static void processClassAdd(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping subjectMapping = event.getOption("disciplina");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping endDateMapping = event.getOption("data_fim");
            OptionMapping timeMapping = event.getOption("hora");
            OptionMapping endTimeMapping = event.getOption("hora_fim");
            OptionMapping linkMapping = event.getOption("link");

            if (nameMapping == null || subjectMapping == null || dateMapping == null || endDateMapping == null || timeMapping == null || endTimeMapping == null || linkMapping == null)
                throw new MissingParameterException();

            Class c = new Class();
            c.setGuild(guild);

            c.setName(nameMapping.getAsString());
            c.setDate(Date.valueOf(dateMapping.getAsString()));
            c.setEndDate(Date.valueOf(endDateMapping.getAsString()));
            try {
                c.setTime(Time.valueOf(timeMapping.getAsString()+":00"));
                c.setEndTime(Time.valueOf(endTimeMapping.getAsString()+":00"));
            } catch (IllegalArgumentException e) {
                throw new InvalidDateTimeFormatException();
            }

            c.setLink(linkMapping.getAsString());

            Subject subject = Subject.getSubjectByShortName(session, subjectMapping.getAsString());

            c.setSubject(subject);

            session.persist(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Aula", "Sucesso", "Aula adicionada com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | InvalidDateTimeFormatException e) {
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

    static void processClassEdit(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {

            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Class c = Class.getClassById(session, id);

            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping endDateMapping = event.getOption("data_fim");
            OptionMapping timeMapping = event.getOption("hora");
            OptionMapping endTimeMapping = event.getOption("hora_fim");
            OptionMapping linkMapping = event.getOption("link");
            OptionMapping subjectMapping = event.getOption("disciplina");

            if (nameMapping != null)
                c.setName(nameMapping.getAsString());

            if (dateMapping != null)
                c.setDate(Date.valueOf(dateMapping.getAsString()));

            if (endDateMapping != null)
                c.setEndDate(Date.valueOf(endDateMapping.getAsString()));

            if (timeMapping != null)
                c.setTime(Time.valueOf(timeMapping.getAsString() + ":00"));

            if (endTimeMapping != null)
                c.setEndTime(Time.valueOf(endTimeMapping.getAsString() + ":00"));

            if (linkMapping != null)
                c.setLink(linkMapping.getAsString());

            if (subjectMapping != null) {
                Subject subject = Subject.getSubjectByShortName(session, subjectMapping.getAsString());

                c.setSubject(subject);
            }

            session.update(c);
            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Aula", "Sucesso", "Aula editada com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | ClassNotFoundException | SubjectNotFoundException e) {
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

    static void processClassRemove(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Class c = Class.getClassById(session, id);
            session.remove(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Aula", "Sucesso", "Aula removida com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | ClassNotFoundException e) {
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
