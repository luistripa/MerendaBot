package com.merendabot.university.events.exceptions;

public class TestNotFoundException extends Throwable {
    public TestNotFoundException(int id) {
        super(String.format("Teste com id %d não encontrado.", id));
    }
}
