package dsb.sunny.constellations.pair;

import dsb.sunny.constellations.tier.WeaponTier;
import dsb.sunny.gen.weapon.Weapon;
import org.jspecify.annotations.NonNull;

public record TierWeaponPair(WeaponTier tier, Weapon weapon) {
    @Override
    public @NonNull String toString() {
        return tier.getLabel() + ": " + weapon.getFormattedDe();
    }

    public String getFormattedDe() {
        return tier.getEmojiFormatted() + " " + weapon.getFormattedDe();
    }

    public String getFormattedEn() {
        return tier.getEmojiFormatted() + " " + weapon.getFormattedEn();
    }
}
