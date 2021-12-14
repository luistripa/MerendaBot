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

        addCommand(new PollCommand(CommandCategory.POLLS, "poll", "Cria uma votação"));
        addCommand(new MultiChoicePollCommand(CommandCategory.POLLS, "multipoll", "Encerra uma votação, independentemente do número de votos."));
        addCommand(new PollCloseCommand(CommandCategory.POLLS, "poll_close", "Cria uma votação de escolha múltipla"));


        Merenda.getInstance().getJda().updateCommands().addCommands(
                new CommandData("aulas", "Lista todas as aulas."),
                new CommandData("now", "Lista as aulas a decorrer agora."),
                new CommandData("testes", "Lista todos os testes."),
                new CommandData("trabalhos", "Lista todos os trabalhos."),
                new CommandData("links", "Lista todos os links importantes."),
                new CommandData("professores", "Lista todos os professores."),
                new CommandData("poll", "Cria uma votação.")
                        .addOption(OptionType.STRING, "descrição", "Descrição da votação.", true),
                new CommandData("multipoll", "Cria uma votação de escolha múltipla.")
                        .addOption(OptionType.STRING, "descrição", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_1", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_2", "Opção de votação.", true)
                        .addOption(OptionType.STRING, "opção_3", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_4", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_5", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_6", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_7", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_8", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_9", "Opção de votação.", false)
                        .addOption(OptionType.STRING, "opção_10", "Opção de votação.", false),
                new CommandData("poll_close", "Encerra uma votação.")
                        .addOption(OptionType.STRING, "id_mensagem", "ID da mensagem original da poll", true)
        ).queue();
    }
}
