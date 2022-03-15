package com.merendabot;

import com.merendabot.polls.PollHandler;
import net.dv8tion.jda.api.entities.*;
import com.merendabot.timers.TimerHandler;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

@Entity
@Table(name = "guilds")
public class GuildManager {

    public static final Logger logger = Logger.getLogger("main-log");

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "default_channel_id", nullable = false)
    private String defaultChannelId;

    @Transient
    private Guild guild;
    @Transient
    private TextChannel defaultChannel;
    @Transient
    public PollHandler pollHandler;
    @Transient
    private TimerHandler timerHandler;

    public GuildManager(Guild guild, String defaultChannelId) {
        this.guildId = guild.getId();
        this.defaultChannelId = defaultChannelId;
    }

    protected GuildManager() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getDefaultChannelId() {
        return defaultChannelId;
    }

    public void setDefaultChannelId(String defaultChannelId) {
        this.defaultChannelId = defaultChannelId;
    }

    public Guild getGuild() {
        if (guild == null) {
            guild = Merenda.getInstance().getJda().getGuildById(guildId);
        }
        return guild;
    }

    public TextChannel getDefaultChannel() {
        if (defaultChannel == null) {
            defaultChannel = this.getGuild().getTextChannelById(defaultChannelId);
        }
        return defaultChannel;
    }

    public PollHandler getPollHandler() {
        return pollHandler;
    }

    public TimerHandler getTimerHandler() {
        return timerHandler;
    }

    public Task<List<Member>> getAllMembers() {
        return guild.loadMembers();
    }

    public Task<List<Member>> getNonBotMembers() {
        return guild.findMembers(Predicate.not(member -> member.getUser().isBot()));
    }

    public MessageAction generateMessage(String message) {
        return getDefaultChannel().sendMessage(message);
    }

    public MessageAction generateMessageEmbed(MessageEmbed embed) {
        return getDefaultChannel().sendMessageEmbeds(embed);
    }

    /**
     * Loads the guild timers and polls.
     * This is to avoid starting the timers when creating the guild manager object.
     */
    public void loadGuild() {
        pollHandler = new PollHandler(this);
        timerHandler = new TimerHandler(this);
    }

    @Override
    public String toString() {
        return "GuildManager{" +
                "guild=" + guildId +
                ", defaultChannel=" + defaultChannelId +
                '}';
    }

    /**
     * Persist a new GuildManager in the database.
     *
     * This method must not be called on an already persisted entity.
     */
    public void insert() {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.persist(this);
            tx.commit();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            throwable.printStackTrace();

        } finally {
            session.close();
        }

        session.close();
    }

    /**
     * Updates an already existing GuildManager in the database.
     */
    public void update() {
        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            session.update(this);
            tx.commit();

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();
            throwable.printStackTrace();

        } finally {
            session.close();
        }
    }

    /**
     * Gets all GuildManager objects present in the database.
     *
     * @return A List of GuildManagers
     */
    public static List<GuildManager> getGuildManagers() {
        List guildManagers;

        Session session = Merenda.getInstance().getFactory().openSession();
        Transaction tx = session.beginTransaction();

        try {
            guildManagers = session.createQuery("from GuildManager").list();
            tx.commit();
            return guildManagers;

        } catch (Throwable throwable) {
            if (tx != null)
                tx.rollback();

        } finally {
            session.close();
        }
        return new ArrayList<>(0);
    }
}
