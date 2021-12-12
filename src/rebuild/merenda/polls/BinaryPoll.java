package rebuild.merenda.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import rebuild.merenda.polls.exceptions.MemberAlreadyVotedException;
import rebuild.merenda.polls.exceptions.PollClosedException;

import java.awt.*;

/**
 * Represents a BinaryPoll.
 *
 * A BinaryPoll is a type of poll that can only have two kinds of votes, For or Against.
 * A BinaryPoll also has the 'Abstain' vote.
 */
public class BinaryPoll extends Poll {

    private int forVotes;
    private int abstainVotes;
    private int againstVotes;

    private boolean isClosed;

    public BinaryPoll(Message message, User owner, String description) {
        super(message, owner, description);
        isClosed = false;
    }

    /**
     * Gets all 'For' votes count.
     *
     * @return Int representing the number of 'For' votes
     */
    public int getForVotes() {
        return forVotes;
    }

    /**
     * Gets all 'Abstain' votes count.
     *
     * @return Int representing the number of 'Abstain' votes
     */
    public int getAbstainVotes() {
        return abstainVotes;
    }

    /**
     * Gets all 'Against' votes count.
     *
     * @return Int representing the number of 'Against' votes
     */
    public int getAgainstVotes() {
        return againstVotes;
    }

    /**
     * Checks if the poll has been approved.
     *
     * @return True if approved, False otherwise.
     */
    public boolean isApproved() {
        return forVotes > againstVotes;
    }

    /**
     * Checks if the poll has entered a draw.
     *
     * @return True if is draw, False otherwise.
     */
    public boolean isDraw() {
        return forVotes == againstVotes;
    }

    /**
     * Checks if the poll has been rejected.
     *
     * @return True if rejected, False otherwise.
     */
    public boolean isRejected() {
        return againstVotes > forVotes;
    }

    /**
     * Deposits a 'For' vote from the given user.
     *
     * @param user The user object
     * @throws PollClosedException if poll is already closed
     * @throws MemberAlreadyVotedException if member has already voted
     */
    public void voteFor(User user) throws PollClosedException, MemberAlreadyVotedException {
        if (isClosed())
            throw new PollClosedException(getId());

        addVoter(user);
        forVotes += 1;
    }

    /**
     * Deposits an 'Abstain' vote from the given user.
     *
     * @param user The user object
     * @throws PollClosedException if poll is already closed
     * @throws MemberAlreadyVotedException if member has already voted
     */
    public void voteAbstain(User user) throws PollClosedException, MemberAlreadyVotedException {
        if (isClosed())
            throw new PollClosedException(getId());

        addVoter(user);
        abstainVotes += 1;
    }

    /**
     * Deposits an 'Against' vote from the given user.
     *
     * @param user The user object
     * @throws PollClosedException if poll is already closed
     * @throws MemberAlreadyVotedException if member has already voted
     */
    public void voteAgainst(User user) throws PollClosedException, MemberAlreadyVotedException {
        if (isClosed())
            throw new PollClosedException(getId());

        addVoter(user);
        againstVotes += 1;
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int votesLeft = memberCount - this.getVoteCount();
        return forVotes > againstVotes+votesLeft || againstVotes > forVotes + votesLeft;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void close() {
        EmbedBuilder eb = new EmbedBuilder(getMessage().getEmbeds().get(0));
        eb.clearFields();
        eb.addField("Iniciada por:", String.format("<@%s>", this.getOwner().getId()), true);

        if (isApproved()) {
            eb.setColor(new Color(150, 255, 70));
            eb.setDescription("A votação foi aprovada.");
            eb.addField("Status:", "Aprovada :white_check_mark:", true);

        } else if (isRejected()) {
            eb.setColor(new Color(255, 50, 50));
            eb.setDescription("A votação não foi aprovada.");
            eb.addField("Status:", "Rejeitada :x:", true);

        } else if (isDraw()) {
            eb.setColor(Color.ORANGE);
            eb.setDescription("A votação não foi aprovada.");
            eb.addField("Status:", "Empate :warning:", true);
        }

        eb.addBlankField(false);
        eb.addField("A Favor", String.valueOf(forVotes), true);
        eb.addField("Absteve-se", String.valueOf(abstainVotes), true);
        eb.addField("Contra", String.valueOf(againstVotes), true);

        isClosed = true;

        // setActionRows() is necessary to remove poll controls from original message
        getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
