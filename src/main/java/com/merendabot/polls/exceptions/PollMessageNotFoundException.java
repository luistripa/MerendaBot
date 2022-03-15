package com.merendabot.polls.exceptions;

public class PollMessageNotFoundException extends Exception {

    public PollMessageNotFoundException() {
        super("Poll message was not found. Maybe it was deleted or an error occurred.");
    }
}
