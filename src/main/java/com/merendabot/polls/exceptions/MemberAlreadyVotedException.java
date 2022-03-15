package com.merendabot.polls.exceptions;

public class MemberAlreadyVotedException extends Exception {

    public MemberAlreadyVotedException(String memberId) {
        super("Member already voted: "+memberId);
    }
}
