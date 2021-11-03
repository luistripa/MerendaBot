package com.merendabot.university.subjects;

public class ProfessorClass implements Professor {

    private int id;
    private String name;
    private String email;
    private int subjectId;

    public ProfessorClass(int id, String name, String email, int subjectId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subjectId = subjectId;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public int getSubjectId() {
        return subjectId;
    }
}
