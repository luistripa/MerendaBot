package com.merendabot.university;

import com.merendabot.commands.CallbackCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.logging.Logger;

/**
 * Represents the message dispatcher.
 * The message dispatcher is a singleton class that handles message queuing.
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

    protected MessageDispatcher() {
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
     * Sends a String message to the default channel.
     *
     * @param messageContent The content of the message
     */
    public void sendMessage(String messageContent, Component ... components) {
        defaultChannel.sendMessage(messageContent).setActionRow(components).queue();
    }

    /**
     * Sends a MessageEmbed to the default channel.
     *
     * @param embed A MessageEmbed object
     */
    public void sendMessage(MessageEmbed embed, Component ... components) {
        defaultChannel.sendMessageEmbeds(embed).setActionRow(components).queue();
    }

    /**
     * Sends a String message with no callback to the command.
     *
     * @param event The event that called the command
     * @param messageContent The content  of the message
     */
    public void sendMessage(MessageReceivedEvent event, String messageContent) {
        event.getChannel().sendMessage(messageContent).queue();
    }

    /**
     * Sends a MessageEmbed with no callback to the command.
     *
     * @param event The event that called the command
     * @param messageEmbed A MessageEmbed object
     */
    public void sendMessageEmbed(MessageReceivedEvent event, MessageEmbed messageEmbed) {
        event.getChannel().sendMessageEmbeds(messageEmbed).queue();
    }

    /**
     * Sends a String message with callback to the command.
     *
     * @param event The event that called the command
     * @param messageContent The content of the message
     * @param callbackCommand A CommandCallback object to callback to
     */
    public void sendMessageWithCallback(MessageReceivedEvent event, String messageContent, CallbackCommand callbackCommand) {
        event.getChannel().sendMessage(messageContent).queue(message -> callbackCommand.messageCallback(message, event));
    }

    /**
     * Sends a MessageEmbed with callback to the command.
     *
     * @param event The event that called the command
     * @param messageEmbed A MessageEmbed object
     * @param callbackCommand A CommandCallback object to callback to
     */
    public void sendMessageEmbedWithCallback(MessageReceivedEvent event, MessageEmbed messageEmbed, CallbackCommand callbackCommand) {
        event.getChannel().sendMessageEmbeds(messageEmbed).queue(message -> callbackCommand.messageCallback(message, event));
    }

    /**
     * Replies to a message with no callback.
     *
     * @param event The event that called the command.
     * @param messageContent the content of the message
     */
    public void reply(MessageReceivedEvent event, String messageContent) {
        event.getMessage().reply(messageContent).queue();
    }

    /**
     * Replies to a message with a callback.
     *
     * @param event The event that called the command.
     * @param messageContent The content of the message
     * @param callbackCommand The command to callback to
     */
    public void replyWithCallback(MessageReceivedEvent event, String messageContent, CallbackCommand callbackCommand) {
        event.getMessage().reply(messageContent).queue(message -> callbackCommand.messageCallback(message, event));
    }

    /**
     * Replies a MessageEmbed object with no callback.
     *
     * @param event The event that called the command.
     * @param messageEmbed A MessageEmbed object
     */
    public void replyEmbed(MessageReceivedEvent event, MessageEmbed messageEmbed) {
        event.getMessage().replyEmbeds(messageEmbed).queue();
    }

    /**
     * Replies a MessageEmbed with a callback.
     *
     * @param event The event that called the command.
     * @param messageEmbed A MessageEmbed object
     * @param callbackCommand The command to callback to.
     */
    public void replyEmbedWithCallback(MessageReceivedEvent event, MessageEmbed messageEmbed, CallbackCommand callbackCommand) {
        event.getMessage().replyEmbeds(messageEmbed).queue(message -> callbackCommand.messageCallback(message, event));
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
