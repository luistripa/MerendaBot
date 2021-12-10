package rebuild.merenda.polls;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import rebuild.merenda.polls.exceptions.MemberAlreadyVotedException;
import rebuild.merenda.polls.exceptions.MemberDidNotVoteException;

import java.util.HashSet;
import java.util.Set;

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

    public String getId() {
        return message.getId();
    }

    public Message getMessage() {
        return message;
    }

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public int getVoteCount() {
        return voters.size();
    }

    public Set<User> getVoters() {
        return voters;
    }

    public boolean hasVote(User user) {
        return voters.contains(user);
    }

    public void addVoter(User user) throws MemberAlreadyVotedException {
        if (hasVote(user))
            throw new MemberAlreadyVotedException(user.getName());
        voters.add(user);
    }

    public void removeVoter(User user) throws MemberDidNotVoteException {
        if (!voters.remove(user))
            throw new MemberDidNotVoteException(user.getName());
    }

    // These 2 methods are abstract in order to enable different poll types to have their own close system
    public abstract boolean isClosed();

    public abstract void close();
}
