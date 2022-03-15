package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;

import java.util.List;


/**
 * This class is reserved for functionality tests. It is used to test functions to later be applied in a production command.
 */
public class TestCommand extends Command {

    public TestCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(GuildManager merenda, SlashCommandEvent event) {
        SelectionMenu selectionMenu = SelectionMenu.create("command test pizza")
                .setPlaceholder("Escolhe uma pizza")
                .setRequiredRange(1, 1)
                .addOption("Pizza Pepperonni", "pizza-pepperonni")
                .addOption("Pizza Hawaii", "pizza-hawaii")
                .addOption("Pizza Carbonara", "pizza-carbonara")
                .build();
        event.reply("Pizza").addActionRow(selectionMenu).setEphemeral(true).queue();
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Um botão foi pressionado, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        String[] id = event.getSelectionMenu().getId().split(" ");

        switch (id[2]) {
            case "pizza":
                processChooseCondiments(guild, event);
                break;
            case "pizza_condiments":
                showResults(guild, event);
                break;
            default:
                event.reply("Opção não encontrada.").setEphemeral(true).queue();
                break;
        }
    }

    private void processChooseCondiments(GuildManager merenda, SelectionMenuEvent event) {
        String pizza = event.getSelectedOptions().get(0).getValue();
        SelectionMenu selectionMenu = SelectionMenu.create(String.format("command test pizza_condiments %s", pizza))
                .setPlaceholder("Condimentos")
                .setRequiredRange(1, 3)
                .addOption("Extra mezzarella", "extra-cheese")
                .addOption("Azeitonas", "olives")
                .addOption("Extra Pepperonni", "extra-pepperonni")
                .build();
        event.reply("Condimentos").addActionRow(selectionMenu).setEphemeral(true).queue();
    }

    private void showResults(GuildManager guild, SelectionMenuEvent event) {
        String pizza = event.getSelectionMenu().getId().split(" ")[3];

        System.out.println(pizza);
        for (String condiment : event.getValues()) {
            System.out.printf("\t- %s%n", condiment);
        }

        event.reply("Pizza.").setEphemeral(true).queue();
    }
}
