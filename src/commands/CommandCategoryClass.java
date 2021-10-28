package commands;

import java.util.*;

public class CommandCategoryClass implements CommandCategory {

    private final String name;
    private final List<Command> commandList;

    public CommandCategoryClass(String name) {
        this.name = name;
        this.commandList = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Command> getCommands() {
        return this.commandList;
    }

    @Override
    public void addCommand(Command command) {
        this.commandList.add(command);
    }

    @Override
    public void removeCommand(Command command) {
        this.commandList.remove(command);
    }


}
