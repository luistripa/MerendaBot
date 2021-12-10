package rebuild.merenda.timers;

import com.merendabot.university.MessageDispatcher;
import com.merendabot.university.events.Class;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import rebuild.merenda.GuildManager;

import java.awt.*;
import java.sql.SQLException;
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

        if (now.toLocalTime().isAfter(event.getEndTime())) { // Event has passed
            eventCache.remove();

        } else {
            if (now.toLocalTime().isAfter(event.getStartTime())) { // Start of event has passed but has not ended
                notifyEvent(eventCache.remove(), event.getSubject());
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
