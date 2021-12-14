package com.merendabot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.GuildManager;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Command extends CommandData {

    public static final String COMMAND_PREFIX = "merenda!";
    public static final String ADMIN_ID = "647790893646086167";

    private CommandCategory category;
    private String name;
    private String description;
    private List<CommandParam> params;

    public Command(CommandCategory category, String name, String description) {
        super(name, description);
        this.category = category;
        this.name = name;
        this.description = description;
        this.params = new ArrayList<>();
    }

    public CommandCategory getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<CommandParam> getParams() {
        return params;
    }

    public MessageEmbed getHelp() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription(this.getDescription());
        eb.setColor(Color.WHITE);
        StringBuilder embedTitle = new StringBuilder();
        embedTitle.append(COMMAND_PREFIX).append(this.getName());

        StringBuilder fieldValue = new StringBuilder();
        for (CommandParam param : this.getParams()) {
            String fieldTitle = String.format("%s - %s", param.getName(), param.getDescription());
            embedTitle.append(String.format(" <%s>", param.getName()));

            for (String possibleValue : param.getPossibleValues()) {
                fieldValue.append(possibleValue).append("\n");
            }
            eb.addField(fieldTitle, fieldValue.toString(), false);
        }
        eb.setTitle(embedTitle.toString());
        return eb.build();
    }

    public Command addParam(CommandParam param) {
        params.add(param);
        return this;
    }

    public abstract void execute(GuildManager guild, SlashCommandEvent event);

    public abstract void processButtonClick(GuildManager guild, ButtonClickEvent event);

    public abstract void processSelectionMenu(GuildManager guild, SelectionMenuEvent event);

    /**
     * Checks if the command has at least the number of provided params.
     *
     * @param command The command space-split
     * @param params The number of required params
     * @return True if verifies the condition, False otherwise.
     */
    protected boolean verifyCommandParams(List<String> command, int params) {
        return command.size() >= params+1;
    }

    /**
     * Generates a message embed as an error.
     *
     * @param title The title of the embed
     * @param fieldTitle The field title of the embed. Usually describes the error generically. Ex.: SQL Error
     * @param fieldValue The field description of the embed. Describes the error or tells the user how to proceed.
     * @return A MessageEmbed object
     */
    protected MessageEmbed getErrorEmbed(String title, String fieldTitle, String fieldValue) {
        return new EmbedBuilder()
                .setColor(new Color(255, 50, 50))
                .setTitle(title)
                .addField(fieldTitle, fieldValue, false)
                .build();
    }

    /**
     * Generates a message embed as a success message.
     *
     * @param title The title of the embed
     * @param fieldTitle The field title of the embed
     * @param fieldValue The field description of the embed
     * @return A MessageEmbed object
     */
    protected MessageEmbed getSuccessEmbed(String title, String fieldTitle, String fieldValue) {
        return new EmbedBuilder()
                .setColor(new Color(150, 255, 70))
                .setTitle(title)
                .addField(fieldTitle, fieldValue, false)
                .build();
    }
}
