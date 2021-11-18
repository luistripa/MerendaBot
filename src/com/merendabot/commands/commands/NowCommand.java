package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.events.Class;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NowCommand extends CommandClass {
    public NowCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas a decorrer agora");

        LocalDateTime now = LocalDateTime.now();
        String fieldTitle = "Aulas";
        String fieldValue = "Não existem aulas a decorrer.";

        try {
            for (Class c : Class.getClassesByWeekDay(now.getDayOfWeek())) {
                if (c.isNow()) {
                    fieldValue = String.format(
                            "%s %s (%s - %s) - [Link](%s)",
                            c.getName(),
                            c.getSubject().getShortName(),
                            c.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                            c.getLink()
                    );
                    break;
                }
            }
            eb.addField(fieldTitle, fieldValue, false);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Agora", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
            ).queue();
        }
    }
}
