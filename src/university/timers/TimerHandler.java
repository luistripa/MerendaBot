package university.timers;

import java.util.*;

public class TimerHandler {

    public static final String WEEKLY_REPORT_TIMER = "weekly-report";
    public static final String CLASSES_TIMER = "classes";
    public static final String POLL_REFRESH_TIMER = "polls";

    private final Map<String, ScheduleTimer> timerMap;

    private final Timer timerScheduler = new Timer();

    public TimerHandler() {
        timerMap = new HashMap<>();
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
}
