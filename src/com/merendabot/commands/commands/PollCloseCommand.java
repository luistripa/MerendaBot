package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.RestrictedCommandClass;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import com.merendabot.university.Merenda;
import com.merendabot.university.polls.Poll;

/**
 * Command to close a poll, regardless of the number of votes.
 * To close a poll, you must reply to the poll's original message.
 */
public class PollCloseCommand extends RestrictedCommandClass {

    private static final String COMMAND_FRIENDLY_NAME = "Poll Close";

    public PollCloseCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {

        // User does not have permission
        if (!isAdmin(event.getAuthor()))
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro de permissão", "Não tens permissão para executar esse comando")
            );

        // User did not reply to the original message
        Message pollMessage = event.getMessage().getReferencedMessage();
        if (pollMessage == null)
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Mensagem sem reply", "Faz reply à mensagem da votação!")
            );

        // The poll does not exist
        Poll poll = merenda.getPollHandler().getPoll(pollMessage.getId());
        if (poll == null)
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Votação não foi encontrada. Ou já encerrou, ou ocorreu um erro.")
            );

        merenda.getPollHandler().endPoll(poll.getMessage().getId());
        return event.getChannel().sendMessageEmbeds(
                getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Votação encerrada", "Votação encerrada com sucesso.")
        );
    }
}
