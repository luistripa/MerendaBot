package rebuild.merenda.polls.exceptions;

public class PollDoesNotExistException extends Exception {

    public PollDoesNotExistException(String pollId) {
        super("Poll does not exist: "+pollId);
    }
}
