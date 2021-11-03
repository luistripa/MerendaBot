package com.merendabot.university.polls;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface Poll {

    /**
     * Gets the id of the poll.
     * The poll id is equal to the original poll message id.
     *
     * @return The poll id
     */
    String getId();

    /**
     * Gets the original message that holds the poll information.
     * This is the message that is going to be updated once the poll ends.
     *
     * @return A Message Object containing the poll original message.
     */
    Message getMessage();

    /**
     * Gets the description of the poll.
     *
     * Example: Do you agree with pinable on pizza?
     *
     * @return A String with the message of the poll
     */
    String getDescription();

    /**
     * Gets the "owner" of the poll. The owner is the person who started the poll.
     *
     * @return A User Object containing the owner of the poll.
     */
    User getOwner();

    /**
     * Checks if the given user already voted in the poll.
     *
     * @param user A User Object
     * @return True if user has voted, False otherwise.
     */
    boolean hasVoteFrom(User user);

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
     * Get the total number of votes.
     *
     * @return The total number of votes.
     */
    int getVoteCount();

    /**
     * Checks if poll has reached a majority vote.
     *
     * @param memberCount The number of members to check for majority
     * @return True if poll has reached a majority, False otherwise.
     */
    boolean hasMajority(int memberCount);

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
     * Checks if the poll is closed.
     * A closed poll does not accept new votes.
     *
     * @return True if is closed, False otherwise.
     */
    boolean isClosed();

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

    /**
     * Closes a poll. This method updates the original message to include the poll results.
     */
    void closePoll();
}
