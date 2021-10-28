package commands.commands;

import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.important_links.ImportantLink;
import university.subjects.Subject;
import university.subjects.SubjectClass;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LinksCommand extends CommandClass {
    public LinksCommand(String category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Links Importantes");
        eb.setColor(Color.WHITE);

        String fieldTitle = "Links";
        StringBuilder fieldValue = new StringBuilder();

        try {
            ResultSet rs = ImportantLink.getLinks(merenda.getConnection());
            while (rs.next()) {
                ImportantLink link = ImportantLink.getLinkFromRS(rs);
                if (link.getSubjectId() == 0) {
                    fieldValue.append(String.format("**%s** - [Link](%s)\n", link.getName(), link.getLink()));
                } else {
                    Subject subject = Subject.getSubjectFromRS(rs, 5);
                    fieldValue.append(
                            String.format(
                                    "**%s %s** - [Link](%s)\n",
                                    link.getName(),
                                    subject.getShortName(),
                                    link.getLink()
                            )
                    );
                }
            }
            eb.addField(fieldTitle, fieldValue.toString(), false);
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Links", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            );
        }
    }
}
