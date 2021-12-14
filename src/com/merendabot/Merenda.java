package com.merendabot;

import com.merendabot.commands.Command;
import com.merendabot.commands.CommandHandler;
import com.merendabot.commands.exceptions.CommandDoesNotExistException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Merenda {

    private static final String URL = "jdbc:postgresql://localhost/";

    private static Merenda instance = null;
    private JDA jda;
    private Connection connection;
    private CommandHandler commandHandler;
    private Map<String, GuildManager> guilds;

    private Merenda() {}

    public static Merenda getInstance() {
        if (instance == null)
            instance = new Merenda();
        return instance;
    }

    public void setup(JDA jda) {
        this.jda = jda;

        new Thread(() -> {
            try {
                this.jda.awaitReady();
                Properties properties = new Properties();
                properties.setProperty("user", System.getenv("DATABASE_USER"));
                properties.setProperty("password", System.getenv("DATABASE_PASSWORD"));

                connection = DriverManager.getConnection(URL+System.getenv("DATABASE_NAME"), properties);
                commandHandler = new CommandHandler();
                guilds = new HashMap<>();

                loadGuilds();

            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                Main.logger.severe("Error seting up merenda. System won't be able to process commands or timers.");
            }
        }).start();
    }

    public JDA getJda() {
        return jda;
    }

    public Connection getConnection() {
        return connection;
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

    private void loadGuilds() {
        // TODO: Get guilds from database
        String defaultChannelId = null;

        List<Guild> guildList = this.jda.getGuilds();
        for (Guild guild : guildList) {
            guilds.put(guild.getId(), new GuildManager(guild, guild.getDefaultChannel().getId()));
        }
    }
}
