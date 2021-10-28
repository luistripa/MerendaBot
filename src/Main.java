import commands.CallbackCommand;
import commands.CallbackCommandClass;
import commands.Command;
import commands.CommandParam;
import commands.commands.*;
import commands.exceptions.CommandNameAlreadyExistsException;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import university.Merenda;
import university.subjects.Subject;
import university.timers.*;

import javax.security.auth.login.LoginException;
import java.sql.*;
import java.util.List;

public class Main extends ListenerAdapter {
    private static final String COMMAND_PREFIX = "merenda!";

    private static final String BOT_ID = "897147724447776819";

    private static Merenda merenda;

    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        JDA jda = null;
        try {
            jda = JDABuilder.createDefault(System.getenv("TOKEN"))
                    .setActivity(Activity.watching(COMMAND_PREFIX))
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.addEventListener(new Main());
        } catch (LoginException e) {
            e.printStackTrace();
        }
        //You can also add event listeners to the already built JDA instance
        // Note that some events may not be received if the listener is added after calling build()
        // This includes events such as the ReadyEvent
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            merenda = new Merenda();

            JDA jda = event.getJDA();

            this.registerCommands();
            this.registerTimers(jda, merenda);

        } catch (CommandNameAlreadyExistsException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event)
    {
        Message message = event.getMessage();
        if (!isCommand(message))
            return;

        String[] splitCommand = splitCommand(message.getContentDisplay());

        if (merenda.getCommandHandler().hasCommand(splitCommand[0])) {
            Command command = merenda.getCommandHandler().getCommand(splitCommand[0]);
            if (command instanceof CallbackCommandClass)
                command.execute(merenda, splitCommand, event)
                        .queue(m -> {
                            CallbackCommand c = (CallbackCommand) command;
                            c.messageCallback(merenda, m, event);
                        });
            else
                command.execute(merenda, splitCommand, event).queue();

        } else {
            event.getChannel().sendMessage(
                    "Oops! Não encontrei esse comando. Tenta **merenda!help** para um lista de comandos"
            ).queue();
        }
    }

    /**
     * Fired when a selection is made in a selection menu.
     *
     * @param event The event that triggered the method
     */
    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        /*
         * selection menu id's should have the following format:
         * [section 1] [section 2] [section3]
         *
         * section 1 - Identifies the handler type. (Command, Timer, Etc.)
         *
         * section 2 - Handler identifier (Identifier of a command or timer)
         *
         * section 3 - Instruction (Handler specific. Some handlers may completely ignore this) (may have internal sections)
         */
        String[] selectionMenu = event.getSelectionMenu().getId().split(" ");
        switch (selectionMenu[0]) {
            case "command": {
                Command command = merenda.getCommandHandler().getCommand(selectionMenu[1]);
                command.processSelectionMenu(merenda, event);
                break;
            }
            case "timer": {
                event.reply("Isto ainda não foi implementado.").setEphemeral(true).queue();
                break;
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        /*
         * Button id's should have the following format:
         * [section 1] [section 2] [section3]
         *
         * section 1 - Identifies the handler type. (Command, Timer, Etc.)
         *
         * section 2 - Handler identifier (Identifier of a command or timer)
         *
         * section 3 - Instruction (Handler specific. Some handlers may completely ignore this)
         */

        String[] button = event.getButton().getId().split(" ");

        switch (button[0]) { // Section 1 of button id
            case "command": {
                if (merenda.getCommandHandler().hasCommand(button[1])) {
                    Command command  = merenda.getCommandHandler().getCommand(button[1]); // Command id is in Section 2
                    command.processButtonPressed(merenda, event).setEphemeral(true).queue();
                } else {
                    event.reply("Uhmm... não encontrei essa ação. Se isto for um erro contacta o administrador.")
                            .setEphemeral(true).queue();
                }
                break;
            }
            case "timer": {
                ScheduleTimer timer = merenda.getTimerHandler().getTimer(button[1]);
                timer.processButtonClick(event);
                break;
            }
        }
    }

    private void registerCommands() {
        merenda.getCommandHandler().clearCommands();
        merenda.getCommandHandler().addCommand(new HelpCommand("Core", "help", "Mostra o menu de ajuda.")
                .addParam(new CommandParam("comando", "opcional")
                        .addPossibleValue("Qualquer comando sem prefixo")));

        merenda.getCommandHandler().addCommand(new ClassesCommand("Aulas", "aulas", "Lista todas as aulas."));
        merenda.getCommandHandler().addCommand(new NowCommand("Aulas", "now", "Lista aulas a decorrer agora."));
        merenda.getCommandHandler().addCommand(new TestsCommand("Aulas", "testes", "Lista todos os testes."));
        merenda.getCommandHandler().addCommand(new AssigmentsCommand("Aulas", "trabalhos", "Lista todos os trabalhos"));
        merenda.getCommandHandler().addCommand(new LinksCommand("Aulas", "links", "Lista todos os links importantes."));
        merenda.getCommandHandler().addCommand(new TeachersCommand("Aulas", "professores", "Lista todos professores."));

        merenda.getCommandHandler().addCommand(new TestCommand("Other", "test", "Testa o que tiver configurado."));

        merenda.getCommandHandler().addCommand(new PollCommand("Polls", "poll", "Cria votações")
                .addParam(new CommandParam("descrição", "Descrição da votação")));
        merenda.getCommandHandler().addCommand(new PollCloseCommand("Polls", "poll_close", "Encerra uma votação, independentemente do número de votos."));
    }

    private void registerTimers(JDA jda, Merenda merenda) {
        merenda.getTimerHandler().addTimer(TimerHandler.CLASSES_TIMER, new ClassesTimerTask(jda, merenda));
        merenda.getTimerHandler().addTimer(TimerHandler.WEEKLY_REPORT_TIMER, new WeeklyReportTimerTask(jda, merenda));

        merenda.getTimerHandler().startTimer(TimerHandler.CLASSES_TIMER, 0, 1000);
        merenda.getTimerHandler().startTimer(TimerHandler.WEEKLY_REPORT_TIMER, 0, 300000); // 5 minutes
    }

    private boolean isCommand(Message message) {
        return message.getContentDisplay().startsWith(COMMAND_PREFIX);
    }

    private String[] splitCommand(String content) {
        String commandMessage = content.split(COMMAND_PREFIX)[1];
        return commandMessage.split(" ");
    }
}