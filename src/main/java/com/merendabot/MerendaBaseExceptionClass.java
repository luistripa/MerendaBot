package com.merendabot;

import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class MerendaBaseExceptionClass extends Exception {

    public MerendaBaseExceptionClass() {
        super();
    }

    public MerendaBaseExceptionClass(String message) {
        super(message);
    }

    public MerendaBaseExceptionClass(String message, Throwable cause) {
        super(message, cause);
    }

    public MerendaBaseExceptionClass(Throwable cause) {
        super(cause);
    }

    public MerendaBaseExceptionClass(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public abstract MessageEmbed getEmbed();
}
