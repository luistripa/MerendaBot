package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.events.Class;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.SubjectClass;
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
            event.reply("Error. Subcommand Name is invalid").queue();
            return;
        }
        switch (event.getSubcommandName()) {
            case "listar":
                processAssignmentList(guild, event);
                break;

            case "novo":
                processAssignmentAdd(guild, event);
                break;

            case "editar":
                processAssignmentEdit(guild, event);
                break;

            case "apagar":
                processAssignmentRemove(guild, event);
                break;
        }
    }

    static void processAssignmentList(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
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

        } finally {
            session.close();
        }
    }

    static void processAssignmentAdd(GuildManager guild, SlashCommandEvent event) {
        Session session = null;
        Transaction tx = null;

        Assignment c = new Assignment();
        c.setGuild(guild);

        try {
            c.setName(event.getOption("nome").getAsString());
            c.setDate(Date.valueOf(event.getOption("data").getAsString()));
            c.setTime(Time.valueOf(event.getOption("hora").getAsString()+":00"));
            c.setLink(event.getOption("link").getAsString());

            session = Merenda.getInstance().getFactory().openSession();
            tx = session.beginTransaction();

            SubjectClass subject = (SubjectClass) session.createQuery("from SubjectClass where shortName = :short")
                    .setParameter("short", event.getOption("disciplina").getAsString()).uniqueResult();

            // TODO: subject == null?

            c.setSubject(subject);

            session.persist(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Trabalho", "Sucesso", "Trabalho adicionado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (NullPointerException e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.reply("Falta um parâmetro!").setEphemeral(true).queue();

        } catch (IllegalArgumentException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Formato da data/hora inválido.", "O formato da data deve ser YYYY-MM-DD e da hora deve ser HH:MM")
            ).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();

        } finally {
            if (session != null)
                session.close();
        }
    }

    static void processAssignmentEdit(GuildManager guild, SlashCommandEvent event) {
        if (event.getOptions().size() == 1) {
            event.replyEmbeds(
                    Command.getErrorEmbed(
                            "Erro",
                            "Número de parâmetros invalidos",
                            "É necessário pelo menos mais um parâmetro para editar um trabalho."
                    )
            ).queue();
            return;
        }

        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            int id = (int) event.getOption("id").getAsLong();

            Assignment c = (Assignment) session.createQuery("from Class where id = :id").setParameter("id", id).uniqueResult();

            OptionMapping name = event.getOption("nome");
            OptionMapping date = event.getOption("data");
            OptionMapping time = event.getOption("hora");
            OptionMapping link = event.getOption("link");
            OptionMapping subject = event.getOption("disciplina");

            if (name != null)
                c.setName(name.getAsString());

            if (date != null)
                c.setDate(Date.valueOf(date.getAsString()));

            if (time != null)
                c.setTime(Time.valueOf(time.getAsString()+":00"));

            if (link != null)
                c.setLink(link.getAsString());

            if (subject != null) {
                SubjectClass s = (SubjectClass) session.createQuery("from SubjectClass where shortName = :short")
                        .setParameter("short", event.getOption("disciplina").getAsString()).uniqueResult();
                // TODO: subject == null?
                c.setSubject(s);
            }

            session.update(c);
            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Trabalho", "Sucesso", "Trabalho editado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (IllegalArgumentException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Formato da data/hora inválido.", "O formato da data deve ser YYYY-MM-DD e da hora deve ser HH:MM")
            ).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            e.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();

        } finally {
            session.close();
        }

        session.close();
    }

    static void processAssignmentRemove(GuildManager guild, SlashCommandEvent event) {
        int id = (int)event.getOption("id").getAsLong();

        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Assignment c = session.find(Assignment.class, id);
            session.remove(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Trabalho", "Sucesso", "Trabalho removido com sucesso.")
            ).setEphemeral(true).queue();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", e.getMessage())
            ).setEphemeral(true).queue();

        } finally {
            session.close();
        }
    }
}
