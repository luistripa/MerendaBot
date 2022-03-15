package com.merendabot.commands;

import com.merendabot.commands.commands.*;
import com.merendabot.commands.commands.events.EventsCommand;
import com.merendabot.commands.exceptions.CommandAlreadyExistsException;
import com.merendabot.commands.exceptions.CommandDoesNotExistException;
import net.dv8tion.jda.api.entities.Guild;

import java.util.*;

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

    public void registerCommandsForGuild(Guild guild) {
        guild.updateCommands().addCommands(commands.values()).queue();
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
        addCommand(new EventsCommand(CommandCategory.CLASSES, "eventos", "Adiciona/Edita/Remove eventos"));
        addCommand(new ClassesCommand(CommandCategory.CLASSES, "aulas", "Lista todas as aulas."));
        addCommand(new NowCommand(CommandCategory.CLASSES, "now", "Lista aulas a decorrer agora."));
        addCommand(new TestsCommand(CommandCategory.CLASSES, "testes", "Lista todos os testes."));
        addCommand(new AssigmentsCommand(CommandCategory.CLASSES, "trabalhos", "Lista todos os trabalhos"));
        addCommand(new LinksCommand(CommandCategory.CLASSES, "links", "Lista todos os links importantes."));
        addCommand(new TeachersCommand(CommandCategory.CLASSES, "professores", "Lista todos professores."));

        addCommand(new TestCommand(CommandCategory.OTHER, "test", "Testa o que tiver configurado."));

        addCommand(new PollCommand(CommandCategory.POLLS, "poll", "Cria uma votação"));
        addCommand(new PollCloseCommand(CommandCategory.POLLS, "poll_close", "Cria uma votação de escolha múltipla"));
    }
}
