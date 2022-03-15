package main.java.com.merendabot.commands.commands.events;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.Merenda;
import main.java.com.merendabot.commands.Command;
import main.java.com.merendabot.university.subjects.Subject;
import main.java.com.merendabot.university.subjects.SubjectClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.Color;
import java.util.List;

public class SubjectsEventCommandProcessor {

    static void processSubject(GuildManager guild, SlashCommandEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.reply("Error. Subcommand Name is invalid").queue();
            return;
        }

        switch (subcommandName) {
            case "listar" -> processSubjectList(guild, event);
            case "novo" -> processSubjectAdd(guild, event);
            case "editar" -> processSubjectEdit(guild, event);
            case "apagar" -> processSubjectRemove(guild, event);
        }
    }

    private static void processSubjectList(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Lista de disciplinas");

            List subjectList = session.createQuery("from SubjectClass").list();

            for (Object o : subjectList) {
                Subject subject = (Subject) o;
                eb.addField(
                        subject.getShortName() + " (" + subject.getFullName() + ")",
                        "ID: " + subject.getId(),
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

    private static void processSubjectAdd(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping shortNameMapping = event.getOption("short_name");
            OptionMapping fullNameMapping = event.getOption("full_name");

            if (shortNameMapping == null || fullNameMapping == null) {
                event.reply("Falta um parâmetro!").setEphemeral(true).queue();
                return;
            }

            Subject subject = new SubjectClass(guild, fullNameMapping.getAsString(), shortNameMapping.getAsString());
            session.persist(subject);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Adicionar Disciplina", "Sucesso", "Disciplina adicionada com sucesso.")
            ).setEphemeral(true).queue();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
        }
    }

    private static void processSubjectEdit(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Parâmetro necessário",
                                "O parâmetro id é necessário para editar uma disciplina"
                        )
                ).setEphemeral(true).queue();
                return;
            }

            int id = (int) idMapping.getAsLong();

            SubjectClass subject = session.get(SubjectClass.class, id);

            if (subject == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Disciplina não encontrada",
                                String.format("Disciplina com id '%d' não foi encontrada.", id)

                        )
                ).setEphemeral(true).queue();
                return;
            }

            OptionMapping fullNameMapping = event.getOption("full_name");
            OptionMapping shortNameMapping = event.getOption("short_name");

            if (fullNameMapping != null) {
                subject.setFullName(fullNameMapping.getAsString());
            }

            if (shortNameMapping != null) {
                subject.setShortName(shortNameMapping.getAsString());
            }

            session.update(subject);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Editar Disciplina", "Sucesso", "Disciplina editada com sucesso.")
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

    private static void processSubjectRemove(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            OptionMapping idMapping = event.getOption("id");

            if (idMapping == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Parâmetro necessário",
                                "O parâmetro id é necessário para remover uma disciplina"
                        )
                ).setEphemeral(true).queue();
                return;
            }

            int id = (int) idMapping.getAsLong();

            SubjectClass subject = session.get(SubjectClass.class, id);

            if (subject == null) {
                event.replyEmbeds(
                        Command.getErrorEmbed(
                                "Erro",
                                "Disciplina não encontrada",
                                String.format("Disciplina com id '%d' não foi encontrada.", id)

                        )
                ).setEphemeral(true).queue();
                return;
            }

            session.remove(subject);

            tx.commit();

            event.replyEmbeds(
                    Command.getSuccessEmbed("Remover Disciplina", "Sucesso", "Disciplina removida com sucesso.")
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
