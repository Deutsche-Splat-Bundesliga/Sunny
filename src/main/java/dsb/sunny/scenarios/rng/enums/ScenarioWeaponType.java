package dsb.sunny.scenarios.rng.enums;

import dsb.sunny.gen.weapon.WeaponType;

public enum ScenarioWeaponType {
    BLASTER(WeaponType.BLASTER, "Blaster"),
    BRELLA(WeaponType.BRELLA, "Brella"),
    BRUSH(WeaponType.BRUSH, "Brush"),
    CHARGER(WeaponType.CHARGER, "Charger"),
    DUALIES(WeaponType.DUALIES, "Dualies"),
    ROLLER(WeaponType.ROLLER, "Roller"),
    SHOOTER(WeaponType.SHOOTER, "Shooter"),
    SLOSHER(WeaponType.SLOSHER, "Slosher"),
    SPLATANA(WeaponType.SPLATANA, "Splatana"),
    SPLATLING(WeaponType.SPLATLING, "Splatling"),
    STRINGER(WeaponType.STRINGER, "Stringer");

    private final String nameDe;
    private final String nameEn;

    ScenarioWeaponType(WeaponType type, String nameEn) {
        this.nameDe = type.getName();
        this.nameEn = nameEn;
    }

    public String getNameDe() {
        return nameDe;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getName(boolean english) {
        return english ? nameEn : nameDe;
    }
}
