package rebuild.merenda.timers;

import rebuild.merenda.GuildManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class TimerHandler {

    public static final String WEEKLY_REPORT_TIMER = "weekly-report";
    public static final String CLASSES_TIMER = "classes";
    public static final String TESTS_REMINDER_TIMER = "test-reminder";

    private GuildManager guild;
    private Timer timerScheduler;
    private Map<String, EventTimer> timers;

    public TimerHandler(GuildManager guild) {
        this.guild = guild;
        timerScheduler = new Timer();
        timers = new HashMap<>();

        registerTimers();
    }

    public EventTimer getTimer(String timerId) {
        return timers.get(timerId);
    }


    /*
    PRIVATE METHODS
     */

    private void addTimer(String timerId, EventTimer timer) {
        timers.put(timerId, timer);
    }

    private void registerTimers() {

    }
}
