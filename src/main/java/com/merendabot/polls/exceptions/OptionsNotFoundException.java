package com.merendabot.polls.exceptions;

import java.util.List;

public class OptionsNotFoundException extends Exception {

    public OptionsNotFoundException(List<String> options) {
        super("Poll options where not found. Options: "+String.join(", ", options));
    }
}
