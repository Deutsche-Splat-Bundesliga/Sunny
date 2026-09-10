package dsb.sunny.gen.weapon;

public enum SubWeapon {
    ANGLE("Winkelmarker", "Angle Shooter"),
    AUTO("Robo-Bombe", "Auto Bomb"),
    CURLING("Curling-Bombe", "Curling Bomb"),
    FIZZY("Sprudel-Bombe", "Fizzy Bomb"),
    MINE("Tintenmine", "Ink Mine"),
    WALL("Tintenwall", "Splash Wall"),
    SPLATBOMB("Klecks-Bombe", "Splat Bomb"),
    SPRINKLER("Sprinkler", "Sprinkler"),
    BEAKON("Sprungboje", "Squid Beakon"),
    SUCTION("Haft-Bombe", "Suction Bomb"),
    TORPEDO("Torpedo", "Torpedo"),
    TOXIC("Sepitox-Nebel", "Toxic Mist"),
    BURST("Insta-Bombe", "Burst Bomb"),
    SENSOR("Detektor", "Point Sensor");

    private final String nameDe;
    private final String nameEn;

    SubWeapon(String nameDe, String nameEn) {
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
