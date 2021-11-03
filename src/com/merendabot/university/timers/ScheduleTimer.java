package com.merendabot.university.timers;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ScheduleTimer {

    /**
     * Runs one cycle of the timer.
     * This method will be called multiple times until the system is shutdown, o the timer is canceled.
     */
    void run();

    /**
     * Cancels a timer. After calling this, the run() method will no longer be called.
     *
     * @return true if timer was cancelled, false otherwise.
     */
    boolean cancel();

    /**
     * Processes a ButtonClickEvent for timers.
     *
     * @param event The event that called the timer
     */
    void processButtonClick(ButtonClickEvent event);
}
