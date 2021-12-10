package rebuild.merenda.polls;

import rebuild.merenda.polls.exceptions.PollDoesNotExistException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PollHandler {

    private Map<String, Poll> polls;

    public PollHandler() {
        polls = new HashMap<>();
    }

    private Poll getPoll(String pollId) throws PollDoesNotExistException {
        if (!hasPoll(pollId))
            throw new PollDoesNotExistException(pollId);
        return polls.get(pollId);
    }

    private List<Poll> getPolls() {
        return new ArrayList<>(polls.values());
    }

    private boolean hasPoll(String pollId) {
        return polls.get(pollId) != null;
    }

    private void addPoll(Poll poll) {
        polls.put(poll.getId(), poll);
    }

    private void closePoll(String pollId) throws PollDoesNotExistException {
        Poll poll = getPoll(pollId);
        poll.close();
    }

}
