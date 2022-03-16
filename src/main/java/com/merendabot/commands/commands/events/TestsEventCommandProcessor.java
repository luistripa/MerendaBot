package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.commands.exceptions.MissingParameterException;
import com.merendabot.university.events.Test;
import com.merendabot.university.events.exceptions.TestNotFoundException;
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

public class TestsEventCommandProcessor {

    static void processTest(GuildManager guild, SlashCommandEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.reply("Error. Subcommand Name is invalid").queue();
            return;
        }
        switch (event.getSubcommandName()) {
            case "listar":
                processTestList(guild, event);
                break;

            case "novo":
                processTestAdd(guild, event);
                break;

            case "editar":
                processTestEdit(guild, event);
                break;

            case "apagar":
                processTestRemove(guild, event);
                break;
        }
    }

    static void processTestList(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de testes");

            List testList = session.createQuery("from Test").list();

            for (Object o : testList) {
                Test c = (Test) o;


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

    static void processTestAdd(GuildManager guild, SlashCommandEvent event) {
        Session session = null;
        Transaction tx = null;

        Test c = new Test();
        c.setGuild(guild);

        try {
            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping subjectMapping = event.getOption("disciplina");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping startTimeMapping = event.getOption("hora");
            OptionMapping endTimeMapping = event.getOption("hora_fim");
            OptionMapping linkMapping = event.getOption("link");

            if (nameMapping == null || subjectMapping == null || dateMapping == null || startTimeMapping == null || endTimeMapping == null || linkMapping == null)
                throw new MissingParameterException();


            c.setName(nameMapping.getAsString());
            c.setDate(Date.valueOf(dateMapping.getAsString()));
            c.setTime(Time.valueOf(startTimeMapping.getAsString()+":00"));
            c.setEndTime(Time.valueOf(endTimeMapping.getAsString()+":00"));
            c.setLink(linkMapping.getAsString());

            session = Merenda.getInstance().getFactory().openSession();
            tx = session.beginTransaction();

            String subjectShortName = subjectMapping.getAsString();

            Subject subject = Subject.getSubjectByShortName(session, subjectShortName);

            c.setSubject(subject);

            session.persist(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Teste", "Sucesso", "Teste adicionado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(Command.getErrorEmbed("Erro", "Parâmetros em falta", e.getMessage())).setEphemeral(true).queue();

        } catch (SubjectNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed(
                            "Erro",
                            "Disciplina não encontrada",
                            e.getMessage()

                    )
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
            if (session != null)
                session.close();
        }
    }

    static void processTestEdit(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");
            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping dateMapping = event.getOption("data");
            OptionMapping startTimeMapping = event.getOption("hora");
            OptionMapping endTimeMapping = event.getOption("hora_fim");
            OptionMapping linkMapping = event.getOption("link");
            OptionMapping subjectMapping = event.getOption("disciplina");

            if (idMapping == null)
                throw new NullPointerException();

            int id = (int) idMapping.getAsLong();

            Test c = Test.getTestById(session, id);

            if (nameMapping != null)
                c.setName(nameMapping.getAsString());

            if (dateMapping != null)
                c.setDate(Date.valueOf(dateMapping.getAsString()));

            if (startTimeMapping != null)
                c.setTime(Time.valueOf(startTimeMapping.getAsString()+":00"));

            if (endTimeMapping != null)
                c.setEndTime(Time.valueOf(endTimeMapping.getAsString()+":00"));

            if (linkMapping != null)
                c.setLink(linkMapping.getAsString());

            if (subjectMapping != null) {
                String subjectShortName = subjectMapping.getAsString();
                Subject s = Subject.getSubjectByShortName(session, subjectShortName);

                c.setSubject(s);
            }

            session.update(c);
            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Teste", "Sucesso", "Teste editado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (SubjectNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed(
                            "Erro",
                            "Disciplina não encontrada",
                            e.getMessage()

                    )
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

    static void processTestRemove(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Test c = Test.getTestById(session, id);

            session.remove(c);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Teste", "Sucesso", "Teste removido com sucesso.")
            ).setEphemeral(true).queue();

        } catch (TestNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(
                    Command.getErrorEmbed(
                            "Erro",
                            "Teste não encontrado",
                            e.getMessage()
                    )
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
