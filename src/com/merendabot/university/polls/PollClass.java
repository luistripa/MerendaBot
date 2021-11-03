package com.merendabot.university.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PollClass implements Poll {

    private final Message message;
    private final String description;

    private final User owner;
    private final Set<User> userSet;

    // Vote counting
    private int forVotes;
    private int abstainVotes;
    private int againstVotes;

    private boolean isClosed;

    public PollClass(Message message, User owner, String description) {
        this.message = message;
        this.description = description;

        this.owner = owner;
        this.userSet = new HashSet<>();
        this.forVotes = 0;
        this.abstainVotes = 0;
        this.againstVotes = 0;

        this.isClosed = false;
    }

    @Override
    public String getId() {
        return this.message.getId();
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
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void voteFor(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        forVotes += 1;
        userSet.add(user);
    }

    @Override
    public void voteAbstain(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        abstainVotes += 1;
        userSet.add(user);
    }

    @Override
    public void voteAgainst(User user) {
        if (isClosed)
            return;

        if (hasVoteFrom(user))
            return;
        againstVotes += 1;
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
