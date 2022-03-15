package com.merendabot.timers;

import com.merendabot.Merenda;
import com.merendabot.university.events.Test;
import com.merendabot.university.subjects.Subject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.GuildManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class TestsReminderEventTimer extends EventTimer {

    private static final int TEST_REMINDER_DAYS_BEFORE = 5;

    private static final Logger logger = Logger.getLogger("main-log");

    private Queue<Test> testCache;
    private LocalDateTime nextCacheReload;

    public TestsReminderEventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        super(guild, scheduler, delay, period);

        testCache = new ConcurrentLinkedQueue<>();
        loadCache();
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
        if (now.toLocalDate().isAfter(test.getDate().toLocalDate())) {
            testCache.remove();
            return;
        }

        LocalDate date = test.getDate().toLocalDate().minusDays(TEST_REMINDER_DAYS_BEFORE);

        if (now.toLocalDate().isEqual(date)) // Test is TEST_REMINDER_DAYS_BEFORE days from now
            notifyTest();

        else if (date.isBefore(now.toLocalDate())) // Test reminding date has already passed
            testCache.remove();
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        Button button = event.getButton();

        if (button == null) {
            logger.severe("Could not find button.");
            event.reply("Não encontrei esse botão... Contacta um administrador.").setEphemeral(true).queue();
            return;
        }

        if (button.getId() == null) {
            logger.severe("Button does not have an id.");
            event.reply("Não encontrei esse botão... Contacta um administrador.").setEphemeral(true).queue();
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
                event.reply("A ação que esse botão pediu não é válida. Contacta um administrador.").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void processSelectionMenu(SelectionMenuEvent event) {
        event.reply("Essa operação não é suportada. Contacta um administrador.").setEphemeral(true).queue();
    }

    /*
    Private Methods
    */

    private void loadCache() {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            testCache.clear();
            LocalDateTime now = LocalDateTime.now();
            try {
                testCache.addAll(Test.getTests(session));

            } catch (Throwable throwables) {
                throwables.printStackTrace();
            }
            nextCacheReload = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);

            tx.commit();
            session.close();
        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            // TODO: Send error message

        } finally {
            session.close();
        }
    }

    private void notifyTest() {
        Session session = Merenda.getInstance().getFactory().openSession();;
        Transaction tx = null;

        try {
            tx = session.beginTransaction();

            Test test = testCache.remove();
            session.update(test); // Persist test in current session
            Subject subject = test.getSubject();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.WHITE);
            eb.setTitle("Hellooo!! Um teste está para ser realizado em breve!");

            String fieldTitle = String.format(
                    "%s %s :pencil:",
                    test.getName(),
                    subject.getShortName());
            String fieldValue = String.format(
                    "%s (%s) %s-%s",
                    test.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM")),
                    test.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("EEE")),
                    test.getTime().toLocalTime().format(DateTimeFormatter.ofPattern("H:mm")),
                    test.getEndTime().toLocalTime().format(DateTimeFormatter.ofPattern("H:mm"))
            );
            eb.addField(fieldTitle, fieldValue, false);

            getGuild().generateMessageEmbed(eb.build())
                    .setActionRow(Button.danger("timer test-reminder panic", "Panic!"))
                    .queue();

            tx.commit();

        } catch (Throwable e) {
            if (tx != null)
                tx.rollback();

            e.printStackTrace();
            // TODO: Send error message

        } finally {
            session.close();
        }

    }
}
