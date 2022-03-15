package main.java.com.merendabot.polls;

import main.java.com.merendabot.Merenda;
import main.java.com.merendabot.polls.exceptions.MemberAlreadyVotedException;
import main.java.com.merendabot.polls.exceptions.MemberDidNotVoteException;
import main.java.com.merendabot.polls.exceptions.PollClosedException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Represents a BinaryPoll.
 *
 * A BinaryPoll is a type of poll that can only have two kinds of votes, For or Against.
 * A BinaryPoll also has the 'Abstain' vote.
 */
@Entity
@Table(name = "guild_polls_binary")
public class BinaryPoll extends Poll {

    @Column(nullable = false)
    private int forVotes;

    @Column(nullable = false)
    private int abstainVotes;

    @Column(nullable = false)
    private int againstVotes;

    public BinaryPoll(Message message, User owner, String description) {
        super(message, owner, description);
    }

    public BinaryPoll(Message message, User owner, String description, int forVotes, int abstainVotes, int againstVotes) {
        super(message, owner, description);

        this.forVotes = forVotes;
        this.abstainVotes = abstainVotes;
        this.againstVotes = againstVotes;
    }

    protected BinaryPoll() {}

    /**
     * Gets all 'For' votes count.
     *
     * @return Int representing the number of 'For' votes
     */
    public int getForVotes() {
        return forVotes;
    }

    public void setForVotes(int forVotes) {
        this.forVotes = forVotes;
    }

    /**
     * Gets all 'Abstain' votes count.
     *
     * @return Int representing the number of 'Abstain' votes
     */
    public int getAbstainVotes() {
        return abstainVotes;
    }

    public void setAbstainVotes(int abstainVotes) {
        this.abstainVotes = abstainVotes;
    }

    /**
     * Gets all 'Against' votes count.
     *
     * @return Int representing the number of 'Against' votes
     */
    public int getAgainstVotes() {
        return againstVotes;
    }

    public void setAgainstVotes(int againstVotes) {
        this.againstVotes = againstVotes;
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
        if (getClosed())
            throw new PollClosedException(getMessageId());

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
        if (getClosed())
            throw new PollClosedException(getMessageId());

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
        if (getClosed())
            throw new PollClosedException(getMessageId());

        addVoter(user);
        againstVotes += 1;
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int votesLeft = memberCount - this.getVoteCount();
        return (forVotes > againstVotes+votesLeft || againstVotes > forVotes + votesLeft);
    }

    @Override
    public MessageEmbed getResults() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(
                String.format("Votação - %s", getDescription())
        );
        eb.addField("Iniciada por:", String.format("<@%s>", getUserId()), true);

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

        return eb.build();
    }

    @Override
    public void insert() {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.persist(this);
        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            session.close();
            throw new RuntimeException("BinaryPoll persist failed: "+ throwable.getMessage());
        }

        tx.commit();
        session.close();
    }

    @Override
    public void update() {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(this);
        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            session.close();
            throw new RuntimeException("BinaryPoll update failed: "+ throwable.getMessage());
        }

        tx.commit();
        session.close();
    }

    static List<Poll> getPolls() {
        List binaryPolls;
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            binaryPolls = session.createQuery("from BinaryPoll").list();
        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            session.close();
            throw new RuntimeException("BinaryPoll load from database failed: "+ throwable.getMessage());
        }

        tx.commit();
        session.close();

        return binaryPolls;
    }
}
