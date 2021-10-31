package com.merendabot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import com.merendabot.university.Merenda;

/**
 * Represents a command with a callback.
 *
 * Callback commands are special commands that have a messageCallback method that is called once a response is sent to the TextChannel.
 */
public abstract class CallbackCommand extends CommandClass {

    protected CallbackCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    /**
     * Calls back the command with the message that was just sent.
     *
     * @param message The message that was sent
     * @param event The event that originally called the command.
     */
    public abstract void messageCallback(Message message, MessageReceivedEvent event);
}
