package com.merendabot.commands.exceptions;

public class CommandDoesNotExistException extends Exception {

    public CommandDoesNotExistException(String commandId) {
        super("Command does not exist: "+commandId);
    }
}
