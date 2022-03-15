package main.java.com.merendabot.timers;

import main.java.com.merendabot.GuildManager;
import main.java.com.merendabot.Merenda;
import main.java.com.merendabot.university.events.Class;
import main.java.com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClassesEventTimer extends EventTimer {

    private final Queue<Class> eventCache;
    private LocalDateTime nextCacheLoad;

    public ClassesEventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        super(guild, scheduler, delay, period);

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

        if (now.toLocalTime().isAfter(event.getEndTime().toLocalTime())) { // Event has passed
            eventCache.remove();

        } else {
            if (now.toLocalTime().isAfter(event.getTime().toLocalTime())) { // Start of event has passed but has not ended
                notifyEvent();
            }
        }
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        event.reply("Essa operação não é suportada. Contacta um administrador.").setEphemeral(true).queue();
    }

    @Override
    public void processSelectionMenu(SelectionMenuEvent event) {
        event.reply("Essa operação não é suportada. Contacta um administrador.").setEphemeral(true).queue();
    }


    /*
    Private Methods
     */

    /**
     * Loads database data into cache.
     */
    private void loadCache() {
        Session session = null;
        Transaction tx = null;

        LocalDateTime now = LocalDateTime.now();
        try {
            session = Merenda.getInstance().getFactory().openSession();
            tx = session.beginTransaction();

            eventCache.addAll(Class.getClassesByWeekday(session, now.getDayOfWeek()));

            tx.commit();
            session.close();

        } catch (Throwable e) {
            if (tx != null) {
                tx.rollback();
                session.close();
            }
            e.printStackTrace();
            // TODO: Send error message
        }
        nextCacheLoad = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Sends the event message to the default channel.
     *
     */
    private void notifyEvent() {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Class event = eventCache.remove();
            session.update(event);
            Subject subject = event.getSubject();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Heyy!! Uma aula está prestes a começar!");

            String embedFieldTitle = String.format(
                    "Aula %s",
                    subject.getShortName()
            );
            String embedFieldValue = String.format(
                    "%s - %s",
                    event.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    event.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            eb.addField(
                    embedFieldTitle,
                    embedFieldValue,
                    false
            );

            // Send the message
            getGuild().generateMessageEmbed(eb.build())
                    .setActionRow(Button.link(event.getLink(), "Zoom"))
                    .queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();

        } finally {
            session.close();
        }
    }
}
