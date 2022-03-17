package com.merendabot.commands.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class MissingParameterException extends MerendaBaseExceptionClass {

    public MissingParameterException() {
        super("Falta um parâmetro");
    }

    @Override
    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
                "Erro",
                "Número de parâmtros inválido",
                "Falta um parâmetro"
        );
    }
}
