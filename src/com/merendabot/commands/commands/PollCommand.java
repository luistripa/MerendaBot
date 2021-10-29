package com.merendabot.commands.commands;

import com.merendabot.commands.CallbackCommand;
import com.merendabot.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import com.merendabot.university.Merenda;
import com.merendabot.university.polls.Poll;
import com.merendabot.university.polls.PollClass;

import java.awt.*;
import java.util.Arrays;

public class PollCommand extends CallbackCommand {

    private static final String COMMAND_FRIENDLY_NAME = "Votação";

    public PollCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {

        if (!event.isFromGuild()) {
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            );
        }

        String[] pollDescription = Arrays.copyOfRange(command, 1, command.length);
        String description = String.join(" ", pollDescription);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Esta votação será encerrada quando atingir maioria.");
        eb.setTitle(
                String.format("Votação - %s", description)
        );
        eb.setColor(new Color(0, 220, 240));

        eb.addField("Iniciada por:", String.format("<@%s>", event.getAuthor().getId()), true);
        eb.addField("Status:", "Aberta :pencil:", true);


        return event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(
                Button.success("command poll vote-for", "A Favor"),
                Button.secondary("command poll vote-abstain", "Abster"),
                Button.danger("command poll vote-against", "Contra")
        );
    }

    @Override
    public void messageCallback(Merenda merenda, Message message, MessageReceivedEvent event) {
        Poll poll = new PollClass(message, event.getAuthor(), event.getMessageId());
        merenda.getPollHandler().addPoll(poll);
        event.getMessage().delete().queue();
    }

    @Override
    public ReplyAction processButtonPressed(Merenda merenda, ButtonClickEvent event) {
        String buttonId = event.getButton().getId().split(" ")[2];
        Message message = event.getMessage();

        Poll poll = merenda.getPollHandler().getPoll(message.getId());
        if (poll == null)
            return event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
            );

        if (poll.hasVoteFrom(event.getUser()))
            return event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Voto já registado", "Desculpa, mas já participaste nesta votação. Os votos são únicos, privados e permanentes!")
            );

        switch (buttonId) {
            case "vote-for":
                poll.voteFor(event.getUser());
                break;
            case "vote-abstain":
                poll.voteAbstain(event.getUser());
                break;
            case "vote-against":
                poll.voteAgainst(event.getUser());
                break;
            default: {
                return event.replyEmbeds(
                        getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "O botão não executou a ação correta. Contacta um administrador.")
                );
            }
        }


        event.getGuild().loadMembers().onSuccess(members -> {
            int memberCount = 0;
            for (Member member : members) {
                if (!member.getUser().isBot())
                    memberCount += 1;
            }

            if (poll.hasMajority(memberCount) || poll.getVoteCount() == memberCount) {
                poll.closePoll();
                merenda.getPollHandler().endPoll(poll.getMessage().getId());
            }
        });
        return event.replyEmbeds(
                getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Voto registado", "Obrigado! O teu voto foi registado!")
        );
    }
}
