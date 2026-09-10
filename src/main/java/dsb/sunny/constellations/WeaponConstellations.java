package dsb.sunny.constellations;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.commands.admin.InkstellationsCommand;
import dsb.sunny.constellations.pair.TierWeaponPair;
import dsb.sunny.constellations.roll.RolledConstellation;
import dsb.sunny.constellations.tier.WeaponTier;
import dsb.sunny.gen.weapon.RandomWeaponGenerator;
import dsb.sunny.gen.weapon.Weapon;
import dsb.sunny.settings.SunnySettings;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class WeaponConstellations extends ListenerAdapter {
    private static final RandomWeaponGenerator WEAPONS = new RandomWeaponGenerator();
    private static final SunnySettings SETTINGS = SunnySettings.CONSTELLATIONS;
    private static final Random RNG = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(WeaponConstellations.class);

    private final Map<WeaponTier, List<Weapon>> weaponTierMap;

    public WeaponConstellations() {
        this.weaponTierMap = new HashMap<>();

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS WeaponConstellations (
                        WeaponIndex INTEGER NOT NULL,
                        Tier TEXT NOT NULL,
                    
                        PRIMARY KEY (WeaponIndex)
                    );""");

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM WeaponConstellations");
            while (rs.next()) {
                Weapon weapon = WEAPONS.getWeapons().get(rs.getInt("WeaponIndex"));
                WeaponTier tier = WeaponTier.valueOf(rs.getString("Tier"));

                weaponTierMap.computeIfAbsent(tier, k -> new ArrayList<>()).add(weapon);
            }
        } catch (SQLException ex) {
            LOG.error("failed to init data: ", ex);
        }

        if (!"1".equals(SETTINGS.string("init"))) {
            init();
        }
    }

    private void init() {
        // all weapons in predefined constellations, see #team-inklingonfire
        LOG.info("init constellations data, this will take some time...");
        try {
            Map<String, WeaponTier> m = new HashMap<>();
                    m.put("Kleckser", WeaponTier.GOD);
                    m.put("Fein-Disperser", WeaponTier.GOD);
                    m.put("Fein-Disperser Neo", WeaponTier.GOD);
                    m.put(".52 Gallon", WeaponTier.GOD);
                    m.put(".52 Gallon Deko", WeaponTier.GOD);
                    m.put("N-ZAP85", WeaponTier.GOD);
                    m.put("Focus-Profi-Kleckser", WeaponTier.GOD);
                    m.put("Profi-Kleckser CH-LL", WeaponTier.GOD);
                    m.put("Turbo-Blaster Plus WNT-R", WeaponTier.GOD);
                    m.put("S-BLAST91", WeaponTier.GOD);
                    m.put("Karbonroller Deko", WeaponTier.GOD);
                    m.put("Klecksroller", WeaponTier.GOD);
                    m.put("R-BLR/5H", WeaponTier.GOD);
                    m.put("Schwapper", WeaponTier.GOD);
                    m.put("Schwappwascher", WeaponTier.GOD);
                    m.put("Klecks-Splatling KLP-R", WeaponTier.GOD);
                    m.put("Klecks-Doppler", WeaponTier.GOD);
                    m.put("Stempel-Splatana", WeaponTier.GOD);
                    m.put("Stempel-Splatana Sticker", WeaponTier.GOD);
                    m.put("Minz-Prophylator", WeaponTier.GOD);
                    m.put("Kartuschierer", WeaponTier.GOD);
                    m.put("Junior-Kleckser", WeaponTier.GOOD);
                    m.put("Tentatek-Kleckser", WeaponTier.GOOD);
                    m.put("Chroma-Kleckser", WeaponTier.GOOD);
                    m.put("Profi-Kleckser", WeaponTier.GOOD);
                    m.put(".96 Gallon Deko", WeaponTier.GOOD);
                    m.put(".96 Gallon Leo", WeaponTier.GOOD);
                    m.put("Quetscher", WeaponTier.GOOD);
                    m.put("Platscher", WeaponTier.GOOD);
                    m.put("Luna-Blaster Neo", WeaponTier.GOOD);
                    m.put("Blaster SE", WeaponTier.GOOD);
                    m.put("Fern-Blaster SE", WeaponTier.GOOD);
                    m.put("Turbo-Blaster Plus", WeaponTier.GOOD);
                    m.put("Turbo-Blaster Plus Deko", WeaponTier.GOOD);
                    m.put("S-BLAST92", WeaponTier.GOOD);
                    m.put("Karbonroller ANG-LR", WeaponTier.GOOD);
                    m.put("Quasto Fresco", WeaponTier.GOOD);
                    m.put("Kalligraf", WeaponTier.GOOD);
                    m.put("Kalligraf Fresco", WeaponTier.GOOD);
                    m.put("Fächerfärber BRN-Z", WeaponTier.GOOD);
                    m.put("Klecks-Konzentrator", WeaponTier.GOOD);
                    m.put("Ziel-Konzentrator", WeaponTier.GOOD);
                    m.put("E-liter 4K", WeaponTier.GOOD);
                    m.put("Ziel-E-liter 4K", WeaponTier.GOOD);
                    m.put("3R-Schwapper Fresco", WeaponTier.GOOD);
                    m.put("Trommel-Schwapper Neo", WeaponTier.GOOD);
                    m.put("Schwappwascher Horn", WeaponTier.GOOD);
                    m.put("Klecks-Splatling", WeaponTier.GOOD);
                    m.put("Sagitron-Klecks-Splatling", WeaponTier.GOOD);
                    m.put("Splatling", WeaponTier.GOOD);
                    m.put("Nautilus 47", WeaponTier.GOOD);
                    m.put("Nautilus 79", WeaponTier.GOOD);
                    m.put("Kartuschierer Fresco", WeaponTier.GOOD);
                    m.put("Sprenkler", WeaponTier.GOOD);
                    m.put("Sprenkler Fresco", WeaponTier.GOOD);
                    m.put("Enperry-Klecks-Doppler", WeaponTier.GOOD);
                    m.put("Glitz-Klecks-Doppler", WeaponTier.GOOD);
                    m.put("Dual-Platscher", WeaponTier.GOOD);
                    m.put("Dual-Platscher ZBR", WeaponTier.GOOD);
                    m.put("Quadhopper Noir", WeaponTier.GOOD);
                    m.put("Stempel-Splatana Fresco", WeaponTier.GOOD);
                    m.put("Wischer-Splatana", WeaponTier.GOOD);
                    m.put("Carbo-Prophylator", WeaponTier.GOOD);
                    m.put("Disperser Neo", WeaponTier.BAD);
                    m.put("Airbrush RG", WeaponTier.BAD);
                    m.put("Airbrush RGB", WeaponTier.BAD);
                    m.put("Kosmo-Kleckser", WeaponTier.BAD);
                    m.put("Annaki Kosmo-Kleckser", WeaponTier.BAD);
                    m.put("Kontra-Blaster", WeaponTier.BAD);
                    m.put("Kontra-Blaster Neo", WeaponTier.BAD);
                    m.put("L3 Tintenwerfer", WeaponTier.BAD);
                    m.put("L3 Tintenwerfer D", WeaponTier.BAD);
                    m.put("S3 Tintenwerfer", WeaponTier.BAD);
                    m.put("S3 Tintenwerfer D", WeaponTier.BAD);
                    m.put("S3 Tintenwerfer VIP-R", WeaponTier.BAD);
                    m.put("Karbonroller", WeaponTier.BAD);
                    m.put("Dynaroller Tesla", WeaponTier.BAD);
                    m.put("Dynaroller Galaxa", WeaponTier.BAD);
                    m.put("Breitroller Express", WeaponTier.BAD);
                    m.put("Breitroller Exzell", WeaponTier.BAD);
                    m.put("Sepiator α", WeaponTier.BAD);
                    m.put("Sepiator β", WeaponTier.BAD);
                    m.put("T-Tuber", WeaponTier.BAD);
                    m.put("T-Tuber SE", WeaponTier.BAD);
                    m.put("Wannen-Schwapper", WeaponTier.BAD);
                    m.put("Hydrant SE", WeaponTier.BAD);
                    m.put("Kelvin 525", WeaponTier.BAD);
                    m.put("Kelvin 525 Deko", WeaponTier.BAD);
                    m.put("Lösch-Doppler FW", WeaponTier.BAD);
                    m.put("Lösch-Doppler FW SE", WeaponTier.BAD);
                    m.put("Parapluviator", WeaponTier.BAD);
                    m.put("Camp-Pluviator CR-MA", WeaponTier.BAD);
                    m.put("UnderCover", WeaponTier.BAD);
                    m.put("Sorella-UnderCover", WeaponTier.BAD);
                    m.put("Flair-UnderCover", WeaponTier.BAD);
                    m.put("Bast-Pluviator 24-A", WeaponTier.BAD);
                    m.put("Bast-Pluviator 24-B", WeaponTier.BAD);
                    m.put("Tri-Stringer", WeaponTier.BAD);
                    m.put("Alpomar-Tri-Stringer", WeaponTier.BAD);
                    m.put("LACT-450", WeaponTier.BAD);
                    m.put("LACT-450 Deko", WeaponTier.BAD);
                    m.put("LACT-450 Q-MLCH", WeaponTier.BAD);
                    m.put("Penta-Pumper SE", WeaponTier.BAD);

            for (Weapon weapon : WEAPONS.getWeapons()) {
                WeaponTier weaponTier = m.getOrDefault(weapon.getWeaponNameDe(), WeaponTier.MID);
                addWeapon(weapon, weaponTier);
            }
            SETTINGS.setValue("init", true);
        } catch (SQLException ex) {
            LOG.error("couldn't init data: ", ex);
        }
    }

    public void addWeapon(Weapon weapon, WeaponTier tier) throws SQLException {
        removeWeapon(weapon);

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO WeaponConstellations VALUES (?, ?)");
            ps.setInt(1, WEAPONS.getWeapons().indexOf(weapon));
            ps.setString(2, tier.name());
            ps.executeUpdate();

            weaponTierMap.computeIfAbsent(tier, t -> new ArrayList<>()).add(weapon);
        }
    }

    public void addWeapon(int weaponIndex, WeaponTier tier) throws SQLException {
        addWeapon(WEAPONS.getWeapons().get(weaponIndex), tier);
    }

    public void removeWeapon(int weaponIndex) throws SQLException {
        removeWeapon(WEAPONS.getWeapons().get(weaponIndex));
    }

    public void removeWeapon(Weapon weapon) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM WeaponConstellations WHERE WeaponIndex = ?;");
            ps.setInt(1, WEAPONS.getWeapons().indexOf(weapon));
            ps.executeUpdate();
        }

        weaponTierMap.forEach((tier, weapons) -> weapons.remove(weapon));
    }

    public RolledConstellation rollConstellation() throws IllegalStateException {
        List<TierWeaponPair> pairs = new ArrayList<>(4);
        if (weaponTierMap.isEmpty()) {
            throw new IllegalStateException("empty map");
        }

        for (Map.Entry<WeaponTier, List<Weapon>> entry : weaponTierMap.entrySet()) {
            List<Weapon> list = entry.getValue();
            if (list.isEmpty()) {
                throw new IllegalStateException("empty list for " + entry.getKey());
            }

            pairs.add(new TierWeaponPair(entry.getKey(), list.get(RNG.nextInt(list.size()))));
        }

        pairs.sort(Comparator.comparing(TierWeaponPair::tier));
        return new RolledConstellation(pairs);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NonNull CommandAutoCompleteInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase(InkstellationsCommand.COMMAND_NAME)) {
            return;
        }

        List<Weapon> weapons = WEAPONS.getWeapons();

        event.replyChoices(weapons.stream()
                .sorted(Comparator.comparingInt(w -> 100 - FuzzySearch.weightedRatio(event.getFocusedOption().getValue(), w.getWeaponNameDe())))
                .limit(3)
                .map(w -> new Command.Choice(w.getWeaponNameDe(), weapons.indexOf(w)))
                .toList()).queue();
    }

    public Map<WeaponTier, List<Weapon>> getConstellations() {
        return weaponTierMap;
    }
}
