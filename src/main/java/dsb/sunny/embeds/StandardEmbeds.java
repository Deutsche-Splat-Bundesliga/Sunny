package dsb.sunny.embeds;

import dsb.sunny.gen.map.combination.MapModeCombination;
import dsb.sunny.gen.map.mode.GameMode;
import dsb.sunny.gen.map.stage.Stage;
import dsb.sunny.mappool.MapPool;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.OffsetDateTime;
import java.util.List;

public class StandardEmbeds {

    public static EmbedBuilder mapPool(MapPool mapPool, int type) {
        if (mapPool.pool().isEmpty()) {
            return null;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Deutsche Splatoon Bundesliga")
                .setColor(SunnySettings.GENERAL.color("color"))
                .setTitle(mapPool.title())
                .setTimestamp(OffsetDateTime.now());

        for (GameMode gameMode : mapPool.pool().keySet()) {
            String gameModeName = switch (type) {
                case Stage.GENERIC -> gameMode.name();
                case Stage.GERMAN -> gameMode.getDe();
                case Stage.ENGLISH -> gameMode.getEn();
                case Stage.BOTH -> gameMode.getDe() + " / " + gameMode.getEn();
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
            StringBuilder sb = new StringBuilder();
            List<String> stages = mapPool.getNames(gameMode, type);
            for (String stage : stages) {
                sb.append(stage).append("\n");
            }

            eb.addField("%s __**%s**__".formatted(gameMode.getEmoji().getFormatted(), gameModeName), sb.toString(), false);
        }
        return eb;
    }

    public static EmbedBuilder mapList(String type, List<MapModeCombination> maps, int lang) {
        String title = switch (type) {
            case "zonesonly" -> "Nur Herrschaft / Zones Only";
            case "equally" -> "Ausgewogen gleiche Modi-Abfolge / Equally-fixed modes";
            case "equallyrandom" -> "Ausgewogen zufällige Modi-Abfolge / Zones Only";
            case "randomseinurgrossvater" -> "Viel Spaß. / Have fun.";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maps.size(); i++) {
            sb.append("%d) ".formatted(i+1)).append(maps.get(i).getFormatted(lang)).append("\n");
        }

        return new EmbedBuilder()
                .setColor(SunnySettings.GENERAL.color("color"))
                .setTitle(title)
                .setDescription(sb.toString())
                .setTimestamp(OffsetDateTime.now());
    }
}
