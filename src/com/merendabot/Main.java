package com.merendabot;

import com.merendabot.commands.Command;
import com.merendabot.commands.CommandHandler;
import com.merendabot.commands.exceptions.CommandDoesNotExistException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import com.merendabot.timers.EventTimer;

import javax.security.auth.login.LoginException;
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
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        Merenda merenda = Merenda.getInstance();
        GuildManager guild = merenda.getGuild(event.getGuild().getId());

        try {
            Command command = merenda.getCommand(event.getCommandPath());
            command.execute(guild, event);

        } catch (CommandDoesNotExistException e) {
            event.reply(
                    "Oops! Não encontrei esse comando. Tenta **merenda!help** para um lista de comandos"
            ).setEphemeral(true).queue();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("JDA Ready");
        Merenda.getInstance().setup(event.getJDA());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {}

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
        GuildManager guild = merenda.getGuild(event.getGuild().getId());
        SelectionMenu selectionMenu = event.getSelectionMenu();

        if (selectionMenu == null || selectionMenu.getId() == null) {
            logger.warning("Selection Menu is null or does not have an ID.");
            return;
        }

        String[] selectionMenuId = selectionMenu.getId().split(" ");
        switch (selectionMenuId[0]) {
            case "command": {
                try {
                    Command command = merenda.getCommand(selectionMenuId[1]);
                    command.processSelectionMenu(guild, event);

                } catch (CommandDoesNotExistException e) {
                    event.getChannel().sendMessage("Comando não encontrado.").queue();
                }
                break;
            }
            case "timer": {
                event.reply("Isto ainda não foi implementado.").setEphemeral(true).queue();
                break;
            }
            default: {
                String logMessage = String.format("Could not find selection menu handler type: %s", selectionMenuId[0]);
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
        GuildManager guild = merenda.getGuild(event.getGuild().getId());
        Button button = event.getButton();

        if (button == null || button.getId() == null) {
            logger.warning("Button is null or its id is null.");
            return;
        }

        String[] buttonId = button.getId().split(" ");

        switch (buttonId[0]) { // Section 1 of button id
            case "command": {
                try {
                    Command command = merenda.getCommand(buttonId[1]);
                    command.processButtonClick(guild, event);

                } catch (CommandDoesNotExistException e) {
                    event.reply("Uhmm... não encontrei essa ação. Se isto for um erro contacta o administrador.")
                            .setEphemeral(true).queue();
                }
                break;
            }
            case "timer": {
                EventTimer timer = guild.getTimerHandler().getTimer(buttonId[1]);
                timer.processButtonClick(event);
                break;
            }
            default: {
                String logMessage = String.format("Could not find button handler type: %s", buttonId[0]);
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
}