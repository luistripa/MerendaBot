package com.merendabot;

import com.merendabot.commands.Command;
import com.merendabot.commands.CommandHandler;
import com.merendabot.commands.exceptions.CommandDoesNotExistException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.hibernate.SessionFactory;

import java.util.*;

public class Merenda {

    private static Merenda instance = null;
    private JDA jda;
    private SessionFactory factory;
    private CommandHandler commandHandler;
    private Map<String, GuildManager> guilds;

    private Merenda() {}

    public static Merenda getInstance() {
        if (instance == null)
            instance = new Merenda();
        return instance;
    }

    public void setup(JDA jda, SessionFactory factory) {
        this.jda = jda;
        this.factory = factory;

        new Thread(() -> {
            try {
                this.jda.awaitReady();

                commandHandler = new CommandHandler();
                guilds = new HashMap<>();

                loadGuilds();

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                Main.logger.severe("Error seting up Merenda. System won't be able to process commands or timers.");
            }
        }).start();
    }

    public JDA getJda() {
        return jda;
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public GuildManager getGuild(String guildId) {
        return guilds.get(guildId);
    }

    public Command getCommand(String commandId) throws CommandDoesNotExistException {
        return commandHandler.getCommand(commandId);
    }


    /*
    PRIVATE METHODS ------------------------------
     */

    /**
     * Loads Guilds from both database and JDA.
     *
     * Guilds that are not in the database will be added to it.
     *
     * Guilds won't be overritten.
     */
    private void loadGuilds() {
        List<GuildManager> guildManagerList;
        try {
            // From database
            guildManagerList = GuildManager.getGuildManagers();
            for (GuildManager guild : guildManagerList) {
                guilds.put(guild.getGuildId(), guild);
                guild.loadGuild(); // Loads pollHandler and timerHandler
            }

            // From JDA
            List<Guild> guildList = jda.getGuilds();
            for (Guild guild : guildList) {
                commandHandler.registerCommandsForGuild(guild);
                if (!guilds.containsKey(guild.getId())) {
                    GuildManager manager = new GuildManager(guild, guild.getDefaultChannel().getId());
                    guilds.put(guild.getId(), manager);
                    manager.insert();
                    manager.loadGuild();
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
