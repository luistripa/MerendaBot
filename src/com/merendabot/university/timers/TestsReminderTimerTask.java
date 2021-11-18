package com.merendabot.university.timers;

import com.merendabot.university.MessageDispatcher;
import com.merendabot.university.events.Test;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Represents the test reminder.
 *
 * This will alert for tests that are to be made in a predefined number of days.
 */
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
            event.reply("Não encontrei esse botão... Contacta um administrador.").queue();
            return;
        }

        if (button.getId() == null) {
            logger.severe("Button does not have an id.");
            event.reply("Não encontrei esse botão... Contacta um administrador.").queue();
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
                event.reply("A ação que esse botão pediu não é válida. Contacta um administrador.").queue();
            }
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


    private void notifyTest(Test test, Subject subject) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Hellooo!! Um teste está para ser realizado em breve!");

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

        MessageDispatcher.getInstance().getDefaultChannel()
                .sendMessageEmbeds(eb.build())
                .setActionRow(Button.danger("timer test-reminder panic", "Panic!"))
                .queue();
    }
}
