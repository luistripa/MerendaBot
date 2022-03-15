package com.merendabot.commands.commands;

import com.merendabot.commands.Command;
import com.merendabot.polls.MultiChoicePoll;
import com.merendabot.polls.exceptions.OptionsNotFoundException;
import com.merendabot.polls.exceptions.PollClosedException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.GuildManager;
import com.merendabot.commands.CommandCategory;
import com.merendabot.polls.BinaryPoll;
import com.merendabot.polls.Poll;
import com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import com.merendabot.polls.exceptions.PollDoesNotExistException;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class PollCommand extends Command {

    private static final String COMMAND_FRIENDLY_NAME = "Votação";

    public PollCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
        super.addSubcommands(
                new SubcommandData("binary", "Votação binária (Favor/Abster/Contra)")
                        .addOption(OptionType.STRING, "descrição", "Descrição da votação.", true),

                new SubcommandData("multi", "Votação de escolha múltipla")
                        .addOption(OptionType.STRING, "descrição", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_1", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_2", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_3", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_4", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_5", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_6", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_7", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_8", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_9", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_10", "Opção de votação.", false)
        );
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {

        if (!event.isFromGuild()) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            ).setEphemeral(true).queue();
            return;
        }

        switch (event.getSubcommandName()) {
            case "binary" -> executeBinaryPoll(guild, event);
            case "multi" -> executeMultiPoll(guild, event);
            default -> event.replyEmbeds(
                    getErrorEmbed(
                            "Comandos",
                            "Comando não encontrado",
                            "O comando introduzido não foi encontrado"
                    )
            ).queue();
        }
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        String buttonId = event.getButton().getId().split(" ")[2];

        try {
            BinaryPoll poll = (BinaryPoll) guild.getPollHandler().getPoll(event.getMessageId());

            switch (buttonId) {
                case "vote-for" -> poll.voteFor(event.getUser());
                case "vote-abstain" -> poll.voteAbstain(event.getUser());
                case "vote-against" -> poll.voteAgainst(event.getUser());
                default -> {
                    event.replyEmbeds(
                            getErrorEmbed(COMMAND_FRIENDLY_NAME, "Erro", "O botão não executou a ação correta. Contacta um administrador.")
                    ).setEphemeral(true).queue();
                    return;
                }
            }

            guild.getGuild().findMembers(Predicate.not(member -> member.getUser().isBot())).onSuccess(members -> {
                int memberCount = 0;
                for (Member member : members) {
                    if (!member.getUser().isBot())
                        memberCount += 1;
                }

                if (poll.hasMajority(memberCount) || poll.getVoteCount() == memberCount) {
                    try {
                        guild.getPollHandler().closePoll(event.getMessage());
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
        List<String> values = event.getValues();

        try {
            MultiChoicePoll poll = (MultiChoicePoll) guild.getPollHandler().getPoll(event.getMessageId());

            poll.vote(event.getUser(), values);

            event.replyEmbeds(
                    getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Voto registado", "Obrigado! O teu voto foi registado!")
            ).setEphemeral(true).queue();

            guild.getGuild().findMembers(Predicate.not(member -> member.getUser().isBot())).onSuccess(members -> {
                int memberCount = 0;
                for (Member member : members) {
                    if (!member.getUser().isBot())
                        memberCount += 1;
                }

                if (poll.hasMajority(memberCount) || poll.getVoteCount() == memberCount) {
                    try {
                        guild.getPollHandler().closePoll(event.getMessage());
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

    private void executeBinaryPoll(GuildManager guild, SlashCommandEvent event) {
        OptionMapping descriptionMapping = event.getOption("descrição");
        Objects.requireNonNull(descriptionMapping);

        String description = descriptionMapping.getAsString();

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

    private void executeMultiPoll(GuildManager guild, SlashCommandEvent event) {
        List<OptionMapping> options = event.getOptions();

        String pollDescription = options.get(0).getAsString();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0, 220, 240));
        eb.setTitle("Votação - " + pollDescription);

        eb.addField("Iniciada por:", String.format("<@%s>", event.getUser().getId()), true);
        eb.addField("Status:", "Aberta :pencil:", true);

        SelectionMenu.Builder selectionMenu = SelectionMenu.create("command poll vote");
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
}
