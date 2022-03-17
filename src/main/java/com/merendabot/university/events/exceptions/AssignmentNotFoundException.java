package com.merendabot.university.events.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AssignmentNotFoundException extends MerendaBaseExceptionClass {


    public AssignmentNotFoundException(int id) {
        super(String.format("Trabalho com o id '%d' não encontrado.", id));
    }

    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
                "Erro",
                "Trabalho não encontrado",
                getMessage()
        );
    }
}
