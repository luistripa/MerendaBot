package commands.commands;

import commands.CallbackCommandClass;
import commands.CommandClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import university.Merenda;
import university.polls.Poll;
import university.polls.PollClass;

import java.awt.*;
import java.util.Arrays;

public class PollCommand extends CallbackCommandClass {

    public PollCommand(String category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public MessageAction execute(Merenda merenda, String[] command, MessageReceivedEvent event) {

        if (!event.isFromGuild()) {
            return event.getChannel().sendMessageEmbeds(
                    getErrorEmbed("Votação", "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            );
        }

        String[] poll_description = Arrays.copyOfRange(command, 1, command.length);
        String description = String.join(" ", poll_description);

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
                    getErrorEmbed("Votação", "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
            );

        if (poll.hasVoteFrom(event.getUser()))
            return event.replyEmbeds(
                    getErrorEmbed("Votação", "Voto já registado", "Desculpa, mas já participaste nesta votação. Os votos são únicos, privados e permanentes!")
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
        }


        event.getGuild().loadMembers().onSuccess(members -> {
            int member_count = 0;
            for (Member member : members) {
                if (!member.getUser().isBot())
                    member_count += 1;
            }

            if (poll.hasMajority(member_count)) {
                poll.closePoll();
                merenda.getPollHandler().endPoll(poll.getMessage().getId());

            } else if (poll.getVoteCount() == member_count) {
                poll.closePoll();
                merenda.getPollHandler().endPoll(poll.getMessage().getId());
            }
        });
        return event.replyEmbeds(
                getSuccessEmbed("Votação", "Voto registado", "Obrigado! O teu voto foi registado!")
        );
    }
}
