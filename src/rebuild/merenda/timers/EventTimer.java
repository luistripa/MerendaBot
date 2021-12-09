package rebuild.merenda.timers;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import rebuild.merenda.GuildManager;

import java.util.Timer;
import java.util.TimerTask;

public abstract class EventTimer extends TimerTask {

    private final GuildManager guild;
    private final Timer scheduler;
    private long delay; // Timer will wait this time before executing the first time
    private long period; // Timer will execute in this interval
    private boolean isActive;

    public EventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        this.guild = guild;
        this.scheduler = scheduler;
        this.delay = delay;
        this.period = period;
    }

    /**
     * Starts the timer.
     * It will become active and {@link #run()} will start being executed.
     */
    public void start() {
        this.scheduler.schedule(this, delay, period);
        isActive = true;
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        this.cancel();
        isActive = false;
    }

    /**
     * Checks if the timer is active.
     *
     * @return True if timer is active, False otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Restarts the timer.
     * TODO: Test restart method
     */
    public void restart() {
        this.stop();
        this.start();
    }

    /**
     * Runs an iteration of the event timer.
     */
    public abstract void run();

    /**
     * Processes a click from a button handled by this timer.
     *
     * @param event The event object
     */
    public abstract void processButtonClick(ButtonClickEvent event);

    /**
     * Processes a selection from a selection menu handled by a timer.
     *
     * @param event The event object
     */
    public abstract void processSelectionMenu(SelectionMenuEvent event);
}
