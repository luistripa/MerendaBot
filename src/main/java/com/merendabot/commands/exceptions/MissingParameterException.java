package com.merendabot.commands.exceptions;

public class MissingParameterException extends Exception {

    public MissingParameterException() {
        super("Falta um parâmetro");
    }
}
