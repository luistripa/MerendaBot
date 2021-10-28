package commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import university.Merenda;

public abstract class CallbackCommand extends CommandClass {

    protected CallbackCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    public abstract void messageCallback(Merenda merenda, Message message, MessageReceivedEvent event);
}
