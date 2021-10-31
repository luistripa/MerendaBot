package com.merendabot.university.polls;

import com.merendabot.university.Merenda;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PollHandler {

    private final Map<String, Poll> pollMap;

    public PollHandler() {
        pollMap = new HashMap<>();
    }

    @Nullable public Poll getPoll(String pollId) {
        return pollMap.get(pollId);
    }

    public Collection<Poll> getPolls() {
        return new ArrayList<>(pollMap.values());
    }

    public void addPoll(Poll pollClass) {
        pollMap.put(pollClass.getMessage().getId(), pollClass);
    }

    public void endPoll(String pollId) {
        Poll poll = pollMap.remove(pollId);
        if (!poll.isClosed())
            poll.closePoll();
    }
}
