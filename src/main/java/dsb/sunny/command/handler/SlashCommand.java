package dsb.sunny.command.handler;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface SlashCommand {

	void handle(SlashCommandInteractionEvent event) throws Exception;
	CommandData commandData();

}
