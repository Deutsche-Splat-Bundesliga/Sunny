package dsb.sunny.gen.weapon;

public enum SpecialWeapon {
    BUBBLER("Kugelschild Pro", "Big Bubbler"),
    BOOYAH("Cool-Kugel", "Booyah Bomb"),
    CRAB("Krabbenpanzer", "Crab Tank"),
    VAC("Tintegrator", "Ink Vac"),
    INKJET("Tintendüser", "Inkjet"),
    WAIL("Heulboje 5.1", "Killer Wail 5.1"),
    REEF("Haihammer", "Reefslider"),
    COOLER("Tranktank", "Tacticooler"),
    INKSTRIKE("Tri-Tintferno", "Triple Inkstrike"),
    TRIZOOKA("Trizooka", "Trizooka"),
    STAMP("Ultra-Stempel", "Ultra Stamp"),
    WAVE("Schauerwelle", "Wave Breaker"),
    ZIPCASTER("Haftsprung", "Zipcaster"),
    MISSILES("Schwarmraketen", "Tenta Missiles"),
    STORM("Tintenschauer", "Ink Storm"),
    KRAKEN("Tintentyrann", "Kraken Royale"),
    CHUMP("Bluffbomber", "Super Chump"),
    SCREEN("Unsichtbarriere", "Splattercolor Screen"),
    SPLASHDOWN("Tri-Tintenschock", "Triple Splashdown");

    private final String nameDe;
    private final String nameEn;

    SpecialWeapon(String nameDe, String nameEn) {
        this.nameDe = nameDe;
        this.nameEn = nameEn;
    }

    public String getNameDe() {
        return nameDe;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getFormatted() {
        return nameDe + " / " + nameEn;
    }
}
