package com.merendabot.university.subjects;

public class SubjectClass implements Subject {

    private int id;
    private String guild_id;
    private final String name;
    private String shortName;

    public SubjectClass(int id, String guild_id, String name, String shortName) {
        this.id = id;
        this.guild_id = guild_id;
        this.name = name;
        this.shortName = shortName;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getGuildId() {
        return guild_id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getShortName() {
        return shortName;
    }
}
