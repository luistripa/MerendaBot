package main.java.com.merendabot.commands;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

public class CommandParam {

    private OptionType type;
    private String name;
    private String description;
    private List<String> possibleValues;

    public CommandParam(OptionType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.possibleValues = new ArrayList<>();
    }

    public OptionType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public CommandParam addPossibleValue(String value) {
        possibleValues.add(value);
        return this;
    }
}
