package com.merendabot.university.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class PollClass extends AbstractPoll implements BinaryPoll {

    // Vote counting
    private int forVotes;
    private int abstainVotes;
    private int againstVotes;

    public PollClass(Message message, User owner, String description) {
        super(message, owner, description);
        this.forVotes = 0;
        this.abstainVotes = 0;
        this.againstVotes = 0;
    }

    @Override
    public int getVotesFor() {
        return forVotes;
    }

    @Override
    public int getVotesAbstain() {
        return abstainVotes;
    }

    @Override
    public int getVotesAgainst() {
        return againstVotes;
    }

    @Override
    public int getVoteCount() {
        return forVotes + abstainVotes + againstVotes;
    }

    @Override
    public boolean hasMajority(int memberCount) {
        int votesLeft = memberCount - this.getVoteCount();
        return forVotes > againstVotes+votesLeft || againstVotes > forVotes + votesLeft;
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
    public void voteFor(User user) {
        if (isClosed())
            return;

        if (hasVoteFrom(user))
            return;
        forVotes += 1;
        getUserSet().add(user);
    }

    @Override
    public void voteAbstain(User user) {
        if (isClosed())
            return;

        if (hasVoteFrom(user))
            return;
        abstainVotes += 1;
        getUserSet().add(user);
    }

    @Override
    public void voteAgainst(User user) {
        if (isClosed())
            return;

        if (hasVoteFrom(user))
            return;
        againstVotes += 1;
        getUserSet().add(user);
    }

    @Override
    public void closePoll() {
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
        eb.addField("A Favor", String.valueOf(getVotesFor()), true);
        eb.addField("Absteve-se", String.valueOf(getVotesAbstain()), true);
        eb.addField("Contra", String.valueOf(getVotesAgainst()), true);

        setClosed(true);
        getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
