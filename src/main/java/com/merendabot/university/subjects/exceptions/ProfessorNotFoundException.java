package com.merendabot.university.subjects.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ProfessorNotFoundException extends MerendaBaseExceptionClass {

    public ProfessorNotFoundException(int id) {
        super(String.format("Professor com id '%d' não encontrado", id));
    }

    @Override
    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
            "Erro",
            "Professor não encontrado",
            getMessage()
        );
    }
}
