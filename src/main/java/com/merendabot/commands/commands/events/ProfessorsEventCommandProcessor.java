package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.commands.exceptions.MissingParameterException;
import com.merendabot.university.subjects.Professor;
import com.merendabot.university.subjects.Subject;
import com.merendabot.university.subjects.exceptions.ProfessorNotFoundException;
import com.merendabot.university.subjects.exceptions.SubjectNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.awt.*;
import java.util.List;

public class ProfessorsEventCommandProcessor {

    static void processProfessor(GuildManager guild, SlashCommandEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.reply("Error. Subcommand Name is invalid").setEphemeral(true).queue();
            return;
        }

        switch (subcommandName) {
            case "listar" -> processProfessorList(guild, event);
            case "novo" -> processProfessorAdd(guild, event);
            case "editar" -> processProfessorEdit(guild, event);
            case "apagar" -> processProfessorRemove(guild, event);
        }
    }

    private static void processProfessorList(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de professores");

            List<Professor> professorList = Professor.getProfessors(session);

            for (Professor professor : professorList) {
                eb.addField(
                        professor.getName() + " (" + professor.getSubject().getShortName() + ")",
                        "ID: " + professor.getId(),
                        false
                );
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();
        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
        }
    }

    private static void processProfessorAdd(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping subjectMapping = event.getOption("disciplina");
            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping emailMapping = event.getOption("email");

            if (subjectMapping == null || nameMapping == null || emailMapping == null)
                throw new MissingParameterException();

            Subject subject = Subject.getSubjectByShortName(session, subjectMapping.getAsString());

            Professor professor = new Professor(guild, nameMapping.getAsString(), emailMapping.getAsString(), subject);

            session.persist(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Professor", "Sucesso", "Professor adicionado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | SubjectNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();

        }
    }

    private static void processProfessorEdit(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");
            OptionMapping nameMapping = event.getOption("nome");
            OptionMapping emailMapping = event.getOption("email");
            OptionMapping subjectMapping = event.getOption("disciplina");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Professor professor = Professor.getProfessorById(session, id);

            if (nameMapping != null) {
                professor.setName(nameMapping.getAsString());
            }

            if (emailMapping != null) {
                professor.setEmail(emailMapping.getAsString());
            }

            if (subjectMapping != null) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<Subject> cr = cb.createQuery(Subject.class);
                Root<Subject> root = cr.from(Subject.class);
                cr.select(root).where(cb.equal(root.get("shortName"), subjectMapping.getAsString()));

                Subject subject = session.createQuery(cr).getSingleResult();
                professor.setSubject(subject);
            }

            session.update(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Professor", "Sucesso", "Professor editado com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | ProfessorNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            throwable.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", throwable.getMessage())
            ).setEphemeral(true).queue();

        }
    }

    private static void processProfessorRemove(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null)
                throw new MissingParameterException();

            int id = (int) idMapping.getAsLong();

            Professor professor = Professor.getProfessorById(session, id);

            session.remove(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Professor", "Sucesso", "Professor removido com sucesso.")
            ).setEphemeral(true).queue();

        } catch (MissingParameterException | ProfessorNotFoundException e) {
            if (tx != null)
                tx.rollback();
            event.replyEmbeds(e.getEmbed()).setEphemeral(true).queue();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            throwable.printStackTrace();
            event.replyEmbeds(
                    Command.getErrorEmbed("Erro", "Contacta um administrador", throwable.getMessage())
            ).setEphemeral(true).queue();

        }
    }
}
