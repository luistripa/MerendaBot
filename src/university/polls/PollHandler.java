package university.polls;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PollHandler {

    private Map<String, Poll> pollMap;

    public PollHandler() {
        pollMap = new HashMap<>();
    }

    @Nullable public Poll getPoll(String poll_id) {
        return pollMap.get(poll_id);
    }

    public Collection<Poll> getPolls() {
        return new ArrayList<>(pollMap.values());
    }

    public void addPoll(Poll pollClass) {
        pollMap.put(pollClass.getMessage().getId(), pollClass);
    }

    public void endPoll(String poll_id) {
        Poll poll = pollMap.remove(poll_id);
        if (!poll.isClosed())
            poll.closePoll();
    }
}
