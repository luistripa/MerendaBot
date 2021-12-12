package rebuild.merenda.polls;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an option for the MultiChoicePoll.
 */
public class MultiChoicePollOption implements Comparable<MultiChoicePollOption> {

    private String description;
    private int voteCount;

    public MultiChoicePollOption(String description) {
        this.description = description;
        this.voteCount = 0;
    }

    /**
     * Gets the description of the option.
     *
     * @return String with the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the number of votes for the option.
     *
     * @return Int with the number of votes for the option
     */
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * Increment the number of votes.
     */
    public void vote() {
        voteCount++;
    }

    @Override
    public int compareTo(@NotNull MultiChoicePollOption o) {
        if (this == o)
            return 0;
        else if (this.description.equals(o.description))
            return 0;
        else if (this.voteCount > o.voteCount)
            return -1;
        else if (this.voteCount == o.voteCount)
            return 0;
        else return 1;
    }
}
