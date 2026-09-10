package dsb.sunny.command.commands.uncategorized;

import dsb.sunny.command.builder.SyntaxBuilder;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.command.handler.SlashCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class HelpCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {

        // Just in case, sollte man /report member o.Ä. eingeben, dass Sunny am Ende nicht sagt, dass der Befehl nicht gefunden wurde.
        String command = event.getOption("command", OptionMapping::getAsString).split("\\s+")[0];
        if (!SlashCommandHandler.commandsActionCommands.containsKey(command)) {
            event.reply("Der Befehl wurde nicht gefunden.").setEphemeral(true).queue();
            return;
        }

        SyntaxBuilder builder = new SyntaxBuilder(SlashCommandHandler.commandsActionCommands.get(command));
        event.replyEmbeds(builder.build()).queue();
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("help", "Zeigt genauere Beschreibungen für Befehle.")
                .addOption(OptionType.STRING, "command", "Der Befehl", true);
    }
}
