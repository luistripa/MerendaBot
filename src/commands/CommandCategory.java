package commands;

import java.util.List;

public interface CommandCategory {

    /**
     * Gets the name of the category.
     * This name can be used for display.
     *
     * @return The name of the category
     */
    String getName();

    /**
     * Gets a list of all commands included in this category.
     *
     * @return A List of commands
     * @see Command
     */
    List<Command> getCommands();

    /**
     * Adds a command to the category.
     * The command must not exist in the processor.
     *
     * @param command The command object to be added
     * @see Command
     */
    void addCommand(Command command);

    /**
     * Removes a command from the category.
     *
     * @param command The command object to be removed
     * @see Command
     */
    void removeCommand(Command command);
}
