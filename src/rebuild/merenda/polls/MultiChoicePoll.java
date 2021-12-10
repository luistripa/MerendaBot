package rebuild.merenda.polls;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import rebuild.merenda.polls.exceptions.MemberAlreadyVotedException;
import rebuild.merenda.polls.exceptions.OptionsNotFoundException;
import rebuild.merenda.polls.exceptions.PollClosedException;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MultiChoicePoll extends Poll {

    private final Map<String, MultiChoicePollOption> options;

    private boolean isClosed;

    public MultiChoicePoll(Message message, User owner, String description, Set<String> optionsSet) {
        super(message, owner, description);

        options = new HashMap<>();

        for (String option : optionsSet) {
            this.options.put(
                    option.toLowerCase().replace(" ", "-"),
                    new MultiChoicePollOption(option)
            );
        }
        isClosed = false;
    }

    public boolean hasOption(String optionId) {
        return options.get(optionId) != null;
    }

    public boolean hasOptions(List<String> values) {
        for (String option : values) {
            if (!hasOption(option))
                return false;
        }
        return true;
    }

    public void vote(User user, List<String> values) throws PollClosedException, OptionsNotFoundException, MemberAlreadyVotedException {
        if (isClosed)
            throw new PollClosedException(getId());

        if (!hasOptions(values))
            throw new OptionsNotFoundException(values);

        addVoter(user);
        for (String value : values) {
            options.get(value).vote();
        }
    }

    public boolean hasMajority(int memberCount) {
        int majorityVoteThreshold = (memberCount / 2) + (memberCount%2);

        for (String option : options.keySet()) {
            int votes = options.get(option).getVoteCount();
            if (votes > majorityVoteThreshold) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void close() {
        EmbedBuilder eb = new EmbedBuilder(getMessage().getEmbeds().get(0));
        eb.clearFields();
        eb.setColor(new Color(150, 255, 70));
        eb.addField("Iniciada por:", String.format("<@%s>", this.getOwner().getId()), true);
        eb.addField("Status:", "Encerrada :white_check_mark:", true);

        eb.addBlankField(false);

        StringBuilder fieldValue = new StringBuilder();

        List<MultiChoicePollOption> optionList = new ArrayList<>(options.values());
        Collections.sort(optionList);
        for (MultiChoicePollOption option : optionList) {
            fieldValue.append(String.format("%s [%d voto(s)]%n", option.getDescription(), option.getVoteCount()));
        }

        eb.addField(
                "Votos",
                fieldValue.toString(),
                false
        );

        isClosed = true;

        getMessage().editMessageEmbeds(eb.build()).setActionRows().queue();
    }
}
