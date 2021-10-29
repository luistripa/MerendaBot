package com.merendabot.university.timers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import com.merendabot.university.Merenda;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.events.Test;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class WeeklyReportTimerTask extends AbstractTimerTask {
    /**
     * Represents the weekly report.
     *
     * The weekly report is a report that comes out every sunday detailing all the activities, such as assignments
     * or tests, that are going to be done that week.
     */

    private static final DayOfWeek REPORT_DAY_OF_WEEK = DayOfWeek.SUNDAY;

    private boolean hasReported;

    public WeeklyReportTimerTask(JDA jda, Merenda merenda) {
        super(jda, merenda);
        this.hasReported = false;
    }

    @Override
    public void run() {
        // JDA connection not available
        if (!this.getJDA().getStatus().equals(JDA.Status.CONNECTED)) {
            System.out.println("Warning: JDA status is not CONNECTED. Weekly report will not be sent.");
            return;
        }

        Guild guild = this.getJDA().getGuildById(GUILD_ID);
        if (guild == null) {
            System.out.printf("Error: Guild with ID %s could not be found\n", GUILD_ID);
            return;
        }

        TextChannel channel = guild.getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            System.out.printf("Error: Channel with ID %s could not be found in guild %s\n", CHANNEL_ID, GUILD_ID);
            return;
        }

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(1).plusDays(1); // Plus 1 day so .isBefore() counts SUNDAY

        if (!start.getDayOfWeek().equals(REPORT_DAY_OF_WEEK)) { // Only send report on the designated weekday
            hasReported = false;
            return;

        } else if (hasReported) // Should not report twice on the same day
            return;

        try {
            // Get fields for tests and assignments
            MessageEmbed.Field testsField = this.getTests(start, end);
            MessageEmbed.Field assignmentsField = this.getAssignments(start, end);

            MessageEmbed messageEmbed = this.generateEmbed(start, end, testsField, assignmentsField);

            this.hasReported = true;

            channel.sendMessageEmbeds(messageEmbed)
                    .setActionRow(
                            Button.secondary("timer weekly-report next-week", "E para a semana?")
                    ).queue();
        } catch (SQLException e) {
            channel.sendMessage("Ocorreu um erro. Contacta um administrador.").queue();
        }
    }

    @Override
    public void processButtonClick(ButtonClickEvent event) {
        LocalDate current = LocalDate.now();
        LocalDate start = current.with(TemporalAdjusters.next(REPORT_DAY_OF_WEEK));
        LocalDate end = start.with(TemporalAdjusters.next(REPORT_DAY_OF_WEEK)).plusDays(1);

        try {
            MessageEmbed.Field testsField = this.getTests(start, end);
            MessageEmbed.Field assignmentsField = this.getAssignments(start, end);

            MessageEmbed messageEmbed = this.generateEmbed(start, end, testsField, assignmentsField);

            event.replyEmbeds(messageEmbed).setEphemeral(true).queue();
        } catch (SQLException e) {
            event.reply("Ocorreu um erro. Contacta um administrador.").setEphemeral(true).queue();
        }
    }

    /**
     * Gets all the tests and generates an embed field for display.
     *
     * @param start The start date that should be considered for test fetch
     * @param end The end date that should be considered for test fetch
     * @return An Embed Field
     * @throws SQLException If an SQL error occurs
     */
    private MessageEmbed.Field getTests(LocalDate start, LocalDate end) throws SQLException {
        StringBuilder fieldValue = new StringBuilder();

        ResultSet rs = Test.getTests(getMerenda().getConnection());
        while (rs.next()) {
            Test test = Test.getTestFromRS(rs);
            if (test.getStartDate().isBefore(end.plusDays(1)) && test.getStartDate().isAfter(start))
                fieldValue.append(
                        String.format(
                                "%s - %s (%s)\n",
                                test.getName(),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("E"))
                        )
                );
        }

        if (fieldValue.length() == 0)
            fieldValue.append("Não há testes esta semana.");

        return new MessageEmbed.Field("Testes", fieldValue.toString(), false);
    }

    /**
     * Gets all the assignments and generates an embed field for display
     *
     * @param start The start date that should be considered for assignment fetch
     * @param end The end date that should be considered for assignment fetch
     * @return An Embed Field
     * @throws SQLException If an SQL error occurs
     */
    private MessageEmbed.Field getAssignments(LocalDate start, LocalDate end) throws SQLException {
        StringBuilder fieldValue = new StringBuilder();

        ResultSet rs = Assignment.getAssignments();
        while (rs.next()) {
            Assignment assignment = Assignment.getAssignmentFromRS(rs);
            if (assignment.getStartDate().isBefore(end) && assignment.getStartDate().isAfter(start))
                fieldValue.append(
                        String.format(
                                "%s - %s (%s)\n",
                                assignment.getName(),
                                assignment.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                assignment.getStartDate().format(DateTimeFormatter.ofPattern("E"))
                        )
                );
        }
        if (fieldValue.length() == 0)
            fieldValue.append("Não há trabalhos esta semana.");

        return new MessageEmbed.Field("Trabalhos", fieldValue.toString(), false);
    }

    /**
     * Generates the final message embed, joining all fields.
     *
     * @param start The start day of the week
     * @param end The end date of the week
     * @param fields All embed fields that should be joined
     * @return A MessageEmbed object ready to send
     */
    private MessageEmbed generateEmbed(LocalDate start, LocalDate end, MessageEmbed.Field...fields) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(0, 150, 255));
        eb.setTitle(
                String.format(
                        "Resumo semanal. Semana %s - %s",
                        start.format(DateTimeFormatter.ofPattern("dd/MM")),
                        end.minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM"))
                ));

        for (MessageEmbed.Field field : fields) {
            eb.addField(field);
        }

        return eb.build();
    }
}
