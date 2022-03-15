package com.merendabot.timers;

import com.merendabot.GuildManager;
import com.merendabot.Main;
import com.merendabot.timers.exceptions.TimerDoesNotExistException;

import java.util.*;
import java.util.logging.Logger;

public class TimerHandler {

    public static final String WEEKLY_REPORT_TIMER = "weekly-report";
    public static final String CLASSES_TIMER = "classes";
    public static final String TESTS_REMINDER_TIMER = "test-reminder";

    private static final Logger logger = Logger.getLogger("main-log");

    private final GuildManager guild;
    private final Timer timerScheduler;
    private final Map<String, EventTimer> timers;

    public TimerHandler(GuildManager guild) {
        this.guild = guild;
        timerScheduler = new Timer();
        timers = new HashMap<>();

        try {
            registerTimers();
        } catch (TimerDoesNotExistException e) {
            logger.warning("Attempted to start a timer that does not exist.");
            e.printStackTrace();
        }
    }

    public EventTimer getTimer(String timerId) {
        return timers.get(timerId);
    }

    public List<EventTimer> getTimers() {
        return new ArrayList<>(timers.values());
    }

    public void startTimer(String timerId) throws TimerDoesNotExistException {
        if (!hasTimer(timerId))
            throw new TimerDoesNotExistException(timerId);
        timers.get(timerId).start();
    }

    public void stopTimer(String timerId) throws TimerDoesNotExistException {
        if (!hasTimer(timerId))
            throw new TimerDoesNotExistException(timerId);
        timers.get(timerId).stop();
    }

    public boolean hasTimer(String timerId) {
        return timers.get(timerId) != null;
    }


    /*
    PRIVATE METHODS
     */

    private void addTimer(String timerId, EventTimer timer) {
        timers.put(timerId, timer);
    }

    private void registerTimers() throws TimerDoesNotExistException {
        addTimer(CLASSES_TIMER, new ClassesEventTimer(guild, timerScheduler, 0, 1000));
        addTimer(TESTS_REMINDER_TIMER, new TestsReminderEventTimer(guild, timerScheduler, 0, 300000));
        addTimer(WEEKLY_REPORT_TIMER, new WeeklyReportEventTimer(guild, timerScheduler, 0, 1000));

        startTimer(CLASSES_TIMER);
        startTimer(TESTS_REMINDER_TIMER);
        startTimer(WEEKLY_REPORT_TIMER);
    }
}
