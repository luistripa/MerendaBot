package com.merendabot.commands;

import net.dv8tion.jda.api.entities.User;

/**
 * Represents a restricted command. Restricted com.merendabot.commands are com.merendabot.commands that can only be accessed by the administrator.
 */
public abstract class RestrictedCommandClass extends CommandClass {

    protected RestrictedCommandClass(CommandCategory category, String name, String description) {
        super(category, name, description);
    }

    /**
     * Checks if the provided user is the admin.
     *
     * @param user A User Object
     * @return True if user is admin, False otherwise.
     */
    protected boolean isAdmin(User user) {
        return user.getId().equals(ADMIN_ID);
    }
}
