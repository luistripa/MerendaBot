package com.merendabot.university.subjects.exceptions;

public class SubjectNotFoundException extends Exception {

    public SubjectNotFoundException(int id) {
        super(String.format("Disciplina com id '%d' não encontrada", id));
    }

    public SubjectNotFoundException(String shortName) {
        super(String.format("Disciplina com nome curto '%s' não encontrada.", shortName));
    }
}
