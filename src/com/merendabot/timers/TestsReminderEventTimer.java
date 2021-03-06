package com.merendabot.timers;

import com.merendabot.university.events.Test;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.GuildManager;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.Timer;
import java.util.logging.Logger;

public class TestsReminderEventTimer extends EventTimer {

    private static final int TEST_REMINDER_DAYS_BEFORE = 5;

    private static final Logger logger = Logger.getLogger("main-log");

    private Queue<Test> testCache;
    private LocalDateTime nextCacheReload;

    public TestsReminderEventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        super(guild, scheduler, delay, period);
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(nextCacheReload))
            loadCache();

        Test test = testCache.peek();
        if (test == null) // There are no tests
            return;

        // Test date already passed
        if (now.toLocalDate().isAfter(test.getStartDate())) {
            testCache.remove();
            return;
        }

        LocalDate date = test.getStartDate().minusDays(TEST_REMINDER_DAYS_BEFORE);

        if (now.toLocalDate().isEqual(date)) // Test is TEST_REMINDER_DAYS_BEFORE days from now
            notifyTest(testCache.remove(), test.getSubject());

        else if (date.isBefore(now.toLocalDate())) // Test reminding date has already passed
            testCache.remove();
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        Button button = event.getButton();

        if (button == null) {
            logger.severe("Could not find button.");
            event.reply("N??o encontrei esse bot??o... Contacta um administrador.").setEphemeral(true).queue();
            return;
        }

        if (button.getId() == null) {
            logger.severe("Button does not have an id.");
            event.reply("N??o encontrei esse bot??o... Contacta um administrador.").setEphemeral(true).queue();
            return;
        }
        String action = button.getId().split(" ")[2];
        switch (action) {
            case "panic": {
                event.reply(":eyes:").setEphemeral(true).queue();
                break;
            }
            default: {
                logger.warning("Could not find action with id: "+action);
                event.reply("A a????o que esse bot??o pediu n??o ?? v??lida. Contacta um administrador.").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void processSelectionMenu(SelectionMenuEvent event) {
        event.reply("Essa opera????o n??o ?? suportada. Contacta um administrador.").setEphemeral(true).queue();
    }

    /*
    Private Methods
    */

    private void loadCache() {
        testCache.clear();
        LocalDateTime now = LocalDateTime.now();
        try {
            testCache.addAll(Test.getTests());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        nextCacheReload = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    }


    private void notifyTest(Test test, Subject subject) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Hellooo!! Um teste est?? para ser realizado em breve!");

        String fieldTitle = String.format(
                "%s %s :pencil:",
                test.getName(),
                subject.getShortName());
        String fieldValue = String.format(
                "%s (%s) %s-%s",
                test.getStartDate().format(DateTimeFormatter.ofPattern("dd MMM")),
                test.getStartDate().format(DateTimeFormatter.ofPattern("EEE")),
                test.getStartTime().format(DateTimeFormatter.ofPattern("H:mm")),
                test.getEndTime().format(DateTimeFormatter.ofPattern("H:mm"))
        );
        eb.addField(fieldTitle, fieldValue, false);

        getGuild().generateMessageEmbed(eb.build())
                .setActionRow(Button.danger("timer test-reminder panic", "Panic!"))
                .queue();
    }
}
