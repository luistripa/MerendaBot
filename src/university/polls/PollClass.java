package university.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class PollClass implements Poll {

    private final Message message;
    private final String description;

    private final User owner;
    private final Set<User> userSet;

    // Vote counting
    private int for_votes;
    private int abstain_votes;
    private int against_votes;

    private boolean isClosed;

    public PollClass(Message message, User owner, String description) {
        this.message = message;
        this.description = description;

        this.owner = owner;
        this.userSet = new HashSet<>();
        this.for_votes = 0;
        this.abstain_votes = 0;
        this.against_votes = 0;

        this.isClosed = false;
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public boolean hasVoteFrom(User user) {
        return userSet.contains(user);
    }

    @Override
    public int getVotesFor() {
        return for_votes;
    }

    @Override
    public int getVotesAbstain() {
        return abstain_votes;
    }

    @Override
    public int getVotesAgainst() {
        return against_votes;
    }

    @Override
    public int getVoteCount() {
        return for_votes + abstain_votes + against_votes;
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int votesLeft = memberCount - this.getVoteCount();
        return for_votes > against_votes+votesLeft || against_votes > for_votes + votesLeft;
    }

    @Override
    public boolean  isApproved() {
        return getVotesFor() > getVotesAgainst();
    }

    @Override
    public boolean isDraw() {
        return getVotesFor() == getVotesAgainst();
    }

    @Override
    public boolean isRejected() {
        return getVotesAgainst() > getVotesFor();
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void voteFor(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        for_votes += 1;
        userSet.add(user);
    }

    @Override
    public void voteAbstain(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        abstain_votes += 1;
        userSet.add(user);
    }

    @Override
    public void voteAgainst(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        against_votes += 1;
        userSet.add(user);
    }

    @Override
    public void closePoll() {
        EmbedBuilder eb = new EmbedBuilder(message.getEmbeds().get(0));
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
        eb.addField("A Favor", String.valueOf(getVotesFor()), true);
        eb.addField("Absteve-se", String.valueOf(getVotesAbstain()), true);
        eb.addField("Contra", String.valueOf(getVotesAgainst()), true);

        isClosed = true;
        message.editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
