package dsb.sunny.scenarios.rng.enums;

import dsb.sunny.gen.weapon.SpecialWeapon;

public enum ScenarioSpecialWeapon {
    BUBBLER(SpecialWeapon.BUBBLER, "Big Bubbler"),
    BOOYAH(SpecialWeapon.BOOYAH, "Booyah Bomb"),
    CRAB(SpecialWeapon.CRAB, "Crab Tank"),
    VAC(SpecialWeapon.VAC, "Ink Vac"),
    INKJET(SpecialWeapon.INKJET, "Inkjet"),
    WAIL(SpecialWeapon.WAIL, "Killer Wail 5.1"),
    REEF(SpecialWeapon.REEF, "Reefslider"),
    COOLER(SpecialWeapon.COOLER, "Tacticooler"),
    INKSTRIKE(SpecialWeapon.INKSTRIKE, "Triple Inkstrike"),
    TRIZOOKA(SpecialWeapon.TRIZOOKA, "Trizooka"),
    STAMP(SpecialWeapon.STAMP, "Ultra Stamp"),
    WAVE(SpecialWeapon.WAVE, "Wavebreaker"),
    ZIPCASTER(SpecialWeapon.ZIPCASTER, "Zipcaster"),
    MISSILES(SpecialWeapon.MISSILES, "Tenta Missiles"),
    STORM(SpecialWeapon.STORM, "Ink Storm"),
    KRAKEN(SpecialWeapon.KRAKEN, "Kraken Royale"),
    CHUMP(SpecialWeapon.CHUMP, "Super Chump"),
    SCREEN(SpecialWeapon.SCREEN, "Splattercolor Screen"),
    SPLASHDOWN(SpecialWeapon.SPLASHDOWN, "Triple Splashdown");

    private final String nameDe;
    private final String nameEn;

    ScenarioSpecialWeapon(SpecialWeapon special, String nameEn) {
        this.nameDe = special.getNameDe();
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
