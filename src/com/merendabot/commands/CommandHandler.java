package com.merendabot.commands;

import com.merendabot.commands.commands.*;
import com.merendabot.university.Merenda;

import java.util.*;

public class CommandHandler {

    public static final String COMMAND_PREFIX = "merenda!";

    private final Map<String, Command> commandMap;
    private final Map<String, List<Command>> commandsByCategory;

    public CommandHandler() {
        this.commandMap = new HashMap<>();
        this.commandsByCategory = new HashMap<>();
        registerCommands();
    }

    public boolean hasCommand(String commandId) {
        return this.commandMap.get(commandId) != null;
    }

    public boolean hasCategory(CommandCategory category) {
        return this.commandsByCategory.get(category.toString()) != null;
    }

    public Command getCommand(String commandId) {
        return this.commandMap.get(commandId);
    }

    public List<Command> getCommandsByCategory(String category) {
        return commandsByCategory.get(category);
    }

    public Set<String> getCommandCategories() {
        return  this.commandsByCategory.keySet();
    }


    /*
    PRIVATE METHODS
     */

    private void addCommand(Command command) {
        if (!hasCategory(command.getCategory())) {
            this.commandsByCategory.put(command.getCategory().toString(), new LinkedList<>());
        }
        this.commandsByCategory.get(command.getCategory().toString()).add(command);
        this.commandMap.put(command.getName(), command);
    }

    private void registerCommands() {
        addCommand(new HelpCommand(CommandCategory.CORE, "help", "Mostra o menu de ajuda.")
                .addParam(new CommandParam("comando", "opcional")
                        .addPossibleValue("Qualquer comando sem prefixo")));

        addCommand(new ClassesCommand(CommandCategory.CLASSES, "aulas", "Lista todas as aulas."));
        addCommand(new NowCommand(CommandCategory.CLASSES, "now", "Lista aulas a decorrer agora."));
        addCommand(new TestsCommand(CommandCategory.CLASSES, "testes", "Lista todos os testes."));
        addCommand(new AssigmentsCommand(CommandCategory.CLASSES, "trabalhos", "Lista todos os trabalhos"));
        addCommand(new LinksCommand(CommandCategory.CLASSES, "links", "Lista todos os links importantes."));
        addCommand(new TeachersCommand(CommandCategory.CLASSES, "professores", "Lista todos professores."));

        addCommand(new TestCommand(CommandCategory.OTHER, "test", "Testa o que tiver configurado."));

        addCommand(new PollCommand(CommandCategory.POLLS, "poll", "Cria votações")
                .addParam(new CommandParam("descrição", "Descrição da votação")));
        addCommand(new PollCloseCommand(CommandCategory.POLLS, "poll_close", "Encerra uma votação, independentemente do número de votos."));
    }
}
