package com.merendabot.university.timers;

import com.merendabot.university.Merenda;
import com.merendabot.university.MessageDispatcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.university.events.Event;
import com.merendabot.university.events.EventClass;
import com.merendabot.university.events.EventType;
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


    private Queue<Event> eventCache;
    private LocalDateTime nextCacheLoad;

    public ClassesTimerTask() {
        eventCache = new ConcurrentLinkedQueue<>();
        loadCache();
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        // Check if should reload the cache
        if (now.isAfter(nextCacheLoad))
            loadCache();

        Event event = eventCache.peek();
        if (event == null) // There are no events
            return;

        if (now.toLocalTime().isAfter(event.getEndTime())) { // Event has passed
            eventCache.remove();

        } else {
            if (now.toLocalTime().isAfter(event.getStartTime())) { // Start of event has passed but has not ended
                notifyEvent(eventCache.remove());
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
            for (Event event : EventClass.getEventsByWeekday(now.getDayOfWeek(), EventType.CLASS)) {
                Subject subject = Subject.getSubjectById(event.getSubjectId());
                event.setSubject(subject);
                eventCache.add(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        nextCacheLoad = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Sends the event message to the default channel.
     *
     * @param event The event that is happening now
     */
    private void notifyEvent(Event event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Heyy!! Uma aula está prestes a começar!");

        try {
            String embedFieldTitle = String.format(
                    "Aula %s",
                    Subject.getSubjectById(event.getSubjectId()).getShortName()
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
            MessageDispatcher.getInstance().sendMessage(
                    eb.build(),
                    Button.link(event.getLink(), "Zoom")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
