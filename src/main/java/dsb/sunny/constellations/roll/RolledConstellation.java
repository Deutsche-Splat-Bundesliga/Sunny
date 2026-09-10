package dsb.sunny.constellations.roll;

import dsb.sunny.constellations.pair.TierWeaponPair;

import java.util.List;
import java.util.stream.Collectors;

public class RolledConstellation {
    private final List<TierWeaponPair> pairs;

    public RolledConstellation(List<TierWeaponPair> pairs) {
        if (pairs.size() != 4) {
            throw new IllegalArgumentException("pairs must be a size of 4");
        }

        if (pairs.stream().distinct().count() != 4) {
            throw new IllegalArgumentException("pairs cannot have the same weapon");
        }

        this.pairs = List.copyOf(pairs);
    }

    @Override
    public String toString() {
        return pairs.stream().map(p -> "\n" + p)
                .collect(Collectors.joining())
                .substring(1);
    }

    public String getFormattedDe() {
        StringBuilder sb = new StringBuilder();
        for (TierWeaponPair pair : pairs) {
            sb.append("\n").append(pair.getFormattedDe());
        }
        return sb.substring(1);
    }

    public String getFormattedEn() {
        StringBuilder sb = new StringBuilder();
        for (TierWeaponPair pair : pairs) {
            sb.append("\n").append(pair.getFormattedEn());
        }
        return sb.substring(1);
    }
}
