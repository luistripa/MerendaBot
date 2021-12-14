package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.university.important_links.ImportantLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LinksCommand extends Command {
    public LinksCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Links Importantes");
        eb.setColor(Color.WHITE);

        String fieldTitle = "Links";
        StringBuilder fieldValue = new StringBuilder();

        try {
            for (ImportantLink link : ImportantLink.getLinks()) {
                if (link.getSubjectId() == 0) {
                    fieldValue.append(String.format("**%s** - [Link](%s)%n", link.getName(), link.getLink()));
                } else {
                    Subject subject = Subject.getSubjectById(link.getSubjectId());
                    fieldValue.append(
                            String.format(
                                    "**%s %s** - [Link](%s)%n",
                                    link.getName(),
                                    subject.getShortName(),
                                    link.getLink()
                            )
                    );
                }
            }

            eb.addField(fieldTitle, fieldValue.toString(), false);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } catch (SQLException e) {
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
