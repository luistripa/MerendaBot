package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import university.Merenda;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandClass implements Command {

    public static final String COMMAND_PREFIX = "merenda!";
    public static final String ADMIN_ID = "647790893646086167";

    private final CommandCategory category;
    private final String name;
    private final String description;
    private final List<CommandParam> params;

    protected CommandClass(CommandCategory category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.params = new ArrayList<>();
    }

    @Override
    public CommandCategory getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public List<CommandParam> getParams() {
        return this.params;
    }

    @Override
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

    @Override
    public Command addParam(CommandParam param) {
        this.params.add(param);
        return this;
    }

    @Override
    public ReplyAction processButtonPressed(Merenda merenda, ButtonClickEvent event) {
        return event.replyEmbeds(
                getSuccessEmbed("Botão", "Botão premido", "Um botão foi premido, mas não realizou nenhuma ação.")
        );
    }

    @Override
    public void processSelectionMenu(Merenda merenda, SelectionMenuEvent event) {
        event.reply("Seleção").setEphemeral(true).queue();
    }

    /**
     * Checks if the command has at least the number of provided params.
     *
     * @param command The command space-split
     * @param params The number of required params
     * @return True if verifies the condition, False otherwise.
     */
    protected boolean verifyCommandParams(String[] command, int params) {
        return command.length >= params+1;
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
