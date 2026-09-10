package dsb.sunny.constellations.tier;

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum WeaponTier {
    GOD(Emoji.fromUnicode("😎"), "God"),
    GOOD(Emoji.fromUnicode("😄"), "Good"),
    MID(Emoji.fromUnicode("😶"), "Mid"),
    BAD(Emoji.fromUnicode("😒"), "Bad");

    private final Emoji emoji;
    private final String label;

    WeaponTier(Emoji emoji, String label) {
        this.emoji = emoji;
        this.label = label;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public String getEmojiFormatted() {
        return emoji.getFormatted();
    }

    public String getLabel() {
        return label;
    }
}
