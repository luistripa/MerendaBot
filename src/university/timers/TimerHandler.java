package university.timers;

import java.util.*;

public class TimerHandler {

    public static final String WEEKLY_REPORT_TIMER = "weekly-report";
    public static final String CLASSES_TIMER = "classes";
    public static final String POLL_REFRESH_TIMER = "polls";

    private Map<String, ScheduleTimer> timerMap;

    private final Timer timerScheduler = new Timer();

    public TimerHandler() {
        timerMap = new HashMap<>();
    }

    public ScheduleTimer getTimer(String timer_id) {
        return timerMap.get(timer_id);
    }

    public Collection<ScheduleTimer> getTimers() {
        return timerMap.values();
    }

    public void addTimer(String timer_id, ScheduleTimer timer) {
        timerMap.put(timer_id, timer);
    }

    public void removeTimer(String timer_id) {
        timerMap.remove(timer_id).cancel();
    }

    public void startTimer(String timer_id, long delay, long period) {
        ScheduleTimer t = timerMap.get(timer_id);
        timerScheduler.schedule((TimerTask) t, delay, period);
    }

    public boolean stopTimer(String timer_id) {
        return timerMap.get(timer_id).cancel();
    }
}
