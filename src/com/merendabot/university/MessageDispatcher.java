package com.merendabot.university;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.logging.Logger;

/**
 * Represents the message dispatcher.
 * The message dispatcher is a singleton class that handles default guild channels.
 *
 * Options include sending Strings or MessageEmbeds to provided channels or a default one, set by the programmer.
 */
public class MessageDispatcher {

    private static MessageDispatcher instance = null;

    private static final String DEFAULT_GUILD = "";
    private static final String DEFAULT_CHANNEL = "";

    private static final String DEBUG_GUILD = "797614596985716827";
    private static final String DEBUG_CHANNEL = "897477068831469638";

    private static final Logger logger = Logger.getLogger("main-log");

    private Guild defaultGuild;
    private TextChannel defaultChannel;

    private final boolean debug;

    private MessageDispatcher() {
        String debugString = System.getenv("DEBUG");
        if (debugString != null)
            debug = System.getenv("DEBUG").equals("TRUE");
        else
            debug = false;

        loadDefaultGuild(Merenda.getJDA());
        loadDefaultChannel();
    }

    public static MessageDispatcher getInstance() {
        if (instance == null)
            instance = new MessageDispatcher();
        return instance;
    }

    /**
     * Gets the default guild from the MessageDispatcher.
     * {@link #getInstance()} should have been called at least once before this method should be called.
     *
     * @return A Guild object
     */
    public Guild getDefaultGuild() {
        return defaultGuild;
    }

    /**
     * Gets the default channel where to send timer messages.
     * {@link #getInstance()} should have been called at least once before this method should be called.
     *
     * @return A TextChannel object
     */
    public TextChannel getDefaultChannel() {
        return defaultChannel;
    }


    /*
    PRIVATE METHODS
     */

    private void loadDefaultGuild(JDA jda) {
        if (debug)
            defaultGuild = jda.getGuildById(DEBUG_GUILD);
        else
            defaultGuild = jda.getGuildById(DEFAULT_GUILD);

        if (defaultGuild == null) {
            if (debug)
                logger.severe("Could not find guild with id "+DEBUG_GUILD);
            else
                logger.severe("Could not find guild with id "+DEFAULT_GUILD);
        }
    }

    private void loadDefaultChannel() {
        if (defaultGuild == null) {
            logger.severe("Could not load default channel because guild was not found");
            return;
        }
        if (debug)
            defaultChannel = defaultGuild.getTextChannelById(DEBUG_CHANNEL);
        else
            defaultChannel = defaultGuild.getTextChannelById(DEFAULT_CHANNEL);

        if (defaultChannel == null) {
            if (debug)
                logger.severe("Could not find channel with id "+DEBUG_CHANNEL);
            else
                logger.severe("Could not find channel with id "+DEFAULT_CHANNEL);
        }
    }
}
