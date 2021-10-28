package commands;

import java.util.ArrayList;
import java.util.List;

public class CommandParam {

    private final String name;
    private final String description;
    private final List<String> possibleValues;

    public CommandParam(String name, String description) {
        this.name = name;
        this.description = description;
        this.possibleValues = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getPossibleValues() {
        return this.possibleValues;
    }

    public CommandParam addPossibleValue(String value) {
        this.possibleValues.add(value);
        return this;
    }
}
