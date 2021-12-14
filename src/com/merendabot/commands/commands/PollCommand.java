package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.polls.exceptions.PollClosedException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.polls.BinaryPoll;
import com.merendabot.polls.Poll;
import com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import com.merendabot.polls.exceptions.PollDoesNotExistException;

import java.awt.*;
import java.util.List;

public class PollCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "Votação";

    public PollCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {

        if (!event.isFromGuild()) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            ).setEphemeral(true).queue();
            return;
        }

        String description = event.getOption("descrição").getAsString();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Esta votação será encerrada quando atingir maioria.");
        eb.setTitle(
                String.format("Votação - %s", description)
        );
        eb.setColor(new Color(0, 220, 240));

        eb.addField("Iniciada por:", String.format("<@%s>", event.getUser().getId()), true);
        eb.addField("Status:", "Aberta :pencil:", true);

        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(
                Button.success("command poll vote-for", "A Favor"),
                Button.secondary("command poll vote-abstain", "Abster"),
                Button.danger("command poll vote-against", "Contra")
        ).queue(message -> {
            Poll poll = new BinaryPoll(message, event.getUser(), description);
            guild.getPollHandler().addPoll(poll);
            event.reply("Votação criada com sucesso.").setEphemeral(true).queue();
        });
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        String buttonId = event.getButton().getId().split(" ")[2];

        try {
            BinaryPoll poll = (BinaryPoll) guild.getPollHandler().getPoll(event.getMessageId());

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
                    event.replyEmbeds(
                            getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "O botão não executou a ação correta. Contacta um administrador.")
                    ).setEphemeral(true).queue();
                }
            }

            event.getGuild().loadMembers().onSuccess(members -> {
                int memberCount = 0;
                for (Member member : members) {
                    if (!member.getUser().isBot())
                        memberCount += 1;
                }

                if (poll.hasMajority(memberCount) || poll.getVoteCount() == memberCount) {
                    poll.close();
                    try {
                        guild.getPollHandler().closePoll(poll.getId());
                    } catch (PollDoesNotExistException e) {
                        e.printStackTrace();
                    }
                }
            });
            event.replyEmbeds(
                    getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Voto registado", "Obrigado! O teu voto foi registado!")
            ).setEphemeral(true).queue();

        } catch (ClassCastException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação inválida", "Essa votação não é uma votação binária.")
            ).setEphemeral(true).queue();

        } catch (PollClosedException | PollDoesNotExistException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
            ).setEphemeral(true).queue();

        } catch (MemberAlreadyVotedException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Voto já registado", "Desculpa, mas já participaste nesta votação. Os votos são únicos, privados e permanentes!")
            ).setEphemeral(true).queue();
        }


    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Uma seleção foi feita, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }
}
