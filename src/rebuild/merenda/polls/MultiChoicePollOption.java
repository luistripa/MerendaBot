package rebuild.merenda.polls;

import org.jetbrains.annotations.NotNull;

public class MultiChoicePollOption implements Comparable<MultiChoicePollOption> {

    private String description;
    private int voteCount;

    public MultiChoicePollOption(String description) {
        this.description = description;
        this.voteCount = 0;
    }

    public String getDescription() {
        return description;
    }

    public int getVoteCount() {
        return voteCount;
    }

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
