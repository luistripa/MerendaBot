package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.university.events.Class;
import com.merendabot.university.subjects.Subject;
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
            event.reply("Error. Subcommand Name is invalid").queue();
            return;
        }
        switch (event.getSubcommandName()) {
            case "listar":
                processClassList(guild, event);
                break;

            case "novo":
                processClassAdd(guild, event);
                break;

            case "editar":
                processClassEdit(guild, event);
                break;

            case "apagar":
                processClassRemove(guild, event);
                break;
        }
    }

    static void processClassList(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
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

        } finally {
            session.close();
        }
    }

    static void processClassAdd(GuildManager guild, SlashCommandEvent event) {
        Session session = null;
        Transaction tx = null;

        Class c = new Class();
        c.setGuild(guild);

        try {
            c.setName(event.getOption("nome").getAsString());
            c.setDate(Date.valueOf(event.getOption("data").getAsString()));
            c.setEndDate(Date.valueOf(event.getOption("data_fim").getAsString()));
            c.setTime(Time.valueOf(event.getOption("hora").getAsString()+":00"));
            c.setEndTime(Time.valueOf(event.getOption("hora_fim").getAsString()+":00"));
            c.setLink(event.getOption("link").getAsString());

            session = Merenda.getInstance().getFactory().openSession();
            tx = session.beginTransaction();

            Subject subject = (Subject) session.createQuery("from Subject where shortName = :short")
                    .setParameter("short", event.getOption("disciplina").getAsString()).uniqueResult();

            // TODO: subject == null?

            c.setSubject(subject);

            session.persist(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Aula", "Sucesso", "Aula adicionada com sucesso.")
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

    static void processClassEdit(GuildManager guild, SlashCommandEvent event) {
        if (event.getOptions().size() == 1) {
            event.replyEmbeds(
                    Command.getErrorEmbed(
                            "Erro",
                            "Número de parâmetros invalidos",
                            "É necessário pelo menos mais um parâmetro para editar um aula."
                    )
            ).queue();
            return;
        }

        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            int id = (int) event.getOption("id").getAsLong();

            Class c = (Class) session.createQuery("from Class where id = :id").setParameter("id", id).uniqueResult();

            OptionMapping name = event.getOption("nome");
            OptionMapping date = event.getOption("data");
            OptionMapping endDate = event.getOption("data_fim");
            OptionMapping time = event.getOption("hora");
            OptionMapping endTime = event.getOption("hora_fim");
            OptionMapping link = event.getOption("link");
            OptionMapping subject = event.getOption("disciplina");

            if (name != null)
                c.setName(name.getAsString());

            if (date != null)
                c.setDate(Date.valueOf(date.getAsString()));

            if (endDate != null)
                c.setEndDate(Date.valueOf(endDate.getAsString()));

            if (time != null)
                c.setTime(Time.valueOf(time.getAsString()+":00"));

            if (endTime != null)
                c.setEndTime(Time.valueOf(endTime.getAsString()+":00"));

            if (link != null)
                c.setLink(link.getAsString());

            if (subject != null) {
                Subject s = (Subject) session.createQuery("from Subject where shortName = :short")
                        .setParameter("short", event.getOption("disciplina").getAsString()).uniqueResult();

                c.setSubject(s);
            }

            session.update(c);
            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Aula", "Sucesso", "Aula editada com sucesso.")
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

    static void processClassRemove(GuildManager guild, SlashCommandEvent event) {
        int id = (int)event.getOption("id").getAsLong();

        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Class c = session.find(Class.class, id);
            session.remove(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Aula", "Sucesso", "Aula removida com sucesso.")
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
