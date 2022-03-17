package com.merendabot.commands.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.Command;
import com.merendabot.university.important_links.ImportantLink;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;

public class LinksCommand extends Command {

    public LinksCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        Transaction tx = null;

        try (Session session = Merenda.getInstance().getFactory().openSession()) {
            tx = session.beginTransaction();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Links Importantes");
            eb.setColor(Color.WHITE);

            String fieldTitle = "Links";
            StringBuilder fieldValue = new StringBuilder();

            for (ImportantLink link : ImportantLink.getLinks(session)) {
                if (link.getSubject() == null) {
                    fieldValue.append(String.format("**%s** - [Link](%s)%n", link.getName(), link.getUrl()));
                } else {
                    Subject subject = link.getSubject();
                    fieldValue.append(
                            String.format(
                                    "**%s %s** - [Link](%s)%n",
                                    link.getName(),
                                    subject.getShortName(),
                                    link.getUrl()
                            )
                    );
                }
            }

            if (fieldValue.length() == 0) {
                eb.setDescription("Não existem links para mostrar.");

            } else {
                eb.addField(fieldTitle, fieldValue.toString(), false);
            }

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Links", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).setEphemeral(true).queue();
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
