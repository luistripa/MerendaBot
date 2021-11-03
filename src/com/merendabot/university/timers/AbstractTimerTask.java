package com.merendabot.university.timers;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.TimerTask;

public abstract class AbstractTimerTask extends TimerTask implements ScheduleTimer {

    public void processButtonClick(ButtonClickEvent event) {
        event.reply("Error Timer Task: Received a button click but method is not overriden.").queue();
    }
}
