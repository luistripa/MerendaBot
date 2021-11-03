package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;
import com.merendabot.university.important_links.ImportantLink;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.SQLException;

public class LinksCommand extends CommandClass {
    public LinksCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
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
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Links", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }
    }
}
