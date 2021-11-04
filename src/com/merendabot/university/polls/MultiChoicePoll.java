package com.merendabot.university.polls;

import net.dv8tion.jda.api.entities.User;

import java.util.List;

public interface MultiChoicePoll extends Poll {

    boolean hasOption(String option);

    boolean hasOptions(List<String> options);

    void vote(User user, List<String> values);
}
