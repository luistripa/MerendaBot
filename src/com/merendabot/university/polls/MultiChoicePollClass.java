package com.merendabot.university.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MultiChoicePollClass extends AbstractPoll implements MultiChoicePoll {

    public final Map<String, MultiChoicePollOption> options;
    private int voteCount;

    public MultiChoicePollClass(Message message, User owner, String description, Set<String> optionsSet) {
        super(message, owner, description);

        this.options = new HashMap<>();
        this.voteCount = 0;
        for (String option : optionsSet)
            this.options.put(
                    option.toLowerCase().replace(" ", "-"),
                    new MultiChoicePollOption(option)
            );
    }

    @Override
    public boolean hasOption(String option) {
        return options.get(option) != null;
    }

    @Override
    public boolean hasOptions(List<String> options) {
        for (String option : options) {
            if (!hasOption(option))
                return false;
        }
        return true;
    }

    @Override
    public void vote(User user, List<String> values) {
        getUserSet().add(user);
        for (String value : values) {
            options.get(value).vote();
        }
        voteCount++;
    }

    @Override
    public int getVoteCount() {
        return voteCount;
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int majorityVoteThreshold = (memberCount / 2) + (memberCount%2);

        for (String option : options.keySet()) {
            int votes = options.get(option).getVotes();
            if (votes > majorityVoteThreshold) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void closePoll() {
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
            fieldValue.append(String.format("%s [%d voto(s)]%n", option.getOptionStr(), option.getVotes()));
        }

        eb.addField(
                "Votos",
                fieldValue.toString(),
                false
        );

        getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
