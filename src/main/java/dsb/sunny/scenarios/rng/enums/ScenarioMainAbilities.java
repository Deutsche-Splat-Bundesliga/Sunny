package dsb.sunny.scenarios.rng.enums;

public enum ScenarioMainAbilities {
    SAVER_MAIN("Hauptverbrauch", "Ink Saver Main", false),
    SAVER_SUB("Sekundärverbrauch", "Ink Saver Sub", false),
    RECOVERY("Regeneration +", "Ink Recovery", false),
    RUN_SPEED("Lauftempo +", "Run Speed Up", false),
    SWIM_SPEED("Schwimmtempo +", "Swim Speed Up", false),
    SPECIAL_CHARGE("Spezialladezeit +", "Special Charge Up", false),
    SPECIAL_SAVER("Spezialabzug -", "Special Saver", false),
    SPECIAL_POWER("Spezialstärke +", "Special Power Up", false),
    QUICK_RESPAWN("Schnelle Rückkehr", "Quick Respawn", false),
    SUPER_JUMP("Supersprung +", "Quick Super Jump", false),
    SUB_POWER("Sekundärstärke +", "Sub Power Up", false),
    INK_RESISTANCE("Tintentoleranz +", "Ink Resistance Up", false),
    SUB_RESISTANCE("Sekundärschutz", "Sub Resistance Up", false),
    INTENSIFY("Action +", "Intensify Action", false),
    // exclusives
    OPENING("Startvorteil", "Opening Gambit", true),
    LAST_DITCH("Endspurt", "Last-Ditch Effort", true),
    TENACITY("Zähigkeit", "Tenacity", true),
    COMEBACK("Rückkehr", "Comeback", true),
    NINJA("Tintenfisch-Ninja", "Ninja Squid", true),
    HAUNT("Vergeltung", "Haunt", true),
    THERMAL("Markierfarbe", "Thermal Ink", true),
    PUNISHER("Heimsuchung", "Respawn Punisher", true),
    STEALTH("Sprunginfiltration", "Stealth Jump", true),
    SHREDDER("Zerstörer", "Object Shredder", true),
    DROP_ROLLER("Tricklandung", "Drop Roller", true)
    ;

    private final String nameDe;
    private final String nameEn;
    private final boolean isExclusive;

    ScenarioMainAbilities(String nameDe, String nameEn, boolean isExclusive) {
        this.nameDe = nameDe;
        this.nameEn = nameEn;
        this.isExclusive = isExclusive;
    }

    public String getNameDe() {
        return nameDe;
    }

    public String getNameEn() {
        return nameEn;
    }

    public boolean isExclusive() {
        return isExclusive;
    }

    public String getName(boolean english) {
        return english ? nameEn : nameDe;
    }
}
