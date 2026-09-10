package dsb.sunny.command.commands.user;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.embeds.StandardEmbeds;
import dsb.sunny.gen.map.MapListGenerator;
import dsb.sunny.gen.map.stage.Stage;
import dsb.sunny.mappool.MapPool;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Objects;

public class MapPoolCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        MapListGenerator generator = DiscordBot.getMapListGenerator();
        MapPool mapPool = generator.getMapPool();

        int type = Objects.requireNonNullElse(event.getOption("language", OptionMapping::getAsInt), 1);
        EmbedBuilder eb = StandardEmbeds.mapPool(mapPool, type);
        if (eb != null) {
            eb.setAuthor("Deutsche Splatoon Bundesliga", null, event.getGuild().getIconUrl())
                    .setFooter("Sunny (DSB)", event.getJDA().getSelfUser().getEffectiveAvatarUrl());

            event.replyEmbeds(eb.build()).setSuppressedNotifications(true).queue();
        } else {
            event.reply("Es gibt momentan kein Map-Pool.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        OptionData language = new OptionData(OptionType.INTEGER, "language", "Die Sprache der Maps und Modis")
                .addChoice("Deutsch", Stage.GERMAN)
                .addChoice("Englisch", Stage.ENGLISH)
                .addChoice("Beides", Stage.BOTH);

        return Commands.slash("mappool", "Schaue dir den Map-Pool der DSB an.")
                .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
                .addOptions(language);
    }
}
