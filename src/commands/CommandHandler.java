package commands;

import java.util.*;

public class CommandHandler {

    private final Map<String, Command> commandMap;
    private final Map<String, CommandCategory> commandCategoryMap;

    public CommandHandler() {
        this.commandMap = new HashMap<>();
        this.commandCategoryMap = new HashMap<>();
    }

    public boolean hasCommand(String command_id) {
        return this.commandMap.get(command_id) != null;
    }

    public boolean hasCategory(String category) {
        return this.commandCategoryMap.get(category) != null;
    }

    public Command getCommand(String command_id) {
        return this.commandMap.get(command_id);
    }

    public List<Command> getCommands() {
        return (List<Command>) this.commandMap.values();
    }

    public Collection<CommandCategory> getCommandCategories() {
        return  this.commandCategoryMap.values();
    }

    public void addCommand(Command command) {
        if (!hasCategory(command.getCategory())) {
            this.commandCategoryMap.put(command.getCategory(), new CommandCategoryClass(command.getCategory()));
        }
        this.commandCategoryMap.get(command.getCategory()).addCommand(command);
        this.commandMap.put(command.getName(), command);
    }

    public void removeCommand(String command_id) {
        Command command = this.commandMap.remove(command_id);
        this.commandCategoryMap.get(command.getCategory()).removeCommand(command);
    }

    public void clearCommands() {
        commandMap.clear();
        commandCategoryMap.clear();
    }
}
