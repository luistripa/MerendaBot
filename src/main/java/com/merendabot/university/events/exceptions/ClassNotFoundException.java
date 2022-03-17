package com.merendabot.university.events.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ClassNotFoundException extends MerendaBaseExceptionClass {

    public ClassNotFoundException(int id) {
        super(String.format("Aula com o id '%d' não encontrada", id));
    }

    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
                "Erro",
                "Aula não encontrada",
                getMessage()
        );
    }
}
