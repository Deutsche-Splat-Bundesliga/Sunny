package dsb.sunny.gen.weapon;

public enum WeaponType {
    BLASTER("Blaster"),
    BRELLA("Pluviator"),
    BRUSH("Bürste"),
    CHARGER("Konzentrator"),
    DUALIES("Doppler"),
    ROLLER("Roller"),
    SHOOTER("Kleckser"),
    SLOSHER("Schwapper"),
    SPLATANA("Splatana"),
    SPLATLING("Splatling"),
    STRINGER("Stringer");

    private final String name;

    WeaponType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
