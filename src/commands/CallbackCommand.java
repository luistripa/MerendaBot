package commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import university.Merenda;

public interface CallbackCommand extends Command {

    void messageCallback(Merenda merenda, Message message, MessageReceivedEvent event);
}
