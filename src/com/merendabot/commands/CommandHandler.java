package com.merendabot.commands;

import com.merendabot.Merenda;
import com.merendabot.commands.commands.*;
import com.merendabot.commands.exceptions.CommandAlreadyExistsException;
import com.merendabot.commands.exceptions.CommandDoesNotExistException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private Map<String, Command> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        registerCommands();
    }

    public boolean hasCommand(String commandId) {
        return commands.get(commandId) != null;
    }

    public Command getCommand(String commandId) throws CommandDoesNotExistException {
        if (!hasCommand(commandId))
            throw new CommandDoesNotExistException(commandId);
        return commands.get(commandId);
    }

    /*
    Private methods
     */

    private void addCommand(Command command) throws CommandAlreadyExistsException {
        if (hasCommand(command.getName()))
            throw new CommandAlreadyExistsException(command.getName());
        commands.put(command.getName(), command);
    }

    private void registerCommands() {
        addCommand(new ClassesCommand(CommandCategory.CLASSES, "aulas", "Lista todas as aulas."));
        addCommand(new NowCommand(CommandCategory.CLASSES, "now", "Lista aulas a decorrer agora."));
        addCommand(new TestsCommand(CommandCategory.CLASSES, "testes", "Lista todos os testes."));
        addCommand(new AssigmentsCommand(CommandCategory.CLASSES, "trabalhos", "Lista todos os trabalhos"));
        addCommand(new LinksCommand(CommandCategory.CLASSES, "links", "Lista todos os links importantes."));
        addCommand(new TeachersCommand(CommandCategory.CLASSES, "professores", "Lista todos professores."));

        addCommand(new TestCommand(CommandCategory.OTHER, "test", "Testa o que tiver configurado."));

        addCommand(new PollCommand(CommandCategory.POLLS, "poll", "Cria uma vota????o"));
        addCommand(new MultiChoicePollCommand(CommandCategory.POLLS, "multipoll", "Encerra uma vota????o, independentemente do n??mero de votos."));
        addCommand(new PollCloseCommand(CommandCategory.POLLS, "poll_close", "Cria uma vota????o de escolha m??ltipla"));


        Merenda.getInstance().getJda().updateCommands().addCommands(
                new CommandData("aulas", "Lista todas as aulas."),
                new CommandData("now", "Lista as aulas a decorrer agora."),
                new CommandData("testes", "Lista todos os testes."),
                new CommandData("trabalhos", "Lista todos os trabalhos."),
                new CommandData("links", "Lista todos os links importantes."),
                new CommandData("professores", "Lista todos os professores."),
                new CommandData("poll", "Cria uma vota????o.")
                        .addOption(OptionType.STRING, "descri????o", "Descri????o da vota????o.", true),
                new CommandData("multipoll", "Cria uma vota????o de escolha m??ltipla.")
                        .addOption(OptionType.STRING, "descri????o", "Op????o de vota????o.", true)
                        .addOption(OptionType.STRING, "op????o_1", "Op????o de vota????o.", true)
                        .addOption(OptionType.STRING, "op????o_2", "Op????o de vota????o.", true)
                        .addOption(OptionType.STRING, "op????o_3", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_4", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_5", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_6", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_7", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_8", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_9", "Op????o de vota????o.", false)
                        .addOption(OptionType.STRING, "op????o_10", "Op????o de vota????o.", false),
                new CommandData("poll_close", "Encerra uma vota????o.")
                        .addOption(OptionType.STRING, "id_mensagem", "ID da mensagem original da poll", true)
        ).queue();
    }
}
