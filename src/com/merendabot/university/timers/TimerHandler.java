package com.merendabot.university.timers;


import com.merendabot.university.Merenda;

import java.util.*;

public class TimerHandler {

    public static final String WEEKLY_REPORT_TIMER = "weekly-report";
    public static final String CLASSES_TIMER = "classes";
    public static final String TESTS_REMINDER_TIMER = "test-reminder";

    private final Map<String, ScheduleTimer> timerMap;
    private final Timer timerScheduler = new Timer();

    public TimerHandler() {
        timerMap = new HashMap<>();
        registerTimers();
    }

    public ScheduleTimer getTimer(String timerId) {
        return timerMap.get(timerId);
    }

    public Collection<ScheduleTimer> getTimers() {
        return timerMap.values();
    }

    public void addTimer(String timerId, ScheduleTimer timer) {
        timerMap.put(timerId, timer);
    }

    public void removeTimer(String timerId) {
        timerMap.remove(timerId).cancel();
    }

    public void startTimer(String timerId, long delay, long period) {
        ScheduleTimer t = timerMap.get(timerId);
        timerScheduler.schedule((TimerTask) t, delay, period);
    }

    public boolean stopTimer(String timerId) {
        return timerMap.get(timerId).cancel();
    }


    /*
    PRIVATE METHODS
     */

    private void registerTimers() {
        addTimer(TimerHandler.CLASSES_TIMER, new ClassesTimerTask());
        addTimer(TimerHandler.WEEKLY_REPORT_TIMER, new WeeklyReportTimerTask());
        addTimer(TimerHandler.TESTS_REMINDER_TIMER, new TestsReminderTimerTask());

        startTimer(TimerHandler.CLASSES_TIMER, 0, 1000);
        startTimer(TimerHandler.WEEKLY_REPORT_TIMER, 0, 300000); // 5 minutes
        startTimer(TimerHandler.TESTS_REMINDER_TIMER, 0, 1000);
    }
}
