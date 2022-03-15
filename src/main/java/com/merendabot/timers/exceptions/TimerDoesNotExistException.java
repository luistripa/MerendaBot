package main.java.com.merendabot.timers.exceptions;

public class TimerDoesNotExistException extends Exception {

    public TimerDoesNotExistException(String timerId) {
        super("Timer does not exist: "+timerId);
    }
}
