package com.merendabot;

import com.merendabot.commands.*;
import com.merendabot.university.MessageDispatcher;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import com.merendabot.university.Merenda;
import com.merendabot.university.timers.*;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends ListenerAdapter {

    public static final Logger logger = Logger.getLogger("main-log");

    public static void main(String[] args) {
        logger.setLevel(Level.ALL);
        if (!verifyEnvironmentVariables()) {
            System.exit(1);
        }

        JDA jda;
        try {
            jda = JDABuilder.createDefault(System.getenv("TOKEN"))
                    .setActivity(Activity.watching(CommandHandler.COMMAND_PREFIX))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.addEventListener(new Main());

        } catch (LoginException e) {
            e.printStackTrace();
        }
        //You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("JDA Ready");
        Merenda.getInstance();
        Merenda.setJDA(event.getJDA());
        MessageDispatcher.getInstance();

        // Setup Merenda. This must be done on a different thread, so we can call jda.awaitReady()
        new Thread(() -> {
            try {
                Merenda.getJDA().awaitReady();
                Merenda.getInstance().setup();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();

    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        Message message = event.getMessage();
        if (!isCommand(message))
            return;

        Merenda merenda = Merenda.getInstance();

        String[] splitCommand = splitCommand(message.getContentDisplay());

        if (merenda.hasCommand(splitCommand[0])) {
            Command command = merenda.getCommand(splitCommand[0]);
            if (command instanceof CallbackCommand)
                command.execute(merenda, splitCommand, event)
                        .queue(m -> {
                            CallbackCommand c = (CallbackCommand) command;
                            c.messageCallback(m, event);
                        });
            else
                command.execute(merenda, splitCommand, event).queue();

        } else {
            event.getChannel().sendMessage(
                    "Oops! Não encontrei esse comando. Tenta **merenda!help** para um lista de comandos"
            ).queue();
        }
    }

    /**
     * Fired when a selection is made in a selection menu.
     *
     * @param event The event that triggered the method
     */
    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        /*
         * selection menu id's should have the following format:
         * [section 1] [section 2] [section3]
         *
         * section 1 - Identifies the handler type. (Command, Timer, Etc.)
         *
         * section 2 - Handler identifier (Identifier of a command or timer)
         *
         * section 3 - Instruction (Handler specific. Some handlers may completely ignore this) (may have internal sections)
         */
        Merenda merenda = Merenda.getInstance();
        String[] selectionMenu = event.getSelectionMenu().getId().split(" ");
        switch (selectionMenu[0]) {
            case "command": {
                Command command = merenda.getCommand(selectionMenu[1]);
                command.processSelectionMenu(merenda, event);
                break;
            }
            case "timer": {
                event.reply("Isto ainda não foi implementado.").setEphemeral(true).queue();
                break;
            }
            default: {
                String logMessage = String.format("Could not find selection menu handler type: %s", selectionMenu[0]);
                logger.severe(logMessage);
                event.reply("Não consegui entender essa ação. Contacta um administrador.").queue();
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        /*
         * Button id's should have the following format:
         * [section 1] [section 2] [section3]
         *
         * section 1 - Identifies the handler type. (Command, Timer, Etc.)
         *
         * section 2 - Handler identifier (Identifier of a command or timer)
         *
         * section 3 - Instruction (Handler specific. Some handlers may completely ignore this)
         */

        Merenda merenda = Merenda.getInstance();

        String[] button = event.getButton().getId().split(" ");

        switch (button[0]) { // Section 1 of button id
            case "command": {
                if (merenda.hasCommand(button[1])) {
                    Command command  = merenda.getCommand(button[1]); // Command id is in Section 2
                    command.processButtonPressed(merenda, event).setEphemeral(true).queue();
                } else {
                    event.reply("Uhmm... não encontrei essa ação. Se isto for um erro contacta o administrador.")
                            .setEphemeral(true).queue();
                }
                break;
            }
            case "timer": {
                ScheduleTimer timer = merenda.getTimer(button[1]);
                timer.processButtonClick(event);
                break;
            }
            default: {
                String logMessage = String.format("Could not find button handler type: %s", button[0]);
                logger.severe(logMessage);
                event.reply("Não consegui entender essa ação. Contacta um administrador.").queue();
            }
        }
    }

    /**
     * Verifies if all environment variables are setup correctly.
     *
     * @return True if setup is correct, False otherwise.
     */
    private static boolean verifyEnvironmentVariables() {
        // Check if DEBUG environment variable is not set
        if (System.getenv("DEBUG") == null)
            logger.warning(
                    "DEBUG environment variable is not set." +
                            "If you do not want DEBUG mode enabled you should set it to FALSE. " +
                            "Will assume FALSE.");
        else if (System.getenv("DEBUG").equals("1")) {
            logger.warning("DEBUG mode is set.");
        }

        // Check if TOKEN environment variable is not set
        if (System.getenv("TOKEN") == null) {
            logger.severe("TOKEN environment variable is not set.");
            return false;
        }

        // Check for DATABASE_USER
        if (System.getenv("DATABASE_USER") == null) {
            logger.severe("DATABASE_USER environment variable is not set.");
            return false;
        }

        // Check for DATABASE_PASSWORD
        if (System.getenv("DATABASE_PASSWORD") == null) {
            logger.severe("DATABASE_PASSWORD environment variable is not set.");
            return false;
        }

        // Check for DATABASE_NAME
        if (System.getenv("DATABASE_NAME") == null) {
            logger.severe("DATABASE_NAME environment variable is not set.");
            return false;
        }

        return true;
    }

    /**
     * Checks if given message is a command, AKA starts with COMMAND_PREFIX.
     *
     * @param message The message object that was received.
     * @return True if is command, False otherwise
     */
    private boolean isCommand(Message message) {
        return message.getContentDisplay().startsWith(CommandHandler.COMMAND_PREFIX);
    }

    /**
     * Splits the command into parts.
     *
     * 0 - The command name
     * 1 ... * - The command arguments
     *
     * @param content The content of the command.
     * @return A split command
     */
    private String[] splitCommand(String content) {
        String commandMessage = content.split(CommandHandler.COMMAND_PREFIX)[1];
        return commandMessage.split(" ");
    }
}