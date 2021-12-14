package com.merendabot.polls;

import com.merendabot.polls.exceptions.PollDoesNotExistException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollHandler {

    private Map<String, Poll> polls;

    public PollHandler() {
        polls = new HashMap<>();
    }

    /**
     * Gets a poll from the list.
     *
     * @param pollId The poll id
     * @return A Poll object
     * @throws PollDoesNotExistException if the poll does not exist
     */
    public Poll getPoll(String pollId) throws PollDoesNotExistException {
        if (!hasPoll(pollId))
            throw new PollDoesNotExistException(pollId);
        return polls.get(pollId);
    }

    /**
     * Gets all polls from the list.
     *
     * @return A List of polls
     */
    public List<Poll> getPolls() {
        return new ArrayList<>(polls.values());
    }

    /**
     * Checks if the poll exists.
     *
     * @param pollId The id of the poll
     * @return True if the poll exists, False otherwise.
     */
    public boolean hasPoll(String pollId) {
        return polls.get(pollId) != null;
    }

    /**
     * Adds a poll to the list.
     *
     * @param poll A Poll object
     */
    public void addPoll(Poll poll) {
        polls.put(poll.getId(), poll);
    }

    /**
     * Closes a poll with the given id.
     *
     * @param pollId The poll id
     * @throws PollDoesNotExistException if the poll does not exist
     */
    public void closePoll(String pollId) throws PollDoesNotExistException {
        Poll poll = getPoll(pollId);
        poll.close();
    }

}
