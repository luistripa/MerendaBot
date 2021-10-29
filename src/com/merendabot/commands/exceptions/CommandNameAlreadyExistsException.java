package com.merendabot.commands.exceptions;

public class CommandNameAlreadyExistsException extends Exception {
    public CommandNameAlreadyExistsException(String name) {
        super("A command with name "+name+" already exists.");
    }
}
