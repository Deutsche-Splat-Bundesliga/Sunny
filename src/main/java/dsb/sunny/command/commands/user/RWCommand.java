package dsb.sunny.command.commands.user;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.gen.weapon.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Random;


public class RWCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        RandomWeaponGenerator randomWeaponGenerator = DiscordBot.getRandomWeaponGenerator();

        OptionMapping amountOption = event.getOption("amount");
        String parameter = event.getOption("parameters", OptionMapping::getAsString);
        int amount = amountOption != null ? amountOption.getAsInt() : 1;

        ArrayList<Weapon> listWeapons = new ArrayList<>();
        if (event.getSubcommandName() != null && !event.getSubcommandName().equalsIgnoreCase("all")) {
            for (int i = 0; i < amount; i++) {
                Weapon newWeapon = randomWeaponGenerator.generateRandomWeapon(
                        randomWeaponGenerator.convertParameter(parameter));
                if (newWeapon == null) {
                    event.reply("Aus irgendwelchen Gründen auch immer konnte ich keine Waffe generieren...")
                            .setEphemeral(true)
                            .queue();
                    break;
                }
                listWeapons.add(newWeapon);
            }
        } else {
            for (int i = 0; i < amount; i++) {
                listWeapons.add(randomWeaponGenerator.generateRandomWeapon());
            }
        }

        MessageEmbed embed = buildEmbed(event.getUser(), listWeapons);
        if (embed == null) {
            event.reply("Aus irgendwelchen Gründen auch immer konnte ich keine Waffe generieren...")
                    .setEphemeral(true)
                    .queue();
        }

        String label = listWeapons.size() > 1 ? "Neue Waffen" : "Neue Waffe";
        Button rerollButton = Button.secondary(String.format("%s:RW:%s;%s", event.getUser().getId(), parameter, amount), label)
                .withEmoji(Emoji.fromUnicode("🔄"));

        event.replyEmbeds(embed)
                .setSuppressedNotifications(true)
                .addComponents(ActionRow.of(rerollButton))
                .queue();
    }

    private MessageEmbed buildEmbed(User issuedBy, ArrayList<Weapon> listWeapons) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Zufällige Waffe!", null, "https://cdn.discordapp.com/attachments/944399291915571201/1017462604463095859/ShopMiniIcon_01w.png")
                .setColor(new Random().nextInt(0xFFFFFF + 1))
                .setFooter(String.format("Ausgeführt von %s", issuedBy.getName()), issuedBy.getEffectiveAvatarUrl())
                .setTimestamp(OffsetDateTime.now());
        switch (listWeapons.size()) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                Weapon polled = listWeapons.get(0);
                eb.setTitle(polled.getFormattedDe())
                        .setImage(polled.getPictureUrl());
            }
            default -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < listWeapons.size(); i++) {
                    if (i % 4 == 0) {
                        sb.append("\n");
                    }
                    sb.append(String.format("%d) %s", i + 1, listWeapons.get(i).getFormattedDe())).append("\n");
                }
                eb.setTitle("Deine gezogenen Waffen:")
                        .setDescription(sb.toString());
            }
        }
        return eb.build();
    }

    @Override
    public CommandData commandData() {
        OptionData typeParameters = new OptionData(OptionType.STRING, "parameters", "Name des Waffentyps:", true);
        OptionData subParameters = new OptionData(OptionType.STRING,"parameters", "Name der Sub-Waffe:", true);
        OptionData specialParameters = new OptionData(OptionType.STRING, "parameters", "Name der Spezialwaffe:", true);
        OptionData amount = new OptionData(OptionType.INTEGER, "amount", "Die Menge der Waffen, maximal 50.", false)
                .setRequiredRange(1, 50);
        for (WeaponType type : WeaponType.values()) typeParameters.addChoice(type.getName(), type.name());
        for (SubWeapon subWeapon : SubWeapon.values()) subParameters.addChoice(subWeapon.getNameDe(), subWeapon.name());
        for (SpecialWeapon specialWeapon : SpecialWeapon.values()) specialParameters.addChoice(specialWeapon.getNameDe(), specialWeapon.name());

        return Commands.slash("rw", "Generiere eine oder mehrere zufällige Waffen aus Splatoon 3.")
                .addSubcommands(new SubcommandData("all", "Generiere eine beliebige Waffe aus Splatoon 3.")
                        .addOptions(amount))
                .addSubcommands(new SubcommandData("type", "Generiere eine zufällige Waffe mit einem bestimmten Waffentyp.")
                        .addOptions(typeParameters).addOptions(amount))
                .addSubcommands(new SubcommandData("sub", "Generiere eine zufällige Waffe mit einer bestimmten Subwaffe.")
                        .addOptions(subParameters).addOptions(amount))
                .addSubcommands(new SubcommandData("special", "Generiere eine zufällige Waffe mit einer bestimmten Spezialwaffe.")
                        .addOptions(specialParameters).addOptions(amount));
    }
}