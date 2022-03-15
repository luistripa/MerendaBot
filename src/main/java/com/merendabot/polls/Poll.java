package com.merendabot.polls;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.polls.exceptions.MemberDidNotVoteException;
import com.merendabot.polls.exceptions.PollMessageNotFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents the base of a poll. All polls must inherit from this class.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id", nullable = false)
    private GuildManager guild;

    @Column(nullable = false)
    private String textChannelId;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String description;

    @Column(name = "datetime_start", nullable = false)
    private Timestamp startDateTime;

    @Column(name = "datetime_end", nullable = true)
    private Timestamp endDateTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "voter")
    @CollectionTable(name = "guild_polls_voters", joinColumns = @JoinColumn( name = "poll_id", nullable = false))
    private Set<String> voters = new HashSet<>();

    @Column(nullable = false)
    private Boolean closed;

    public Poll() {}

    public Poll(Message message, User owner, String description) {
        this.textChannelId = message.getTextChannel().getId();
        this.messageId = message.getId();
        this.userId = owner.getId();
        this.description = description;
        this.startDateTime = Timestamp.valueOf(LocalDateTime.now());

        voters = new HashSet<>();
        closed = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GuildManager getGuild() {
        return guild;
    }

    public void setGuild(GuildManager guild) {
        this.guild = guild;
    }

    public String getTextChannelId() {
        return textChannelId;
    }

    public void setTextChannelId(String textChannelId) {
        this.textChannelId = textChannelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the description of the poll.
     *
     * @return The description of the poll
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets all voters of the poll.
     *
     * @return A Set of Users
     */
    public Set<String> getVoters() {
        return voters;
    }

    public void setVoters(Set<String> voters) {
        this.voters = voters;
    }

    /**
     * Gets the number of votes.
     *
     * @return The number of votes
     */
    public int getVoteCount() {
        return voters.size();
    }

    /**
     * Checks if the user has voted in the poll.
     *
     * @param user A User object
     * @return True if it has voted, False otherwise.
     */
    public boolean hasVote(User user) {
        return voters.contains(user.getId());
    }

    /**
     * Adds a new voter to the poll.
     *
     * @param user A User object
     * @throws MemberAlreadyVotedException if the user already voted
     */
    public void addVoter(User user) throws MemberAlreadyVotedException {
        if (hasVote(user))
            throw new MemberAlreadyVotedException(user.getName());
        voters.add(user.getId());
    }

    /**
     * Removes a voter from the poll, for example, when the user wants to cancel his vote.
     *
     * @param user A User object
     * @throws MemberDidNotVoteException if the user did not vote in the poll
     */
    public void removeVoter(User user) throws MemberDidNotVoteException {
        if (!voters.remove(user.getId()))
            throw new MemberDidNotVoteException(user.getName());
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
        this.endDateTime = Timestamp.valueOf(LocalDateTime.now());
    }

    /**
     * Checks if the poll has reached a majority vote.
     *
     * @param memberCount The number of non-bot members in the server to compare to current votes
     * @return True if poll has reached majority, False otherwise.
     */
    public abstract boolean hasMajority(int memberCount);

    public abstract MessageEmbed getResults();

    public abstract void insert();

    public abstract void update();
}
