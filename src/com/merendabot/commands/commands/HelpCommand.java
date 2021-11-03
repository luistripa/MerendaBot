package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.MessageDispatcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;

import java.awt.*;


public class HelpCommand extends CommandClass {
    public HelpCommand(CommandCategory category, String name, String help) {
        super(category, name, help);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        if (this.verifyCommandParams(command, 1))
            showHelpForCommand(merenda, command[1], event);
        else
            showHelpMenu(merenda, event);
    }

    /**
     * Shows the help menu for a specific program.
     *
     * @param merenda The system object
     * @param command The command to show help from
     */
    private void showHelpForCommand(Merenda merenda, String command, MessageReceivedEvent event) {
        if (merenda.hasCommand(command)) {
            Command c = merenda.getCommand(command);
            event.getChannel().sendMessageEmbeds(c.getHelp()).queue();
        } else {
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(String.format("Help - %s", command), "Comando não encontrado", "Ajuda para esse comando não existe.")
            ).queue();
        }

    }

    private void showHelpMenu(Merenda merenda, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Comandos");

        for (String category : merenda.getCommandCategories()) {
            StringBuilder fieldValue = new StringBuilder();
            for (Command command : merenda.getCommandsByCategory(category)) {
                fieldValue.append(
                        String.format("%s%s - %s%n", COMMAND_PREFIX, command.getName(), command.getDescription())
                );
            }
            eb.addField(category, fieldValue.toString(), false);
        }
        eb.setFooter(String.format("%shelp <comando> para mais informações de um comando.", COMMAND_PREFIX));
        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }
}
