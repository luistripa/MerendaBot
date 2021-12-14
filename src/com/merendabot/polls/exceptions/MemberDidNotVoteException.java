package com.merendabot.polls.exceptions;

public class MemberDidNotVoteException extends Exception {

    public MemberDidNotVoteException(String userId) {
        super("Member did not vote: "+userId);
    }
}
