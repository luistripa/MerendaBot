package com.merendabot.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import com.merendabot.polls.exceptions.OptionsNotFoundException;
import com.merendabot.polls.exceptions.PollClosedException;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represents a MultiChoicePoll.
 *
 * A MultiChoicePoll is a type of poll that has multiple options. Members can choose multiple options from the list.
 */
public class MultiChoicePoll extends Poll {

    private final Map<String, MultiChoicePollOption> options;

    private boolean isClosed;

    public MultiChoicePoll(Message message, User owner, String description, Set<String> optionsSet) {
        super(message, owner, description);

        options = new HashMap<>();

        for (String option : optionsSet) {
            this.options.put(
                    option.toLowerCase().replace(" ", "-"),
                    new MultiChoicePollOption(option)
            );
        }
        isClosed = false;
    }

    /**
     * Checks if the poll has the given option in its options list.
     *
     * @param optionId The id of the option
     * @return True if it has option, False otherwise
     */
    public boolean hasOption(String optionId) {
        return options.get(optionId) != null;
    }

    /**
     * Checks if the poll has ALL the options in the given list in its options list.
     *
     * @param values A List of options
     * @return True if has options, False otherwise
     */
    public boolean hasOptions(List<String> values) {
        for (String option : values) {
            if (!hasOption(option))
                return false;
        }
        return true;
    }

    /**
     * Deposits a vote from the given user with the given options.
     *
     * @param user A User object
     * @param values The options chosen by the user
     * @throws PollClosedException if poll has already been closed
     * @throws OptionsNotFoundException if one of the options in the list does not exist in the poll's option list
     * @throws MemberAlreadyVotedException if the user has already voted
     */
    public void vote(User user, List<String> values) throws PollClosedException, OptionsNotFoundException, MemberAlreadyVotedException {
        if (isClosed)
            throw new PollClosedException(getId());

        if (!hasOptions(values))
            throw new OptionsNotFoundException(values);

        addVoter(user);
        for (String value : values) {
            options.get(value).vote();
        }
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int majorityVoteThreshold = (memberCount / 2) + (memberCount%2);

        for (String option : options.keySet()) {
            int votes = options.get(option).getVoteCount();
            if (votes > majorityVoteThreshold) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void close() {
        EmbedBuilder eb = new EmbedBuilder(getMessage().getEmbeds().get(0));
        eb.clearFields();
        eb.setColor(new Color(150, 255, 70));
        eb.addField("Iniciada por:", String.format("<@%s>", this.getOwner().getId()), true);
        eb.addField("Status:", "Encerrada :white_check_mark:", true);

        eb.addBlankField(false);

        StringBuilder fieldValue = new StringBuilder();

        List<MultiChoicePollOption> optionList = new ArrayList<>(options.values());
        Collections.sort(optionList);
        for (MultiChoicePollOption option : optionList) {
            fieldValue.append(String.format("%s [%d voto(s)]%n", option.getDescription(), option.getVoteCount()));
        }

        eb.addField(
                "Votos",
                fieldValue.toString(),
                false
        );

        isClosed = true;

        getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
