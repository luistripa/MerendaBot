package com.merendabot.commands;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import com.merendabot.university.Merenda;

import java.util.*;

public interface Command {

    CommandCategory getCategory();

    String getName();

    String getDescription();

    List<CommandParam> getParams();

    /**
     * Generate a help message embed specifying all the parameters.
     *
     * @return An MessageEmbed object ready to send.
     */
    MessageEmbed getHelp();

    /**
     * Adds a parameter to the command.
     * Parameters will show on command help messages.
     *
     * @param param A CommandParam object
     * @return A Command Object.
     */
    Command addParam(CommandParam param);

    /**
     * Executes the command itself.
     *  @param merenda The system object
     * @param command An array containing the command and its parameters
     * @param event The event that triggered the method
     * @return
     */
    MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event);

    /**
     * Processes a button click for this command.
     *  @param merenda The system object
     * @param event The event that triggered the method
     * @return
     */
    ReplyAction processButtonPressed(Merenda merenda, ButtonClickEvent event);

    /**
     * Processes a selection menu event.
     *
     * @param merenda The system object
     * @param event The event that triggered the method
     */
    void processSelectionMenu(Merenda merenda, SelectionMenuEvent event);
}
