package com.merendabot.commands.exceptions;

import com.merendabot.MerendaBaseExceptionClass;
import com.merendabot.commands.Command;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class InvalidDateTimeFormatException extends MerendaBaseExceptionClass {

    @Override
    public MessageEmbed getEmbed() {
        return Command.getErrorEmbed(
                "Erro",
                "Formato da data/hora inválido.",
                "O formato da data deve ser YYYY-MM-DD e da hora deve ser HH:MM"
        );
    }
}
