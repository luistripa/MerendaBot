package commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;

public abstract class CallbackCommandClass extends CommandClass implements CallbackCommand {

    public CallbackCommandClass(String category, String name, String description) {
        super(category, name, description);
    }

    public abstract MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event);

    public abstract void messageCallback(Merenda merenda, Message message, MessageReceivedEvent event);
}
