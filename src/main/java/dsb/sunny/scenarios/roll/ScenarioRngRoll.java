package dsb.sunny.scenarios.roll;

import dsb.sunny.gen.weapon.RandomWeaponGenerator;
import dsb.sunny.gen.weapon.Weapon;
import dsb.sunny.scenarios.rng.ScenarioRngUtil;
import dsb.sunny.scenarios.rng.enums.ScenarioMainAbilities;
import dsb.sunny.scenarios.rng.enums.ScenarioSpecialWeapon;
import dsb.sunny.scenarios.rng.enums.ScenarioSubWeapon;
import dsb.sunny.scenarios.rng.enums.ScenarioWeaponType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioRngRoll {
    private static final Pattern PATTERN = Pattern.compile("%(\\w+(?::\\d+)?)%");
    private static final RandomWeaponGenerator RWG = new RandomWeaponGenerator();
    private static final Random RNG = new Random();

    private final List<ScenarioWeaponType> weaponTypes = ScenarioRngUtil.fromEnum(ScenarioWeaponType.class, 4, false);
    private final List<ScenarioWeaponType> weaponTypesRedraw = ScenarioRngUtil.fromEnum(ScenarioWeaponType.class, 4, true);
    private final Weapon randomWeapon = RWG.generateRandomWeapon();

    private final List<ScenarioMainAbilities> mainAbilities = ScenarioRngUtil.fromEnum(ScenarioMainAbilities.class, 8, false);

    private final List<ScenarioSpecialWeapon> specialWeapons = ScenarioRngUtil.fromEnum(ScenarioSpecialWeapon.class, 15, false);

    private final List<ScenarioSubWeapon> subWeapons = ScenarioRngUtil.fromEnum(ScenarioSubWeapon.class, 3, false);

    public String[] formatRules(String rulesDe, String rulesEn) {
        return applyPlaceholder(rulesDe, rulesEn);
    }

    private String[] applyPlaceholder(String rulesDe, String rulesEn) {
        rulesDe = rulesDe.replace("%random_weapon%", randomWeapon.getFormattedDe());

        AtomicReference<String> englishAtomic = new AtomicReference<>(rulesEn.replace("%random_weapon%", randomWeapon.getFormattedDe()));
        Matcher matcher = PATTERN.matcher(rulesDe);
        while (matcher.find()) {
            rulesDe = matcher.replaceFirst(mr -> {
                StringBuilder german = new StringBuilder();
                StringBuilder english = new StringBuilder();
                String[] input = mr.group(1).split(":");

                String invoke = input[0];
                String[] args = Arrays.copyOfRange(input, 1, input.length);
                switch (invoke) {
                    case "weapon_type", "weapon_type_redraw" -> {
                        int amount;
                        if (args.length == 0) {
                            amount = RNG.nextInt(4) + 1;
                        } else {
                            amount = Integer.parseInt(args[0]);
                        }
                        List<ScenarioWeaponType> types = invoke.endsWith("_redraw") ? weaponTypesRedraw : weaponTypes;
                        for (int i = 0; i < amount && i < types.size(); i++) {
                            german.append(", ").append(types.get(i).getName(false));
                            english.append(", ").append(types.get(i).getName(true));
                        }
                    }
                    case "main_ability" -> {
                        int amount;
                        if (args.length == 0) {
                            amount = RNG.nextInt(8) + 1;
                        } else {
                            amount = Integer.parseInt(args[0]);
                        }
                        for (int i = 0; i < amount && i < mainAbilities.size(); i++) {
                            german.append(", ").append(mainAbilities.get(i).getName(false));
                            english.append(", ").append(mainAbilities.get(i).getName(true));
                        }
                    }
                    case "special_weapon" -> {
                        int amount;
                        if (args.length == 0) {
                            amount = RNG.nextInt(15) + 1;
                        } else {
                            amount = Integer.parseInt(args[0]);
                        }
                        for (int i = 0; i < amount && i < specialWeapons.size(); i++) {
                            german.append(", ").append(specialWeapons.get(i).getName(false));
                            english.append(", ").append(specialWeapons.get(i).getName(true));
                        }
                    }
                    case "sub_weapon" -> {
                        int amount;
                        if (args.length == 0) {
                            amount = RNG.nextInt(3) + 1;
                        } else {
                            amount = Integer.parseInt(args[0]);
                        }
                        for (int i = 0; i < amount && i < subWeapons.size(); i++) {
                            german.append(", ").append(subWeapons.get(i).getName(false));
                            english.append(", ").append(subWeapons.get(i).getName(true));
                        }
                    }
                    default -> {
                        german.append("  !!invoke invalid!!");
                        english.append("  !!invoke invalid!!");
                    }
                }
                englishAtomic.set(PATTERN.matcher(englishAtomic.get()).replaceFirst(english.substring(2)));
                return german.substring(2);
            });
            matcher.reset(rulesDe);
        }
        return new String[]{rulesDe, englishAtomic.get()};
    }
}
