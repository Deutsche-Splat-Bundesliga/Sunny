package dsb.sunny.embeds;

import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.awt.Color;
import java.time.OffsetDateTime;

/**
 * Central helpers for building Discord embeds in the DSB house style.
 * <p>
 * Every factory method returns a fresh, already timestamped {@link EmbedBuilder} that the caller
 * keeps configuring with the regular JDA fluent API before calling {@link EmbedBuilder#build()}.
 * The point is to stop copy-pasting the same {@code setColor(...) / setTimestamp(...) / setTitle(...)}
 * prelude into every command, listener and module.
 * <p>
 * Domain specific embeds that are shared between a handful of related call sites (map pools,
 * score reports, …) live in their own {@code *Embeds} classes next to this one and build on top
 * of these primitives.
 */
public final class Embeds {

    /** Fallback colour (DSB red) used when a member or role carries no colour of its own. */
    public static final Color FALLBACK = new Color(0xC5003D);
    /** Colour for "operation succeeded" embeds. */
    public static final Color SUCCESS = new Color(0x00FF33);
    /** Colour for "operation failed / rejected" embeds. */
    public static final Color ERROR = new Color(0xFF013C);

    private static final String SLASH_EMOTE = "<:slash:998986781938679930>";

    private Embeds() {
    }

    /** Bare embed carrying only the current timestamp. */
    public static EmbedBuilder plain() {
        return new EmbedBuilder().setTimestamp(OffsetDateTime.now());
    }

    /** Timestamped embed tinted with an explicit colour. */
    public static EmbedBuilder colored(Color color) {
        return plain().setColor(color);
    }

    /** DSB house style: the configured general brand colour plus a timestamp. */
    public static EmbedBuilder standard() {
        return colored(SunnySettings.GENERAL.color("color"));
    }

    /** {@link #standard()} with the "Deutsche Splatoon Bundesliga" author line on top. */
    public static EmbedBuilder branded() {
        return standard().setAuthor("Deutsche Splatoon Bundesliga");
    }

    /** Timestamped embed tinted with the member's colour, or {@link #FALLBACK} when they have none. */
    public static EmbedBuilder byMember(Member member) {
        return colored(colorOf(member));
    }

    /** Timestamped embed tinted with the role's colour (e.g. a division role). */
    public static EmbedBuilder byRole(Role role) {
        return colored(role.getColors().getPrimary());
    }

    /** Green, timestamped embed with the given author line, for success confirmations. */
    public static EmbedBuilder success(String author) {
        return colored(SUCCESS).setAuthor(author);
    }

    /** Red, timestamped embed with the given author line, for failures and rejections. */
    public static EmbedBuilder error(String author) {
        return colored(ERROR).setAuthor(author);
    }

    /**
     * Skeleton of a changelog entry: the acting member's colour, a
     * {@code "<slash emote> <action> ausgeführt"} title and a "Member" field identifying the actor.
     * Callers append the command specific fields before sending it to the changelog channel.
     *
     * @param actor  the member who triggered the action
     * @param action the action label, e.g. {@code "Befehl"} or {@code "Report-Command"}
     */
    public static EmbedBuilder changelog(Member actor, String action) {
        return byMember(actor)
                .setTitle(SLASH_EMOTE + " " + action + " ausgeführt")
                .addField("Member", actor.getUser().getName() + " " + actor.getUser().getAsMention(), false);
    }

    /** The member's primary colour, or {@link #FALLBACK} when the member has none. */
    public static Color colorOf(Member member) {
        Color primary = member.getColors().getPrimary();
        return primary != null ? primary : FALLBACK;
    }

    /** Wraps a value in Discord inline-code markup, for use as a field value. */
    public static String code(Object value) {
        return "`" + value + "`";
    }
}
