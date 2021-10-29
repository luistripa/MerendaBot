package com.merendabot.commands.exceptions;

public class CommandDoesNotExistException extends Exception {

    public CommandDoesNotExistException(String name) {
        super("Command "+name+" does not exist.");
    }
}
