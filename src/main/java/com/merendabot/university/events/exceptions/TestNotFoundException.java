package com.merendabot.university.events.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class TestNotFoundException extends MerendaBaseExceptionClass {

    public TestNotFoundException(int id) {
        super(String.format("Teste com id %d não encontrado.", id));
    }

    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
                "Erro",
                "Teste não encontrado",
                getMessage()
        );
    }
}
