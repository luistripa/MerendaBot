package com.merendabot.university.timers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import com.merendabot.university.Merenda;

import java.util.TimerTask;

public abstract class AbstractTimerTask extends TimerTask implements ScheduleTimer {

    public static final String GUILD_ID = "687683461523439644";
    public static final String CHANNEL_ID = "897851097518080021";

    public static final String DEV_GUILD = "797614596985716827";
    public static final String DEV_GUILD_CHANNEL = "897477068831469638";

    private final JDA jda;
    private final Merenda merenda;

    public AbstractTimerTask(JDA jda, Merenda merenda) {
        this.jda = jda;
        this.merenda = merenda;
    }

    protected JDA getJDA() {
        return this.jda;
    }

    protected Merenda getMerenda() {
        return this.merenda;
    }

    public void processButtonClick(ButtonClickEvent event) {
        event.reply("Error Timer Task: Received a button click but method is not overriden.").queue();
    }
}
