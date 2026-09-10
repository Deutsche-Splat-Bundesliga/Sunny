package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.constellations.WeaponConstellations;
import dsb.sunny.constellations.roll.RolledConstellation;
import dsb.sunny.constellations.tier.WeaponTier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.sql.SQLException;

public class InkstellationsCommand implements SlashCommand {
    public static final String COMMAND_NAME = "inkstellations";

    public WeaponConstellations constellations;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (constellations == null) {
            constellations = DiscordBot.getConstellations();
        }

        String sub = event.getSubcommandName();

        switch (sub) {
            case "roll" -> onRoll(event);
            case "set" -> onSet(event);
            case "remove" -> onRemove(event);
            default -> throw new IllegalArgumentException("unknown sub command: " + sub);
        }
    }

    private void onRemove(SlashCommandInteractionEvent event) throws SQLException {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            informMissingPerms(event);
            return;
        }

        int weaponIndex = event.getOption("weapon", OptionMapping::getAsInt);

        constellations.removeWeapon(weaponIndex);
        event.reply("Die Waffe wurde entfernt!")
                .setEphemeral(true)
                .queue();
    }

    private void onSet(SlashCommandInteractionEvent event) throws SQLException {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            informMissingPerms(event);
            return;
        }

        WeaponTier tier = WeaponTier.valueOf(event.getOption("tier", OptionMapping::getAsString));
        int weaponIndex = event.getOption("weapon", OptionMapping::getAsInt);

        constellations.addWeapon(weaponIndex, tier);
        event.reply("Die Waffe wurde als \"**%s**\"-Tier eingestuft!".formatted(tier.getLabel()))
                .setEphemeral(true)
                .queue();
    }

    private void informMissingPerms(SlashCommandInteractionEvent event) {
        event.reply("Nur Turnierleiter dürfen diesen Subcommand ausführen.")
                .setEphemeral(true)
                .queue();
    }

    private void onRoll(SlashCommandInteractionEvent event) {
        try {
            int amount = 1;
            OptionMapping option = event.getOption("amount");
            if (option != null) {
                amount = option.getAsInt();
            }

            EmbedBuilder ebDe = new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor("DSSQ: Inkstellations")
                    .setTitle("Deine gerollten Konstellationen:");

            EmbedBuilder ebEn = new EmbedBuilder()
                    .setColor(Color.YELLOW)
                    .setAuthor("DSSQ: Inkstellations")
                    .setTitle("Your rolled constellations:");

            for (int i = 0; i < amount; i++) {
                RolledConstellation rolled = constellations.rollConstellation();
                ebDe.addField("Inkstellation #%d:".formatted(i + 1), rolled.getFormattedDe(), false);
                ebEn.addField("Inkstellation #%d:".formatted(i + 1), rolled.getFormattedEn(), false);
            }

            event.replyEmbeds(ebDe.build(), ebEn.build()).queue();
        } catch (IllegalStateException e) {
            event.reply("Die Konstellationen wurden noch nicht richtig eingestellt.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public CommandData commandData() {
        OptionData tiers = new OptionData(OptionType.STRING, "tier", "Der Tier", true);
        OptionData weapon = new OptionData(OptionType.INTEGER, "weapon", "Die Waffe", true, true);
        for (WeaponTier value : WeaponTier.values()) {
            tiers.addChoice(value.getLabel(), value.name());
        }

        return Commands.slash(COMMAND_NAME, "DSSQ: Inkstellations")
                .addSubcommands(new SubcommandData("roll", "Rolle ein oder mehrere Konstellationen.")
                        .addOptions(new OptionData(OptionType.INTEGER, "amount", "Die Anzahl der Konstellationen, die gerollt werden sollen")
                                .setRequiredRange(1, 3)))

                .addSubcommands(new SubcommandData("set", "Füge eine neue Waffe einer Tier hinzu oder verändere sie.")
                        .addOptions(tiers, weapon))

                .addSubcommands(new SubcommandData("remove", "Entferne eine Waffe.")
                        .addOptions(weapon))

                .addSubcommands(new SubcommandData("test", "Test"));
    }
}
