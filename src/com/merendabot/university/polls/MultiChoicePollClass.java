package com.merendabot.university.polls;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class MultiChoicePollClass extends AbstractPoll implements MultiChoicePoll {

    public final Map<String, Integer> options;
    private int voteCount;

    public MultiChoicePollClass(Message message, User owner, String description, Set<String> optionsSet) {
        super(message, owner, description);

        this.options = new HashMap<>();
        this.voteCount = 0;
        for (String option : optionsSet)
            this.options.put(option.toLowerCase().replace(" ", "-"), 0);
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
            options.replace(value, options.get(value)+1);
        }
        voteCount++;
    }

    @Override
    public int getVoteCount() {
        return 0; // TODO
    }

    @Override
    public boolean hasMajority(int memberCount) {
        return false; // TODO
    }

    @Override
    public void closePoll() {
        // TODO
    }
}
