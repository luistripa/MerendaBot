package rebuild.merenda;

import com.merendabot.Main;
import com.merendabot.commands.CommandHandler;
import com.merendabot.university.polls.PollHandler;
import com.merendabot.university.timers.TimerHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Merenda {

    private static final String URL = "jdbc:postgresql://localhost/merendabot";

    private static Merenda instance = null;
    private JDA jda;
    private Connection connection;
    private Map<String, GuildManager> guilds;

    private Merenda() {

    }

    public static Merenda getInstance() {
        return instance;
    }

    public void setup(JDA jda) {
        this.jda = jda;

        new Thread(() -> {
            try {
                com.merendabot.university.Merenda.getJDA().awaitReady();
                Properties properties = new Properties();
                properties.setProperty("user", System.getenv("DATABASE_USER"));
                properties.setProperty("password", System.getenv("DATABASE_PASSWORD"));

                connection = DriverManager.getConnection(URL, properties);
                loadGuilds();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                Main.logger.severe("Error seting up merenda. System won't be able to process commands or timers.");
            }
        });
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

    public boolean hasGuild(String guildId) {
        return getGuild(guildId) != null;
    }


    /*
    PRIVATE METHODS ------------------------------
     */

    private void loadGuilds() {
        // TODO: Get guilds from database
        String defaultChannelId = null;

        List<Guild> guildList = this.jda.getGuilds();
        for (Guild guild : guildList) {
            guilds.put(guild.getId(), new GuildManager(guild, defaultChannelId));
        }
    }
}
