package com.merendabot.university.timers;

import com.merendabot.university.MessageDispatcher;
import com.merendabot.university.events.Class;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Represents the alert for classes.
 *
 * This sends a message to a specific channel warning a class is about to start.
 * It also includes the zoom link for that class.
 */
public class ClassesTimerTask extends AbstractTimerTask {

    private static final Logger logger = Logger.getLogger("main-log");


    private final Queue<Class> eventCache;
    private LocalDateTime nextCacheLoad;

    public ClassesTimerTask() {
        eventCache = new ConcurrentLinkedQueue<>();
        loadCache();
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        // Check if it should reload the cache
        if (now.isAfter(nextCacheLoad))
            loadCache();

        Class event = eventCache.peek();
        if (event == null) // There are no events
            return;

        if (now.toLocalTime().isAfter(event.getEndTime())) { // Event has passed
            eventCache.remove();

        } else {
            if (now.toLocalTime().isAfter(event.getStartTime())) { // Start of event has passed but has not ended
                notifyEvent(eventCache.remove(), event.getSubject());
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
            eventCache.addAll(Class.getClassesByWeekDay(now.getDayOfWeek()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        nextCacheLoad = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Sends the event message to the default channel.
     *
     * @param event The event that is happening now
     */
    private void notifyEvent(Class event, Subject subject) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Heyy!! Uma aula está prestes a começar!");

        String embedFieldTitle = String.format(
                "Aula %s",
                subject.getShortName()
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

        // Send the message
        MessageDispatcher.getInstance().getDefaultChannel()
                .sendMessageEmbeds(eb.build())
                .setActionRow(Button.link(event.getLink(), "Zoom"))
                .queue();
    }
}
