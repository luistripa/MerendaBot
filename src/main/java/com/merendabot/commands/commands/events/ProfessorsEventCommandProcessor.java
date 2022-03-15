package main.java.com.merendabot.commands.commands.events;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.Merenda;
import main.java.com.merendabot.commands.Command;
import main.java.com.merendabot.university.subjects.Professor;
import main.java.com.merendabot.university.subjects.ProfessorClass;
import main.java.com.merendabot.university.subjects.SubjectClass;
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
            event.reply("Error. Subcommand Name is invalid").queue();
            return;
        }

        switch (subcommandName) {
            case "listar" -> processProfessorList(guild, event);
            case "novo" -> processProfessorAdd(guild, event);
            case "editar" -> processProfessorEdit(guild, event);
            case "apagar" -> processProfessorRemove(guild, event);
        }
    }

    private void base() {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            tx.commit();
        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
        }
    }

    private static void processProfessorList(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de professores");

            List professorList = session.createQuery("from ProfessorClass ").list();

            for (Object o : professorList) {
                Professor professor = (Professor) o;
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

            if (subjectMapping == null || nameMapping == null || emailMapping == null) {
                event.reply("Falta um parâmetro!").setEphemeral(true).queue();
                return;
            }

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<SubjectClass> cr = cb.createQuery(SubjectClass.class);
            Root<SubjectClass> root = cr.from(SubjectClass.class);
            cr.select(root).where(cb.equal(root.get("shortName"), subjectMapping.getAsString()));

            SubjectClass subject = session.createQuery(cr).getSingleResult();

            if (subject == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Disciplina não encontrada",
                                String.format("Disciplina com nome '%s' não foi encontrada.", subjectMapping.getAsString())

                        )
                ).setEphemeral(true).queue();
                return;
            }

            Professor professor = new ProfessorClass(guild, nameMapping.getAsString(), emailMapping.getAsString(), subject);

            session.persist(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Professor", "Sucesso", "Professor adicionado com sucesso.")
            ).setEphemeral(true).queue();

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

            if (idMapping == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Parâmetro necessário",
                                "O parâmetro id é necessário para editar um professor"
                        )
                ).setEphemeral(true).queue();
                return;
            }

            int id = (int) idMapping.getAsLong();

            ProfessorClass professor = session.get(ProfessorClass.class, id);

            if (professor == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Professor não encontrado",
                                String.format("Professor com id '%d' não foi encontrado.", id)

                        )
                ).setEphemeral(true).queue();
                return;
            }

            if (nameMapping != null) {
                professor.setName(nameMapping.getAsString());
            }

            if (emailMapping != null) {
                professor.setEmail(emailMapping.getAsString());
            }

            if (subjectMapping != null) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<SubjectClass> cr = cb.createQuery(SubjectClass.class);
                Root<SubjectClass> root = cr.from(SubjectClass.class);
                cr.select(root).where(cb.equal(root.get("shortName"), subjectMapping.getAsString()));

                SubjectClass subject = session.createQuery(cr).getSingleResult();
                professor.setSubject(subject);
            }

            session.update(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Professor", "Sucesso", "Professor editado com sucesso.")
            ).setEphemeral(true).queue();

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

            if (idMapping == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Parâmetro necessário",
                                "O parâmetro id é necessário para remover um professor"
                        )
                ).setEphemeral(true).queue();
                return;
            }

            int id = (int) idMapping.getAsLong();

            ProfessorClass professor = session.get(ProfessorClass.class, id);

            if (professor == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Professor não encontrado",
                                String.format("Professor com id '%d' não foi encontrado.", id)

                        )
                ).setEphemeral(true).queue();
                return;
            }

            session.remove(professor);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Professor", "Sucesso", "Professor removido com sucesso.")
            ).setEphemeral(true).queue();

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
