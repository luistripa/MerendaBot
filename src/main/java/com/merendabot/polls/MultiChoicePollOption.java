package main.java.com.merendabot.polls;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Represents an option for the MultiChoicePoll.
 */
@Entity
@Table(name = "guild_polls_multi_options")
public class MultiChoicePollOption implements Comparable<MultiChoicePollOption> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int voteCount;

    public MultiChoicePollOption(String description) {
        this.description = description;
        this.voteCount = 0;
    }

    protected MultiChoicePollOption() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the description of the option.
     *
     * @return String with the description
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the number of votes for the option.
     *
     * @return Int with the number of votes for the option
     */
    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
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
