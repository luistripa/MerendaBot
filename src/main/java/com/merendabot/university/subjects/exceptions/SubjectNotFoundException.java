package com.merendabot.university.subjects.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SubjectNotFoundException extends MerendaBaseExceptionClass {

    private Integer id;
    private String shortName;

    public SubjectNotFoundException(int id) {
        super(String.format("Disciplina com id '%d' não encontrada", id));
        this.id = id;
    }

    public SubjectNotFoundException(String shortName) {
        super(String.format("Disciplina com nome curto '%s' não encontrada.", shortName));
        this.shortName = shortName;
    }

    @Override
    public MessageEmbed getEmbed() {
        if (id == null) {
            return Command.getErrorEmbed(
                    "Erro",
                    "Disciplina não encontrada",
                    String.format("Disciplina com nome '%s' não foi encontrada.", shortName)
            );
        } else {
            return Command.getErrorEmbed(
                    "Erro",
                    "Disciplina não encontrada",
                    String.format("Disciplina com id '%d' não foi encontrada.", id)
            );
        }

    }
}
