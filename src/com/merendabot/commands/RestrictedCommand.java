package com.merendabot.commands;

import net.dv8tion.jda.api.entities.User;

public abstract class RestrictedCommand extends Command {

    public static final String ADMIN_ID = "647790893646086167";

    public RestrictedCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    protected boolean isAdmin(User user) {
        return user.getId().equals(ADMIN_ID);
    }
}
