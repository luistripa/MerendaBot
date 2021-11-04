package com.merendabot.university.polls;

import net.dv8tion.jda.api.entities.User;

public interface BinaryPoll extends Poll {

    /**
     * Get the number of favourable votes.
     *
     * @return The number of favorable votes.
     */
    int getVotesFor();

    /**
     * Get the number of abstaining votes. Abstaining votes do not count for poll approval or rejection.
     *
     * @return The number of abstaining votes.
     */
    int getVotesAbstain();

    /**
     * Get the number of unfavourable votes.
     *
     * @return The number of unfavourable votes.
     */
    int getVotesAgainst();

    /**
     * Checks if poll was approved.
     *
     * @return True if approved, False otherwise.
     */
    boolean isApproved();

    /**
     * Checks if poll was a draw.
     *
     * @return True if poll is a draw, False otherwise.
     */
    boolean isDraw();

    /**
     * Checks if poll was rejected.
     *
     * @return True if rejected, False otherwise.
     */
    boolean isRejected();

    /**
     * Registers a favourable vote from a user.
     *
     * @param user The voting user
     */
    void voteFor(User user);

    /**
     * Registers an abstaining vote from a user.
     *
     * @param user The voting user
     */
    void voteAbstain(User user);

    /**
     * Registers an unfavourable vote from a user.
     *
     * @param user The voting user
     */
    void voteAgainst(User user);
}
