package com.merendabot.timers;

import com.merendabot.GuildManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

import java.util.Timer;
import java.util.TimerTask;

public abstract class EventTimer extends TimerTask {

    private final GuildManager guild;
    private final Timer scheduler;
    private long delay; // Timer will wait this time before executing the first cycle
    private long period; // Timer will execute in this interval
    private boolean isActive;

    public EventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        this.guild = guild;
        this.scheduler = scheduler;
        this.delay = delay;
        this.period = period;
    }

    /**
     * Gets the guild manager of the timer.
     *
     * @return A GuildManager object
     */
    public GuildManager getGuild() {
        return guild;
    }

    /**
     * Returns the scheduler of the timer.
     *
     * @return A Timer object
     */
    public Timer getScheduler() {
        return scheduler;
    }

    /**
     * Gets the delay of the timer. The timer will be executed X milliseconds after being scheduled.
     *
     * @return Int representing the timer delay.
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Gets the period of the timer.
     *
     * @return Int representing the period of the timer.
     */
    public long getPeriod() {
        return period;
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
