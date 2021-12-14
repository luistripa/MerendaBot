package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.polls.MultiChoicePoll;
import com.merendabot.polls.exceptions.OptionsNotFoundException;
import com.merendabot.polls.exceptions.PollClosedException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import com.merendabot.polls.exceptions.PollDoesNotExistException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MultiChoicePollCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "MultiChoicePoll";

    public MultiChoicePollCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        if (!event.isFromGuild()) {
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            ).queue();
            return;
        }

        List<OptionMapping> options = event.getOptions();

        String pollDescription = options.get(0).getAsString();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0, 220, 240));
        eb.setTitle("Votação - " + pollDescription);

        eb.addField("Iniciada por:", String.format("<@%s>", event.getUser().getId()), true);
        eb.addField("Status:", "Aberta :pencil:", true);

        SelectionMenu.Builder selectionMenu = SelectionMenu.create("command multipoll vote");
        selectionMenu.setRequiredRange(1, 1);
        selectionMenu.setPlaceholder("Escolhe uma opção.");

        Set<String> optionsSet = new HashSet<>();
        for (OptionMapping option : options.subList(1, options.size())) {
            optionsSet.add(option.getAsString());
        }

        for (String option : optionsSet) {
            selectionMenu.addOption(option, option.toLowerCase().replace(" ", "-"));
        }
        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(
                selectionMenu.build()
        ).queue(message -> {
            MultiChoicePoll poll = new MultiChoicePoll(message, event.getUser(), pollDescription, optionsSet);
            guild.getPollHandler().addPoll(poll);
        });
        event.reply("Votação criada com sucesso.").setEphemeral(true).queue();
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        event.replyEmbeds(
                getErrorEmbed("Assignments", "Ação não encontrada", "Um botão foi pressionado, mas não realizou nenhuma ação.")
        ).setEphemeral(true).queue();
    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        List<String> values = event.getValues();

        try {
            MultiChoicePoll poll = (MultiChoicePoll) guild.getPollHandler().getPoll(event.getMessageId());

            poll.vote(event.getUser(), values);

            event.replyEmbeds(
                    getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Voto registado", "Obrigado! O teu voto foi registado!")
            ).setEphemeral(true).queue();

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
                        event.replyEmbeds(
                                getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
                        ).setEphemeral(true).queue();
                    }
                }
            });

        } catch (ClassCastException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação inválida", "Essa votação não é uma votação de escolha múltipla.")
            ).setEphemeral(true).queue();

        } catch (PollDoesNotExistException | PollClosedException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
            ).setEphemeral(true).queue();

        } catch (OptionsNotFoundException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Opções inválidas", "As opções selecionadas não são válidas para esta votação.")
            ).setEphemeral(true).queue();

        } catch (MemberAlreadyVotedException e) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Voto já registado", "Desculpa, mas já participaste nesta votação. Os votos são únicos, privados e permanentes!")
            ).setEphemeral(true).queue();
        }
    }
}
