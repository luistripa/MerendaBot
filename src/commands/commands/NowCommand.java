package commands.commands;

import commands.CommandCategory;
import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.events.Event;
import university.events.EventClass;
import university.events.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NowCommand extends CommandClass {
    public NowCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas a decorrer agora");

        LocalDateTime now = LocalDateTime.now();
        String fieldTitle = "Aulas";
        String fieldValue = "Não existem aulas a decorrer.";

        try {
            ResultSet rs = EventClass.getEventsByWeekday(now.getDayOfWeek(), EventType.CLASS);
            while (rs.next()) {
                Event e = EventClass.getEventFromRS(rs);
                if (e.isNow()) {
                    fieldValue = String.format(
                            "%s (%s - %s) - [Link](%s)",
                            e.getName(),
                            e.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            e.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            e.getLink()
                    );
                    break;
                }
            }
            eb.addField(fieldTitle, fieldValue, false);
            return event.getChannel().sendMessageEmbeds(eb.build());

        } catch (SQLException e) {
            e.printStackTrace();
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Agora", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            );
        }
    }
}
