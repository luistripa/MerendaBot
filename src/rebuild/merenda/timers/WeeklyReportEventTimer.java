package rebuild.merenda.timers;

import com.merendabot.university.Merenda;
import com.merendabot.university.MessageDispatcher;
import com.merendabot.university.events.Assignment;
import com.merendabot.university.events.Test;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import rebuild.merenda.GuildManager;

import java.awt.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Timer;
import java.util.logging.Logger;

public class WeeklyReportEventTimer extends EventTimer {

    private static final DayOfWeek REPORT_DAY_OF_WEEK = DayOfWeek.SUNDAY;

    private static final Logger logger = Logger.getLogger("main-log");

    private boolean hasReported;

    public WeeklyReportEventTimer(GuildManager guild, Timer scheduler, long delay, long period) {
        super(guild, scheduler, delay, period);

        hasReported = false;
    }

    @Override
    public void run() {
        // JDA connection not available
        if (!Merenda.getJDA().getStatus().equals(JDA.Status.CONNECTED)) {
            logger.warning("JDA status is not CONNECTED. Weekly report will not be sent. STATUS: "+Merenda.getJDA().getStatus());
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

            MessageDispatcher.getInstance().getDefaultChannel()
                    .sendMessageEmbeds(messageEmbed).setActionRow(Button.secondary("timer weekly-report next-week", "E para a semana?"))
                    .queue();

        } catch (SQLException e) {
            MessageDispatcher.getInstance().getDefaultChannel()
                    .sendMessage("Ocorreu um erro. Contacta um administrador.")
                    .queue();
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

    @Override
    public void processSelectionMenu(SelectionMenuEvent event) {
        event.reply("Essa operação não é suportada. Contacta uma administrador.").setEphemeral(true).queue();
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

        for (Test test : Test.getTests()) {
            if (test.getStartDate().isBefore(end.plusDays(1)) && test.getStartDate().isAfter(start))
                fieldValue.append(
                        String.format(
                                "%s %s - %s (%s)%n",
                                test.getName(),
                                test.getSubject().getShortName(),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                test.getStartDate().format(DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("PT")))
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

        for (Assignment assignment : Assignment.getAssignments()) {
            if (assignment.getStartDate().isBefore(end) && assignment.getStartDate().isAfter(start)) {
                fieldValue.append(
                        String.format(
                                "%s %s - %s (%s)%n",
                                assignment.getName(),
                                assignment.getSubject().getShortName(),
                                assignment.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")),
                                assignment.getStartDate().format(DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("PT")))
                        )
                );
            }
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
