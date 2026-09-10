package dsb.sunny.command.commands.admin;

import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReactionRolesCommand implements SlashCommand {

    private static final Logger LOG = LoggerFactory.getLogger("ReactionRolesCommand");

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (!event.getMember().getPermissions().contains(Permission.MANAGE_ROLES)) {
            event.reply("Dir fehlt die Berechtigung \"Rollen verwalten\", und kannst demnach diesen Befehl nicht nutzen.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Role role = event.getOption("role").getAsRole();
        if (!event.getMember().canInteract(role)) {
            event.reply("Die Rolle, die du mir gegeben hast, ist höher als deine höchste Rolle, demnach kann ich diesen Befehl nicht korrekt ausführen.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String title = event.getOption("title").getAsString();
        String description = event.getOption("description").getAsString();
        String emote = event.getOption("emote").getAsString();

        event.reply("Die Reaction Role wurde erstellt!").setEphemeral(true).queue();
        event.getChannel()
                .sendMessageEmbeds(new EmbedBuilder()
                        .setTitle(title)
                        .setDescription(description)
                        .setColor(SunnySettings.GENERAL.color("color"))
                        .build())
                .addComponents(ActionRow.of(Button.secondary("giverole:" + role.getId(), Emoji.fromFormatted(emote))))
                .queue();

        LOG.info("Created new Reaction Role in #" + event.getChannel().getName());
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("reactionroles", "Erstelle eine ReactionRole.")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES))
                .addOption(OptionType.STRING, "title", "Der Titel", true)
                .addOption(OptionType.STRING, "description", "Die beschreibung", true)
                .addOption(OptionType.ROLE, "role", "Die Rolle", true)
                .addOption(OptionType.STRING, "emote", "Das Emote", true);
    }
}
