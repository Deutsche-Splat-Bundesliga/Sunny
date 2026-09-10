package dsb.sunny.gen.map.mode;

import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum GameMode {
    TURF(Emoji.fromCustom("turf_war", 997212946256105602L, false), "Turf War", "Revierkampf"),
    ZONES(Emoji.fromCustom("SZ", 1015019419077980250L, false), "Herrschaft", "Splat Zones"),
    TOWER(Emoji.fromCustom("TC", 1015019423062560848L, false), "Turmkommando", "Tower Control"),
    RAIN(Emoji.fromCustom("RM", 1015019421590372453L, false), "Operation Goldfisch", "Rainmaker"),
    CLAM(Emoji.fromCustom("CB", 1015019420348846081L, false), "Muschelchaos", "Clam Blitz");

    private final CustomEmoji emoji;
    private final String de;
    private final String en;

    GameMode(CustomEmoji emoji, String de, String en) {
        this.emoji = emoji;
        this.de = de;
        this.en = en;
    }

    public CustomEmoji getEmoji() {
        return emoji;
    }

    @Override
    public String toString() {
        return emoji.getFormatted() + " ";
    }

    public String getDe() {
        return de;
    }

    public String getEn() {
        return en;
    }
}
