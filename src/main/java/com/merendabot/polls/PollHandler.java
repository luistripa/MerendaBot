package com.merendabot.polls;

import com.merendabot.GuildManager;
import com.merendabot.polls.exceptions.PollDoesNotExistException;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.*;

public class PollHandler {

    private Map<String, Poll> polls;
    private GuildManager guild;

    public PollHandler(GuildManager guild) {
        polls = new HashMap<>();
        this.guild = guild;
        loadPolls();
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
        polls.put(poll.getMessageId(), poll);
        poll.setGuild(guild);
        poll.insert();
    }

    /**
     * Closes a poll with the given id and displays its results in the original message.
     *
     * @throws PollDoesNotExistException if the poll does not exist
     */
    public void closePoll(Message message) throws PollDoesNotExistException {
        Poll poll = getPoll(message.getId());
        poll.setClosed(true);
        poll.update();
        MessageEmbed messageEmbed = poll.getResults();

        // setActionRows() is necessary to remove poll controls from original message
        message.editMessageEmbeds(messageEmbed).setActionRows().queue();
    }

    /**
     * Loads polls from the database into the guild cache.
     */
    private void loadPolls() {
        List<Poll> binaryPolls = BinaryPoll.getPolls();
        for (Poll poll :
                binaryPolls) {
            if (!poll.getClosed())
                polls.put(poll.getMessageId(), poll);
        }

        List<Poll> multiChoicePolls = MultiChoicePoll.getPolls();
        for (Poll poll :
                multiChoicePolls) {
            if (!poll.getClosed())
                polls.put(poll.getMessageId(), poll);
        }
    }
}
