package com.merendabot.commands;

import java.util.*;

public class CommandHandler {

    private final Map<String, Command> commandMap;
    private final Map<String, List<Command>> commandsByCategory;

    public CommandHandler() {
        this.commandMap = new HashMap<>();
        this.commandsByCategory = new HashMap<>();
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

    public List<Command> getCommands() {
        return (List<Command>) this.commandMap.values();
    }

    public List<Command> getCommandsByCategory(String category) {
        return commandsByCategory.get(category);
    }

    public Set<String> getCommandCategories() {
        return  this.commandsByCategory.keySet();
    }

    public void addCommand(Command command) {
        if (!hasCategory(command.getCategory())) {
            this.commandsByCategory.put(command.getCategory().toString(), new LinkedList<>());
        }
        this.commandsByCategory.get(command.getCategory().toString()).add(command);
        this.commandMap.put(command.getName(), command);
    }

    public void removeCommand(String commandId) {
        Command command = this.commandMap.remove(commandId);
        this.commandsByCategory.get(command.getCategory().toString()).remove(command);
    }

    public void clearCommands() {
        commandMap.clear();
        commandsByCategory.clear();
    }
}
