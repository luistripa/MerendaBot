package com.merendabot.university.timers;

import com.merendabot.university.MessageDispatcher;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Test;
import com.merendabot.university.subjects.Subject;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class TestsReminderTimerTask extends AbstractTimerTask {

    private static final int TEST_REMINDER_DAYS_BEFORE = 5;

    private static final Logger logger = Logger.getLogger("main-log");

    private Queue<Test> testCache;
    private LocalDateTime nextCacheReload;

    public TestsReminderTimerTask() {
        testCache = loadCache();
    }

    @Override
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(nextCacheReload))
            testCache = loadCache();

        Test test = testCache.peek();
        if (test == null)
            return;

        // Test date already passed
        if (now.toLocalDate().isAfter(test.getStartDate())) {
            testCache.remove();
            return;
        }

        LocalDate date = test.getStartDate().minusDays(TEST_REMINDER_DAYS_BEFORE);

        if (now.toLocalDate().isEqual(date)) // Test is TEST_REMINDER_DAYS_BEFORE days from now
            notifyTest(testCache.remove());

        else if (date.isBefore(now.toLocalDate())) // Test reminding date has already passed
            testCache.remove();
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        switch (event.getButton().getId().split(" ")[2]) {
            case "panic": {
                event.reply(":eyes:").queue();
                break;
            }
            default:
                event.reply(":confused:").queue();
        }
    }

    /*
    Private Methods
     */

    private Queue<Test> loadCache() {
        Queue<Test> newTestCache = new ConcurrentLinkedQueue<>();
        LocalDateTime now = LocalDateTime.now();
        try {
            newTestCache.addAll(Test.getTests());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        nextCacheReload = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        return newTestCache;
    }


    private void notifyTest(Test test) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Hellooo!! Um teste está para ser realizado em breve!");

        try {
            String fieldTitle = String.format(
                    "%s %s",
                    test.getName(),
                    Subject.getSubjectById(test.getSubjectId()).getShortName());
            String fieldValue = String.format(
                    "%s %s-%s",
                    test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                    test.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    test.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
            );
            eb.addField(fieldTitle, fieldValue, false);

            MessageDispatcher.getInstance().sendMessage(
                    eb.build(), Button.danger("timer test-reminder panic", "Panic!")
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
