package com.merendabot.university.polls;

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

    /**
     * Gets a poll from the system.
     *
     * @param pollId The poll id. This is equal to the id of the poll's original message
     * @return A Poll object if found, null otherwise.
     */
    @Nullable public Poll getPoll(String pollId) {
        return pollMap.get(pollId);
    }

    /**
     * Gets all polls from the system
     *
     * @return A Collection of polls
     */
    public Collection<Poll> getPolls() {
        return new ArrayList<>(pollMap.values());
    }

    /**
     * Adds a poll to the system.
     *
     * @param poll A Poll object
     */
    public void addPoll(Poll poll) {
        pollMap.put(poll.getMessage().getId(), poll);
    }

    /**
     * Terminates a poll.
     * This will update the original message with the results and remove its buttons.
     *
     * @param pollId The poll id. This is equal to the id of the poll's original message
     */
    public void endPoll(String pollId) {
        Poll poll = pollMap.remove(pollId);
        if (!poll.isClosed())
            poll.closePoll();
    }
}
