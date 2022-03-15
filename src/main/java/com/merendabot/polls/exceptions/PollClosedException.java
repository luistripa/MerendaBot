package com.merendabot.polls.exceptions;

public class PollClosedException extends Exception {

    public PollClosedException(String pollId) {
        super("Poll is already closed: "+pollId);
    }
}
