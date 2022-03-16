package com.merendabot.commands.commands.events;

import com.merendabot.GuildManager;
import com.merendabot.commands.Command;
import com.merendabot.commands.CommandCategory;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class EventsCommand extends Command {

    public EventsCommand(CommandCategory category, String name, String description) {
        super(category, name, description);
        super.addSubcommandGroups(
                new SubcommandGroupData("aula", "Eventos relacionados com aulas").addSubcommands(
                        new SubcommandData("listar", "Lista todas as aulas"),
                        new SubcommandData("novo", "Adiciona uma aula")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "nome", "Nome da aula sem referência à disciplina", true),
                                        new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", true),
                                        new OptionData(OptionType.STRING, "data", "Data de inicio da aula no formato YYYY-MM-DD", true),
                                        new OptionData(OptionType.STRING, "data_fim", "Data de fim da aula no formato YYYY-MM-DD", true),
                                        new OptionData(OptionType.STRING, "hora", "Hora de inicio da aula no formato HH:MM", true),
                                        new OptionData(OptionType.STRING, "hora_fim", "Hora de fim da aula no formato HH:MM", true),
                                        new OptionData(OptionType.STRING, "link", "Link da aula", true)
                                ),

                        new SubcommandData("editar", "Edita uma aula")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "id", "Id da aula para editar", true),
                                        new OptionData(OptionType.STRING, "nome", "Nome da aula sem referência à disciplina", false),
                                        new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", false),
                                        new OptionData(OptionType.STRING, "data", "Data de inicio da aula no formato YYYY-MM-DD", false),
                                        new OptionData(OptionType.STRING, "data_fim", "Data de fim da aula no formato YYYY-MM-DD", false),
                                        new OptionData(OptionType.STRING, "hora", "Hora de inicio da aula no formato HH:MM", false),
                                        new OptionData(OptionType.STRING, "hora_fim", "Hora de fim da aula no formato HH:MM", false),
                                        new OptionData(OptionType.STRING, "link", "Link da aula", false)
                                ),
                        new SubcommandData("apagar", "Apaga um aula")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "id", "Id da aula para apagar", true)
                                )
                ),
                new SubcommandGroupData("trabalho", "Commandos relacionados com trabalhos.")
                        .addSubcommands(
                                new SubcommandData("listar", "Lista todas os trabalhos"),
                                new SubcommandData("novo", "Adiciona um trabalho")
                                        .addOptions(
                                                new OptionData(OptionType.STRING, "nome", "Nome do trabalho sem referência à disciplina", true),
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", true),
                                                new OptionData(OptionType.STRING, "data", "Data de entrega do trbalho no formato YYYY-MM-DD", true),
                                                new OptionData(OptionType.STRING, "hora", "Hora de entrega do trabalho no formato HH:MM", true),
                                                new OptionData(OptionType.STRING, "link", "Link do trabalho", false)
                                        ),
                                new SubcommandData("editar", "Edita um trabalho")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do trabalho para editar", true),
                                                new OptionData(OptionType.STRING, "nome", "Nome do trabalho sem referência à disciplina", false),
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", false),
                                                new OptionData(OptionType.STRING, "data", "Data de entrega do trabalho no formato YYYY-MM-DD", false),
                                                new OptionData(OptionType.STRING, "hora", "Hora de entrega do trabalho no formato HH:MM", false),
                                                new OptionData(OptionType.STRING, "link", "Link do trabalho", false)
                                        ),
                                new SubcommandData("apagar", "Apagar um trabalho")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do trabalho para apagar", true)
                                        )
                        ),
                new SubcommandGroupData("teste", "Comandos relacionados com testes")
                        .addSubcommands(
                                new SubcommandData("listar", "Lista todas os testes"),
                                new SubcommandData("novo", "Adiciona um teste")
                                        .addOptions(
                                                new OptionData(OptionType.STRING, "nome", "Nome do teste sem referência à disciplina", true),
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", true),
                                                new OptionData(OptionType.STRING, "data", "Data do teste no formato YYYY-MM-DD", true),
                                                new OptionData(OptionType.STRING, "hora", "Hora de início do teste no formato HH:MM", true),
                                                new OptionData(OptionType.STRING, "hora_fim", "Hora de fim do teste no formato HH:MM", true),
                                                new OptionData(OptionType.STRING, "link", "Link do teste", true)
                                        ),
                                new SubcommandData("editar", "Edita um teste")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do teste para editar", true),
                                                new OptionData(OptionType.STRING, "nome", "Nome do teste sem referência à disciplina", false),
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina. Ex: CGI", false),
                                                new OptionData(OptionType.STRING, "data", "Data de início do teste no formato YYYY-MM-DD", false),
                                                new OptionData(OptionType.STRING, "hora", "Hora de início do teste no formato HH:MM", false),
                                                new OptionData(OptionType.STRING, "hora_fim", "Hora de fim do teste no formato HH:MM", false),
                                                new OptionData(OptionType.STRING, "link", "Link do teste", false)
                                        ),
                                new SubcommandData("apagar", "Apaga um teste")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do teste para editar", true)
                                        )
                        ),
                new SubcommandGroupData("disciplina", "Comandos relacionados com disciplinas.")
                        .addSubcommands(
                                new SubcommandData("listar", "Lista todas as disciplinas"),
                                new SubcommandData("novo", "Adiciona uma disciplina.")
                                        .addOptions(
                                                new OptionData(OptionType.STRING, "full_name", "Nome completo da disciplina", true),
                                                new OptionData(OptionType.STRING, "short_name", "Nome curto da disciplina. Ex: CGI", true)
                                        ),
                                new SubcommandData("editar", "Edita uma disciplina")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id da disciplina para editar", true),
                                                new OptionData(OptionType.STRING, "full_name", "Nome completo da disciplina", false),
                                                new OptionData(OptionType.STRING, "short_name", "Nome curto da disciplina. Ex: CGI", false)
                                        ),
                                new SubcommandData("apagar", "Apaga um disciplina")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id da disciplina para apagar", true)
                                        )
                        ),
                new SubcommandGroupData("professor", "Comandos relacionados com professores.")
                        .addSubcommands(
                                new SubcommandData("listar", "Lista todas os professores"),
                                new SubcommandData("novo", "Adiciona um professor")
                                        .addOptions(
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina", true),
                                                new OptionData(OptionType.STRING, "nome", "Nome do professor", true),
                                                new OptionData(OptionType.STRING, "email", "Email do professor", true)
                                        ),
                                new SubcommandData("editar", "Edita um professor")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do professor", true),
                                                new OptionData(OptionType.STRING, "disciplina", "Nome curto da disciplina", false),
                                                new OptionData(OptionType.STRING, "nome", "Nome do professor", false),
                                                new OptionData(OptionType.STRING, "email", "Email do professor", false)
                                        ),
                                new SubcommandData("apagar", "Apaga um professor")
                                        .addOptions(
                                                new OptionData(OptionType.INTEGER, "id", "Id do professor", true)
                                        )
                        )
                // TODO: Important links
        );
    }

    @Override
    public void execute(GuildManager guild, SlashCommandEvent event) {
        String subcommandGroup = event.getSubcommandGroup();

        switch (subcommandGroup) {
            case "aula" -> ClassesEventCommandProcessor.processClass(guild, event);
            case "trabalho" -> AssignmentsEventCommandProcessor.processAssignment(guild, event);
            case "teste" -> TestsEventCommandProcessor.processTest(guild, event);
            case "disciplina" -> SubjectsEventCommandProcessor.processSubject(guild, event);
            case "professor" -> ProfessorsEventCommandProcessor.processProfessor(guild, event);
            default -> {} // TODO
        }
    }

    @Override
    public void processButtonClick(GuildManager guild, ButtonClickEvent event) {
        // TODO
    }

    @Override
    public void processSelectionMenu(GuildManager guild, SelectionMenuEvent event) {
        // TODO
    }

}
