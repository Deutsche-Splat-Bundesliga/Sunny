package dsb.sunny.gen.weapon;

public class Weapon {

    private final String weaponNameDe;
    private final String weaponNameEn;
    private final String pictureUrl;
    private final WeaponType weaponType;
    private final SubWeapon sub;
    private final SpecialWeapon specialWeapon;

    public Weapon(String weaponNameDe, String weaponNameEn, String pictureUrl, WeaponType type, SubWeapon sub, SpecialWeapon special) {
        this.weaponNameDe = weaponNameDe;
        this.weaponNameEn = weaponNameEn;
        this.pictureUrl = pictureUrl;
        this.weaponType = type;
        this.sub = sub;
        this.specialWeapon = special;
    }

    public String getWeaponNameDe() {
        return weaponNameDe;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getTypeName() {
        return weaponType.getName();
    }

    public String getSubName() {
        return sub.getNameDe();
    }

    public String getSpecialName() {
        return specialWeapon.getNameDe();
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public SubWeapon getSub() {
        return sub;
    }

    public SpecialWeapon getSpecialWeapon() {
        return specialWeapon;
    }

    public String getFormattedDe() {
        return String.format("%s (%s, %s)", weaponNameDe, sub.getNameDe(), specialWeapon.getNameDe());
    }

    public String getFormattedEn() {
        return String.format("%s (%s, %s)", weaponNameEn, sub.getNameEn(), specialWeapon.getNameEn());
    }

    public String getFullFormatted() {
        return String.format(
                "%s / %s (%s, %s)",
                weaponNameDe,
                weaponNameEn,
                sub.getFormatted(),
                specialWeapon.getFormatted()
        );
    }
}
