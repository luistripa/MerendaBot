package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.university.events.Class;

import java.awt.Color;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ClassesCommand extends Command {

    public ClassesCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Aulas");
        eb.setColor(Color.WHITE);

        try {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                String fieldTitle = dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("PT"));
                StringBuilder fieldValue = new StringBuilder();

                for (Class c : Class.getClassesByWeekDay(dayOfWeek)) {
                    fieldValue.append(
                            String.format(
                                    "**%s %s** (%s - %s) - [Link](%s)%n",
                                    c.getName(),
                                    c.getSubject().getShortName(),
                                    c.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    c.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                    c.getLink()));
                }
                if (fieldValue.length() > 0) // Filter days that have no classes
                    eb.addField(fieldTitle, fieldValue.toString(), false);
            }
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

        } catch (SQLException e) {
            e.printStackTrace();
            event.replyEmbeds(
                    getErrorEmbed("Aulas", "Erro SQL", "Ocorreu um erro. Contacta o administrador.")
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
