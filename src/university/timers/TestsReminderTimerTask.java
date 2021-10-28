package university.timers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import university.Merenda;
import university.events.Test;
import university.subjects.Subject;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestsReminderTimerTask extends AbstractTimerTask {

    private static final int TEST_REMINDER_DAYS_BEFORE = 5;

    private Queue<Test> testCache;
    private LocalDateTime nextCacheReload;

    public TestsReminderTimerTask(JDA jda, Merenda merenda) {
        super(jda, merenda);
        testCache = loadCache();
    }

    @Override
    public void run() {
        Guild guild = this.getJDA().getGuildById(GUILD_ID);
        if (guild == null) {
            System.out.println("Error: Could not find guild with id " + GUILD_ID);
            this.cancel();
            return;
        }
        TextChannel channel = guild.getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            System.out.println("Error: Could not find channel with id " + CHANNEL_ID);
            this.cancel();
            return;
        }

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

        // Test is TEST_REMINDER_DAYS_BEFORE days from now
        if (now.toLocalDate().isEqual(test.getStartDate().minusDays(TEST_REMINDER_DAYS_BEFORE))) {
            notifyTest(channel, test);
        }
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        switch (event.getButton().getId().split(" ")[2]) {
            case "panic": {
                event.reply(":eyes:").queue();
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
            ResultSet rs = Test.getTests(this.getMerenda().getConnection());
            while (rs.next()) {
                Test event = Test.getTestFromRS(rs);
                Subject subject = Subject.getSubjectById(event.getSubjectId());
                event.setSubject(subject);
                newTestCache.add(event);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        nextCacheReload = now.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        return newTestCache;
    }


    private void notifyTest(TextChannel channel, Test test) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.WHITE);
        eb.setTitle("Hellooo!! Um teste está para ser realizado em breve!");

        String fieldTitle = String.format("%s %s", test.getName(), test.getSubject().getShortName());
        String fieldValue = String.format(
                "%s %s-%s",
                test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                test.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                test.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"))
        );
        eb.addField(fieldTitle, fieldValue, false);

        channel.sendMessageEmbeds(eb.build()).setActionRow(
                Button.success("timer test-reminder-timer panic", "Panic")
        ).queue();
    }
}
