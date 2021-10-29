package com.merendabot.university.timers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Event;
import com.merendabot.university.events.EventClass;
import com.merendabot.university.events.EventType;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents the alert for classes.
 *
 * This sends a message to a specific channel warning a class is about to start.
 * It also includes the zoom link for that class.
 */
public class ClassesTimerTask extends AbstractTimerTask {


    private Queue<Event> eventCache;
    private LocalDateTime nextCacheLoad;

    public ClassesTimerTask(JDA jda, Merenda merenda) {
        super(jda, merenda);
        eventCache = new ConcurrentLinkedQueue<>();
        loadCache();
    }

    @Override
    public void run() {
        Guild guild = this.getJDA().getGuildById(GUILD_ID);
        if (guild == null) {
            System.out.println("Error: Could not find guild with id "+GUILD_ID);
            this.cancel();
            return;
        }
        TextChannel channel = guild.getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            System.out.println("Error: Could not find channel with id "+CHANNEL_ID);
            this.cancel();
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Check if should reload the cache
        if (now.isAfter(nextCacheLoad))
            loadCache();

        Event event = eventCache.peek();
        if (event == null)
            return;

        if (now.toLocalTime().isAfter(event.getEndTime())) {
            eventCache.remove();

        } else {
            if (now.toLocalTime().isAfter(event.getStartTime())) {
                notifyEvent(channel, eventCache.remove());
            }
        }
    }


    /*
    Private Methods
     */

    /**
     * Loads database data into cache.
     */
    private void loadCache() {
        LocalDateTime now = LocalDateTime.now();
        try {
            ResultSet rs = EventClass.getEventsByWeekday(now.getDayOfWeek(), EventType.CLASS);
            while (rs.next()) {
                Event event = EventClass.getEventFromRS(rs);
                Subject subject = Subject.getSubjectFromRS(rs, 11);
                event.setSubject(subject);
                eventCache.add(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        nextCacheLoad = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Sends the event message to the specified channel.
     *
     * @param channel The channel to send the message to
     * @param event The event that is happening now
     */
    private void notifyEvent(TextChannel channel, Event event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Heyy!! Uma aula está prestes a começar!");

        String embedFieldTitle = String.format(
                "Aula %s",
                event.getSubject().getShortName()
        );
        String embedFieldValue = String.format(
                "%s - %s",
                event.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                event.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        eb.addField(
                embedFieldTitle,
                embedFieldValue,
                false
        );
        channel.sendMessageEmbeds(eb.build()).setActionRow(
                Button.link(event.getLink(), "Zoom")
        ).queue();
    }
}
