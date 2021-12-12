package rebuild.merenda.polls;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import rebuild.merenda.polls.exceptions.MemberAlreadyVotedException;
import rebuild.merenda.polls.exceptions.MemberDidNotVoteException;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the base of a poll. All polls must inherit from this class.
 */
public abstract class Poll {

    private final Message message;
    private final User owner;
    private final String description;
    private final Set<User> voters;

    public Poll(Message message, User owner, String description) {
        this.message = message;
        this.owner = owner;
        this.description = description;
        voters = new HashSet<>();
    }

    /**
     * Gets the id of the poll
     *
     * @return Int representing the id of the poll
     */
    public String getId() {
        return message.getId();
    }

    /**
     * Gets the Message object of the poll's original message.
     *
     * @return A Message object
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Gets the User object of the owner of the poll.
     *
     * @return A User object
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Gets the description of the poll.
     *
     * @return The description of the poll
     */
    public String getDescription() {
        return description;
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
     * Gets all voters of the poll.
     *
     * @return A Set of Users
     */
    public Set<User> getVoters() {
        return voters;
    }

    /**
     * Checks if the user has voted in the poll.
     *
     * @param user A User object
     * @return True if it has voted, False otherwise.
     */
    public boolean hasVote(User user) {
        return voters.contains(user);
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
        voters.add(user);
    }

    /**
     * Removes a voter from the poll, for example, when the user wants to cancel his vote.
     *
     * @param user A User object
     * @throws MemberDidNotVoteException if the user did not vote in the poll
     */
    public void removeVoter(User user) throws MemberDidNotVoteException {
        if (!voters.remove(user))
            throw new MemberDidNotVoteException(user.getName());
    }

    /**
     * Checks if the poll has reached a majority vote.
     *
     * @param memberCount The number of non-bot members in the server to compare to current votes
     * @return True if poll has reached majority, False otherwise.
     */
    public abstract boolean hasMajority(int memberCount);

    /**
     * Checks if the poll has closed.
     *
     * @return True if poll has closed, False otherwise.
     */
    public abstract boolean isClosed();

    /**
     * Closes the poll.
     */
    public abstract void close();
}
