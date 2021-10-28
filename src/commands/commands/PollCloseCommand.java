package commands.commands;

import commands.RestrictedCommandClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import university.Merenda;
import university.polls.Poll;

/**
 * Command to close a poll, regardless of the number of votes.
 * To close a poll, you must reply to the poll's original message.
 */
public class PollCloseCommand extends RestrictedCommandClass {

    public PollCloseCommand(String category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {

        // User does not have permission
        if (!isAdmin(event.getAuthor()))
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Poll Close", "Erro de permissão", "Não tens permissão para executar esse comando")
            );

        // User did not reply to the original message
        Message poll_message = event.getMessage().getReferencedMessage();
        if (poll_message == null)
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Poll Close", "Mensagem sem reply", "Faz reply à mensagem da votação!")
            );

        // The poll does not exist
        Poll poll = merenda.getPollHandler().getPoll(poll_message.getId());
        if (poll == null)
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Poll Close", "Votação não encontrada", "Votação não foi encontrada. Ou já encerrou, ou ocorreu um erro.")
            );

        merenda.getPollHandler().endPoll(poll.getMessage().getId());
        return event.getChannel().sendMessageEmbeds(
                getSuccessEmbed("Poll Close", "Votação encerrada", "Votação encerrada com sucesso.")
        );
    }
}
