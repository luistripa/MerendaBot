package com.merendabot.university.polls;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPoll implements Poll {

    private final Message message;
    private final User owner;
    private final String description;
    private final Set<User> userSet;

    private boolean isClosed;

    protected AbstractPoll(Message message, User owner, String description) {
        this.message = message;
        this.owner = owner;
        this.description = description;
        this.userSet = new HashSet<>();

        setClosed(false);
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

    public Set<User> getUserSet() {
        return userSet;
    }

    public boolean hasVoteFrom(User user) {
        return userSet.contains(user);
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public abstract void closePoll();
}
