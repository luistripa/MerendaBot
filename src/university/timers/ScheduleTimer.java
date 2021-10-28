package university.timers;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

public interface ScheduleTimer {

    void run();

    boolean cancel();

    void processButtonClick(ButtonClickEvent event);
}
