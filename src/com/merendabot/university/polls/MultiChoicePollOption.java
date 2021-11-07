package com.merendabot.university.polls;

import org.jetbrains.annotations.NotNull;

public class MultiChoicePollOption implements Comparable<MultiChoicePollOption> {

    private final String optionStr;
    private int votes;

    public MultiChoicePollOption(String optionStr) {
        this.optionStr = optionStr;
        this.votes = 0;
    }

    public String getOptionStr() {
        return optionStr;
    }

    public int getVotes() {
        return votes;
    }

    public void vote() {
        this.votes++;
    }

    @Override
    public int compareTo(@NotNull MultiChoicePollOption o) {
        if (this == o)
            return 0;
        else if (this.optionStr.equals(o.optionStr))
            return 0;
        else if (this.votes > o.votes)
            return -1;
        else if (this.votes == o.votes)
            return 0;
        else return 1;
    }
}
