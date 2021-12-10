package rebuild.merenda.polls.exceptions;

public class MemberAlreadyVotedException extends Exception {

    public MemberAlreadyVotedException(String memberId) {
        super("Member already voted: "+memberId);
    }
}
