package dsb.sunny.command.commands.admin;

import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.selectionroles.SelectionRolesManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SelectionRolesCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            TextChannel channel = event.getOption("channel", OptionMapping::getAsChannel).asTextChannel();
            Message message = (event.getOption("messageid") != null
                    ? channel.retrieveMessageById(event.getOption("messageid", OptionMapping::getAsString)).complete()
                    : null);
            Role role = event.getOption("role", OptionMapping::getAsRole);
            String emote = event.getOption("emote", OptionMapping::getAsString);

            switch (event.getSubcommandName()) {
                case "create":
                    String title = event.getOption("title", OptionMapping::getAsString);
                    String description = event.getOption("description", OptionMapping::getAsString);
                    channel.sendMessageEmbeds(new EmbedBuilder()
                            .setTitle(title)
                            .setDescription(description)
                            .setColor(event.getGuild().getSelfMember().getColors().getPrimary())
                            .build())
                            .setComponents(
                                    ActionRow.of(StringSelectMenu.create("selectionroles")
                                            .setDisabled(true)
                                            .setPlaceholder("Es wurde keine Rolle hinzugefügt.")
                                            .addOption("Füge zuerst eine Rolle hinzu!", "dummy")
                                            .build()))
                            .queue();

                    event.reply("Das Menü wurde erstellt.").setEphemeral(true).queue();
                    break;
                case "add":
                    if (!event.getGuild().getSelfMember().canInteract(role)) {
                        event.reply(
                                "Ich kann mit der Rolle nicht interagieren. Sicher, dass die Rolle unter meiner eigenen ist?")
                                .queue();
                        return;
                    }

                    if (message == null) {
                        event.reply(
                                "https://media.discordapp.net/attachments/975740225613615175/1011951785780449320/unknown.png?width=971&height=657")
                                .queue();
                        return;
                    }

                    if (!SelectionRolesManager.hasSelectMenu(message)) {
                        event.reply("Die angegebene Nachricht hat kein SelectMenu.").queue();
                        return;
                    }

                {
                    SelectionRolesManager manager = new SelectionRolesManager(event.getGuild());
                    StringSelectMenu menu = manager.getSelectMenu(message);
                    SelectionRolesManager.SelectionRoleBuilder builder = manager.editSelectMenu(menu);

                    if (builder.addRole(role, emote)) {
                        event.reply("Die Rolle wurde erfolgreich zum Menü hinzugefügt!").queue();
                        message.editMessageComponents(ActionRow.of(builder.getSelectMenu())).queue();
                        return;
                    }

                    event.reply("Die Rolle wurde bereits zum Menü hinzugefügt.").queue();
                }
                    break;
                case "remove": {

                    if (!SelectionRolesManager.hasSelectMenu(message)) {
                        event.reply("Die angegebene Nachricht hat kein SelectMenu.").queue();
                        return;
                    }

                    SelectionRolesManager manager = new SelectionRolesManager(event.getGuild());
                    StringSelectMenu menu = manager.getSelectMenu(message);
                    SelectionRolesManager.SelectionRoleBuilder builder = manager.editSelectMenu(menu);

                    if (builder.removeRole(role)) {
                        event.reply("Die Rolle wurde erfolgreich vom Menü entfernt!").queue();
                        message.editMessageComponents(ActionRow.of(builder.getSelectMenu())).queue();
                        return;
                    }

                    event.reply("Die Rolle wurde nie zum Menü hinzugefügt.").queue();
                }
                    break;
                case "maxroles":

                    if (!SelectionRolesManager.hasSelectMenu(message)) {
                        event.reply("Die angegebene Nachricht hat kein SelectMenu.").queue();
                        return;
                    }

                {
                    SelectionRolesManager manager = new SelectionRolesManager(event.getGuild());
                    StringSelectMenu menu = manager.getSelectMenu(message);
                    SelectionRolesManager.SelectionRoleBuilder builder = manager.editSelectMenu(menu);

                    int maxValue = event.getOption("maxroles", OptionMapping::getAsInt);
                    if (maxValue < 1) {
                        event.reply("Sehr originell. :clown:").queue();
                        return;
                    }

                    if (maxValue > 25) {
                        event.reply("Du kannst nicht mehr als **25** Rollen auswählbar machen.").queue();
                        return;
                    }

                    builder.setMaxValues(maxValue);
                    message.editMessageComponents(ActionRow.of(builder.getSelectMenu())).queue();

                    event.reply("Mitglieder können nun nur noch maximal **" + maxValue + "** Rollen auswählen.")
                            .queue();
                }
                    break;
            }
        } else {
            event.reply("Du bist kein Turnierleiter und kannst daher diesen Befehl nicht nutzen.").setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("selectionroles", "Erstelle oder bearbeite SelectionRoles.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("create", "Erstellt eine Multiple Choice Reaction Role.")
                        .addOption(OptionType.CHANNEL, "channel", "Der Textkanal", true)
                        .addOption(OptionType.STRING, "title", "Der Titel", true)
                        .addOption(OptionType.STRING, "description", "Die Beschreibung", true))

                .addSubcommands(new SubcommandData("add", "Fügt eine neue Rolle zu einem Menü hinzu.")
                        .addOption(OptionType.CHANNEL, "channel", "Der Textkanal", true)
                        .addOption(OptionType.STRING, "messageid", "Die Nachrichtenid", true)
                        .addOption(OptionType.ROLE, "role", "Die Rolle", true)
                        .addOption(OptionType.STRING, "emote", "Das Emote", true))

                .addSubcommands(new SubcommandData("remove", "Entfernt eine Rolle von einem Menü.")
                        .addOption(OptionType.CHANNEL, "channel", "Der Textkanal", true)
                        .addOption(OptionType.STRING, "messageid", "Die Nachrichtenid", true)
                        .addOption(OptionType.ROLE, "role", "Die Rolle", true))

                .addSubcommands(new SubcommandData("maxroles", "Beschränkt die Auswahlmöglichkeiten eines Menüs.")
                        .addOption(OptionType.CHANNEL, "channel", "Der Textkanal", true)
                        .addOption(OptionType.STRING, "messageid", "Die Nachrichtenid", true)
                        .addOption(OptionType.INTEGER, "maxroles", "Die maximale Anzahl", true));
    }
}
