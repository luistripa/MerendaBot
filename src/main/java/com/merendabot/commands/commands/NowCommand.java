package com.merendabot.commands.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.events.Class;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NowCommand extends Command {

    public NowCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = null;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas a decorrer agora");

        LocalDateTime now = LocalDateTime.now();
        String fieldTitle = "Aulas";
        String fieldValue = "Não existem aulas a decorrer.";

        try {
            tx = session.beginTransaction();

            for (Class c : Class.getClassesByWeekday(session, now.getDayOfWeek())) {
                if (c.isNow()) {
                    fieldValue = String.format(
                            "%s %s (%s - %s) - [Link](%s)",
                            c.getName(),
                            c.getSubject().getShortName(),
                            c.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getLink()
                    );
                    break;
                }
            }
            eb.addField(fieldTitle, fieldValue, false);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Agora", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();

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
