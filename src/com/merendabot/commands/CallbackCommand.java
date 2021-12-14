package com.merendabot.commands;


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CallbackCommand extends Command {

    public CallbackCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    public abstract void messageCallback(Message message, MessageReceivedEvent event);
}
