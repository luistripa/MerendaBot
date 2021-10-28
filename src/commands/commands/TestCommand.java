package commands.commands;

import commands.CommandCategory;
import commands.CommandClass;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;


/**
 * This class is reserved for functionality tests. It is used to test functions to later be applied in a production command.
 */
public class TestCommand extends CommandClass {
    public TestCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        SelectionMenu selectionMenu = SelectionMenu.create("command test pizza")
                .setPlaceholder("Escolhe uma pizza")
                .setRequiredRange(1, 1)
                .addOption("Pizza Pepperonni", "pizza-pepperonni")
                .addOption("Pizza Hawaii", "pizza-hawaii")
                .addOption("Pizza Carbonara", "pizza-carbonara")
                .build();
        return event.getChannel().sendMessage("Pizza").setActionRow(selectionMenu);
    }

    @Override
    public void processSelectionMenu(Merenda merenda, SelectionMenuEvent event) {
        String[] id = event.getSelectionMenu().getId().split(" ");

        switch (id[2]) {
            case "pizza":
                processChooseCondiments(merenda, event);
                break;
            case "pizza_condiments":
                showResults(merenda, event);
                break;
            default:
                event.reply("Opção não encontrada.").queue();
                break;
        }
    }

    private void processChooseCondiments(Merenda merenda, SelectionMenuEvent event) {
        String pizza = event.getSelectedOptions().get(0).getValue();
        SelectionMenu selectionMenu = SelectionMenu.create(String.format("command test pizza_condiments %s", pizza))
                .setPlaceholder("Condimentos")
                .setRequiredRange(1, 3)
                .addOption("Extra mezzarella", "extra-cheese")
                .addOption("Azeitonas", "olives")
                .addOption("Extra Pepperonni", "extra-pepperonni")
                .build();
        event.reply("Condimentos").addActionRow(selectionMenu).queue();
    }

    private void showResults(Merenda merenda, SelectionMenuEvent event) {
        String pizza = event.getSelectionMenu().getId().split(" ")[3];

        System.out.println(pizza);
        for (String condiment : event.getValues()) {
            System.out.printf("\t- %s%n", condiment);
        }

        event.reply("Pizza.").queue();
    }
}
