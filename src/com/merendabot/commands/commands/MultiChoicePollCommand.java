package com.merendabot.commands.commands;

import com.merendabot.commands.CommandCategory;
import com.merendabot.commands.CommandClass;
import com.merendabot.university.Merenda;
import com.merendabot.university.polls.MultiChoicePoll;
import com.merendabot.university.polls.MultiChoicePollClass;
import com.merendabot.university.polls.Poll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;

import java.awt.Color;
import java.util.*;

public class MultiChoicePollCommand extends CommandClass {

    private static final String COMMAND_FRIENDLY_NAME = "MultiChoicePoll";

    public MultiChoicePollCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    @Override
    public void execute(Merenda merenda, String[] command, MessageReceivedEvent event) {
        if (!event.isFromGuild()) {
            event.getChannel().sendMessageEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Funcionalidade não disponível", "Desculpa, mas esta funcionalidade só pode ser utilizada em servidores.")
            ).queue();
            return;
        }

        List<String> commandWithQuotes = joinQuotes(command);

        String pollDescription = commandWithQuotes.get(1);
        List<String> options = commandWithQuotes.subList(2, commandWithQuotes.size());

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0, 220, 240));
        eb.setTitle("Votação - " + pollDescription);

        eb.addField("Iniciada por:", String.format("<@%s>", event.getAuthor().getId()), true);
        eb.addField("Status:", "Aberta :pencil:", true);

        SelectionMenu.Builder selectionMenu = SelectionMenu.create("command multipoll vote");
        selectionMenu.setRequiredRange(1, options.size());
        selectionMenu.setPlaceholder("Escolhe uma ou várias opções.");

        Set<String> optionsSet = new HashSet<>(options);
        for (String option : optionsSet) {
            selectionMenu.addOption(option, option.toLowerCase().replace(" ", "-"));
        }
        event.getChannel().sendMessageEmbeds(eb.build()).setActionRow(
                selectionMenu.build()
        ).queue(message -> {
            MultiChoicePoll poll = new MultiChoicePollClass(message, event.getAuthor(), pollDescription, optionsSet);
            Merenda.getInstance().addPoll(poll);
            event.getMessage().delete().queue();
        });
    }

    @Override
    public void processSelectionMenu(Merenda merenda, SelectionMenuEvent event) {
        List<String> values = event.getValues();

        Poll pollInstance = merenda.getPoll(event.getMessageId());
        if (pollInstance == null) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação não encontrada", "Não encontrei essa votação. Provavelmente já encerrou ou existe um erro algures...")
            ).setEphemeral(true).queue();
            return;
        }

        if (!(pollInstance instanceof MultiChoicePoll)) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Votação inválida", "Essa votação não é uma votação de escolha múltipla.")
            ).setEphemeral(true).queue();
            return;
        }

        MultiChoicePoll poll = (MultiChoicePoll) pollInstance;

        if (poll.hasVoteFrom(event.getUser())) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Voto já registado", "Desculpa, mas já participaste nesta votação. Os votos são únicos, privados e permanentes!")
            ).setEphemeral(true).queue();
            return;
        }

        if (!poll.hasOptions(values)) {
            event.replyEmbeds(
                    getErrorEmbed(COMMAND_FRIENDLY_NAME, "Opções inválidas", "As opções selecionadas não são válidas para esta votação.")
            ).setEphemeral(true).queue();
            return;
        }

        poll.vote(event.getUser(), values);

        event.replyEmbeds(
                getSuccessEmbed(COMMAND_FRIENDLY_NAME, "Voto registado", "Obrigado! O teu voto foi registado!")
        ).setEphemeral(true).queue();

        // TODO: Check for majority
    }

    private List<String> joinQuotes(String[] commandList) {
        List<String> command = new LinkedList<>();
        StringBuilder builder = new StringBuilder();

        for (String part : commandList) {
            if (part.startsWith("\"") && part.endsWith("\""))
                command.add(part.replace("\"", ""));
            else if (part.startsWith("\"")) {
                builder.append(part.replace("\"", "")).append(" ");
            } else if (part.endsWith("\"")) {
                builder.append(part.replace("\"", ""));
                command.add(builder.toString());
                builder.setLength(0);
            } else {
                if (builder.length() > 0)
                    builder.append(part).append(" ");
                else
                    command.add(part);
            }
        }
        return command;
    }
}
