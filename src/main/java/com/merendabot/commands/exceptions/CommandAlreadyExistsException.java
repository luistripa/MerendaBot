package com.merendabot.commands.exceptions;

public class CommandAlreadyExistsException extends RuntimeException {

    public CommandAlreadyExistsException(String commandId) {
        super("Command already exists: "+commandId);
    }
}
