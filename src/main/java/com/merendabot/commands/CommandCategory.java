package main.java.com.merendabot.commands;

public enum CommandCategory {
    CORE("Core"),
    CLASSES("Aulas"),
    POLLS("Votações"),
    OTHER("Outro");

    private final String friendlyName;

    CommandCategory(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
