package dsb.sunny.scenarios.rng.enums;

import dsb.sunny.gen.weapon.SubWeapon;

public enum ScenarioSubWeapon {
    ANGLE(SubWeapon.ANGLE, "Angle Shooter"),
    AUTO(SubWeapon.AUTO, "Autobomb"),
    CURLING(SubWeapon.CURLING, "Curling Bomb"),
    FIZZY(SubWeapon.FIZZY, "Fizzy Bomb"),
    MINE(SubWeapon.MINE, "Ink Mine"),
    WALL(SubWeapon.WALL, "Splash Wall"),
    SPLATBOMB(SubWeapon.SPLATBOMB, "Splat Bomb"),
    SPRINKLER(SubWeapon.SPRINKLER, "Sprinkler"),
    BEAKON(SubWeapon.BEAKON, "Squid Beakon"),
    SUCTION(SubWeapon.SUCTION, "Suction Bomb"),
    TORPEDO(SubWeapon.TORPEDO, "Torpedo"),
    TOXIC(SubWeapon.TOXIC, "Toxic Mist"),
    BURST(SubWeapon.BURST, "Burst Bomb"),
    SENSOR(SubWeapon.SENSOR, "Point Sensor");

    private final String nameDe;
    private final String nameEn;


    ScenarioSubWeapon(SubWeapon sub, String nameEn) {
        this.nameDe = sub.getNameDe();
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
