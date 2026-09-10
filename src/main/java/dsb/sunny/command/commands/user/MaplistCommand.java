package dsb.sunny.command.commands.user;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.embeds.StandardEmbeds;
import dsb.sunny.gen.map.MapListGenerator;
import dsb.sunny.gen.map.combination.MapModeCombination;
import dsb.sunny.gen.map.mode.GameMode;
import dsb.sunny.gen.map.stage.Stage;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.*;

public class MaplistCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        MapListGenerator gen = DiscordBot.getMapListGenerator();
        String type = Objects.requireNonNull(event.getSubcommandName());
        int amount = event.getOption("size", OptionMapping::getAsInt);

        if (type.equalsIgnoreCase("custom")) {
            customSequenceBuilder(event);
            return;
        }

        List<GameMode> modes = switch (type) {
            case "zonesonly" -> List.of(GameMode.ZONES);
            case "equally" -> {
                List<GameMode> l = new LinkedList<>(Arrays.asList(GameMode.values()));
                Collections.shuffle(l);
                yield l;
            }
            case "equallyrandom", "randomseinurgrossvater" -> {
                boolean totallyRandom = "randomseinurgrossvater".equalsIgnoreCase(type);
                List<GameMode> l = new LinkedList<>();
                while (l.size() < amount) {
                    List<GameMode> ll = new LinkedList<>(Arrays.asList(GameMode.values()));
                    Collections.shuffle(ll);
                    l.addAll(totallyRandom ? ll.subList(0, 1) : ll);
                }
                yield l.subList(0, amount);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        List<MapModeCombination> generated = gen.generateMaps(modes, amount);

        if (generated != null) {
            event.replyEmbeds(StandardEmbeds.mapList(type, generated, Stage.BOTH)
                    .setFooter("Maplist generiert von %s".formatted(event.getUser().getName()))
                    .setAuthor("Deutsche Splatoon Bundesliga", null, event.getGuild().getIconUrl())
                    .build()).queue();
        } else {
            event.reply("Ich konnte leider keine Maps generieren.").setEphemeral(true).queue();
        }
    }

    private void customSequenceBuilder(SlashCommandInteractionEvent event) {
        event.reply("🚧 Dieser Command wurde noch nicht implementiert. Wir bitten um Geduld.").setEphemeral(true).queue();
    }

    @Override
    public CommandData commandData() {
        OptionData size = new OptionData(OptionType.INTEGER, "size", "Die Menge der Maps, maximal 50.", true)
                .setRequiredRange(1, 50);

        return Commands.slash("maplist", "Generiere eine zufällige Maplist aus dem DSB-Mappool.")
                .addSubcommands(new SubcommandData("zonesonly", "Generiert nur Zones-Maps.")
                        .addOptions(size))
                .addSubcommands(new SubcommandData("equally", "Eine feste Abfolge von Modis werden wiederholt.")
                        .addOptions(size))
                .addSubcommands(new SubcommandData("equallyrandom", "Sobald 4 Modis gespielt worden sind, wird neu gewürfelt.")
                        .addOptions(size))
                .addSubcommands(new SubcommandData("randomseinurgrossvater", "Generiert einfach drauf los. Warum auch immer man das machen will.")
                        .addOptions(size));
    }
}
