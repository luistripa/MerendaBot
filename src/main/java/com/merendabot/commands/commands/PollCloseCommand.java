package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.polls.Poll;
import com.merendabot.polls.exceptions.PollDoesNotExistException;
import net.dv8tion.jda.api.interactions.commands.OptionType;


/**
 * Command to close a poll, regardless of the number of votes.
 * To close a poll, you must reply to the poll's original message.
 */
public class PollCloseCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "Poll Close";

    public PollCloseCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
        super.addOption(OptionType.STRING, "id_mensagem", "ID da mensagem original da poll", true);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {

        // User does not have permission
        if (!event.getUser().getId().equals(ADMIN_ID)) {
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro de permissão", "Não tens permissão para executar esse comando")
            ).queue();
            return;
        }

        try {
            // User did not reply to the original message
            Message pollMessage = event.getChannel().retrieveMessageById(event.getOption("id_mensagem").getAsString()).complete();
            if (pollMessage == null) {
                event.replyEmbeds(
                        getErrorEmbed(COMMAND_FRIENDLY_NAME, "Mensagem sem reply", "Faz reply à mensagem da votação!")
                ).setEphemeral(true).queue();
                return;
            }

            guild.getPollHandler().closePoll(pollMessage);

            event.replyEmbeds(
                    getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Votação encerrada", "Votação encerrada com sucesso.")
            ).setEphemeral(true).queue();

        } catch (PollDoesNotExistException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Votação não foi encontrada. Ou já encerrou, ou ocorreu um erro.")
            ).setEphemeral(true).queue();

        } catch (IllegalArgumentException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "ID inválido", "O ID introduzido não é válido.")
            ).setEphemeral(true).queue();
        }
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Um botão foi pressionado, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Uma seleção foi feita, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }
}
