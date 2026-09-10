package dsb.sunny.command.handler;

import dsb.sunny.command.commands.admin.*;
import dsb.sunny.command.commands.uncategorized.HelpCommand;
import dsb.sunny.command.commands.user.*;
import dsb.sunny.enums.ChannelReferences;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;

public class SlashCommandHandler extends ListenerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger("SlashCommandHandler");
	public static HashMap<String, SlashCommand> slashCommands = new HashMap<>();
	public static HashMap<String, Command> commandsActionCommands = new HashMap<>();

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		initializeCommands(event.getJDA(), true);
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) {
			event.reply(String.format("Bitte nutze die Befehle in %s.", ChannelReferences.BOT_CHANNEL)).queue();
			return;
		}

		SlashCommand cmd = slashCommands.get(event.getName());
		try {
			cmd.handle(event);

			String changeLogChannelId = SunnySettings.CHANGELOG.string("channel");
			if (changeLogChannelId == null) {
				LOG.error("changelogchannel can not be retrieved: null");
				return;
			}

			TextChannel changelogChannel = event.getGuild().getTextChannelById(changeLogChannelId);
			if (changelogChannel == null) {
				LOG.error("Changelog channel does not exist");
				return;
			}

			if (!changelogChannel.canTalk()) {
				LOG.error("Cannot send messages in changelog channel.");
				return;
			}

			MessageEmbed eb = new EmbedBuilder()
					.setColor(event.getMember().getColor() != null ? event.getMember().getColor() : new Color(0xC5003D))
					.setTitle("<:slash:998986781938679930> Befehl ausgeführt")
					.addField("Member", event.getUser().getName() + " " + event.getUser().getAsMention(), false)
					.addField("Befehl", "`" + event.getCommandString() + "`", false)
					.setTimestamp(OffsetDateTime.now())
					.build();
			changelogChannel.sendMessageEmbeds(eb).queue();

			LOG.info("Member {} executed command '{}' - full command: {}", event.getMember().getUser().getName(), event.getFullCommandName(), event.getCommandString());
		} catch(Exception ex) {
			if (ex instanceof InsufficientPermissionException permissionException) {
				String permission = "Missing following permission to properly execute this command: **%s**"
						.formatted(permissionException.getPermission().getName());

				if (event.isAcknowledged()) {
					event.getHook().editOriginal(permission).setReplace(true).queue();
					return;
				}
				event.reply(permission).setEphemeral(true).queue();
				return;
			}

			LOG.error("Could not process command", ex);
			event.reply("An internal error occurred, please check the console logs for more information.")
					.setEphemeral(true)
					.queue();
		}
	}

	public void initializeCommands(JDA jda, boolean update) {
		slashCommands.put("help", new HelpCommand());
		slashCommands.put("reactionroles", new ReactionRolesCommand());
		slashCommands.put("report", new ReportCommand());
		slashCommands.put("divisions", new DivisionsCommand());
		slashCommands.put("nowrite", new NoWriteRoleCommand());
		//slashCommands.put("mvp", new MVPCommand());
		//slashCommands.put("adminmvp", new AdminMVPCommand());
		slashCommands.put("givecaprole", new GiveCapRoleCommand());
		slashCommands.put("drop", new DropCommand());
		slashCommands.put("selectionroles", new SelectionRolesCommand());
		slashCommands.put("mappool", new MapPoolCommand());
		slashCommands.put("setmappool", new SetMapPoolCommand());
		slashCommands.put("maplist", new MaplistCommand());
		slashCommands.put("rw", new RWCommand());
		slashCommands.put("settings", new SettingsCommand());
		slashCommands.put("editscore", new EditScoreCommand());
		// slashCommands.put("looking4", new LookingForCommand()); // TODO: 08.09.2022 Cyo: Muss noch Channels einstellen und Team-Modal machen
		slashCommands.put("cleaner", new CleanerCommand());
		slashCommands.put("remindme", new ReminderCommand());
		slashCommands.put("cocap", new CoCaptainCommand());
        slashCommands.put("scenario", new ScenarioCommand());
		slashCommands.put("inkstellations", new InkstellationsCommand());

		CommandListUpdateAction commandsAction = jda.updateCommands();
		for (SlashCommand cmd : slashCommands.values()) {
			commandsAction.addCommands(cmd.commandData());
		}
		
		if (update) {
			commandsAction.queue(success -> {
				success.forEach(command -> commandsActionCommands.put(command.getName(), command));
				LOG.info("Synchronized Slash-Commands!");
			});
		}
	}
}
