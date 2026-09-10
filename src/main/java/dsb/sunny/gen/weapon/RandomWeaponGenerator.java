package dsb.sunny.gen.weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomWeaponGenerator {

    private final ArrayList<Weapon> weapons;
    private final Random rng;

    public RandomWeaponGenerator() {
        this.weapons = new ArrayList<>();
        this.rng = new Random();

        // Shooter:
        weapons.add(new Weapon("Kleckser", "Splattershot", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Normal_00.png", WeaponType.SHOOTER, SubWeapon.SUCTION, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Tentatek-Kleckser", "Tentatek Splattershot", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Normal_01.png", WeaponType.SHOOTER, SubWeapon.SPLATBOMB, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("N-ZAP85", "N-ZAP '85", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_QuickMiddle_00.png", WeaponType.SHOOTER, SubWeapon.SUCTION, SpecialWeapon.COOLER));
        weapons.add(new Weapon("N-ZAP89", "N-ZAP '89", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_QuickMiddle_01.png", WeaponType.SHOOTER, SubWeapon.AUTO, SpecialWeapon.CHUMP));
        weapons.add(new Weapon(".52 Gallon", ".52 Gal", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Gravity_00.png", WeaponType.SHOOTER, SubWeapon.WALL, SpecialWeapon.WAIL));
        weapons.add(new Weapon(".96 Gallon", ".96 Gal", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Heavy_00.png", WeaponType.SHOOTER, SubWeapon.SPRINKLER, SpecialWeapon.VAC));
        weapons.add(new Weapon(".96 Gallon Deko", ".96 Gal Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Heavy_01.png", WeaponType.SHOOTER, SubWeapon.WALL, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Airbrush MG", "Aerospray MG", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Blaze_00.png", WeaponType.SHOOTER, SubWeapon.FIZZY, SpecialWeapon.REEF));
        weapons.add(new Weapon("Airbrush RG", "Aerospray RG", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Blaze_01.png", WeaponType.SHOOTER, SubWeapon.SPRINKLER, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Junior-Kleckser", "Splattershot Jr.", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_First_00.png", WeaponType.SHOOTER, SubWeapon.SPLATBOMB, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Junior-Kleckser Plus", "Custom Splattershot Jr.", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_First_01.png", WeaponType.SHOOTER, SubWeapon.TORPEDO, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Profi-Kleckser", "Splattershot Pro", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Expert_00.png", WeaponType.SHOOTER, SubWeapon.ANGLE, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Focus-Profi-Kleckser", "Forge Splattershot Pro", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Expert_01.png", WeaponType.SHOOTER, SubWeapon.SUCTION, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Kosmo-Kleckser", "Splattershot Nova", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_QuickLong_00.png", WeaponType.SHOOTER, SubWeapon.SENSOR, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Annaki Kosmo-Kleckser", "Annaki Splattershot Nova", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_QuickLong_01.png", WeaponType.SHOOTER, SubWeapon.MINE, SpecialWeapon.INKJET));
        weapons.add(new Weapon("S3 Tintenwerfer", "H-3 Nozzlenose", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleMiddle_00.png", WeaponType.SHOOTER, SubWeapon.SENSOR, SpecialWeapon.COOLER));
        weapons.add(new Weapon("S3 Tintenwerfer D", "H-3 Nozzlenose D", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleMiddle_01.png", WeaponType.SHOOTER, SubWeapon.WALL, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Platscher", "Jet Squelcher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Long_00.png", WeaponType.SHOOTER, SubWeapon.ANGLE, SpecialWeapon.VAC));
        weapons.add(new Weapon("Platscher SE", "Custom Jet Squelcher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Long_01.png", WeaponType.SHOOTER, SubWeapon.TOXIC, SpecialWeapon.STORM));
        weapons.add(new Weapon("L3 Tintenwerfer", "L-3 Nozzlenose", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleQuick_00.png", WeaponType.SHOOTER, SubWeapon.CURLING, SpecialWeapon.CRAB));
        weapons.add(new Weapon("L3 Tintenwerfer D", "L-3 Nozzlenose D", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleQuick_01.png", WeaponType.SHOOTER, SubWeapon.BURST, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Fein-Disperser", "Splash-o-matic", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Precision_00.png", WeaponType.SHOOTER, SubWeapon.BURST, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Fein-Disperser Neo", "Neo Splash-o-matic", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Precision_01.png", WeaponType.SHOOTER, SubWeapon.SUCTION, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Disperser", "Sploosh-o-matic", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Short_00.png", WeaponType.SHOOTER, SubWeapon.CURLING, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Disperser Neo", "Neo Sploosh-o-matic", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Short_01.png", WeaponType.SHOOTER, SubWeapon.CURLING, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Quetscher", "Squeezer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Flash_00.png", WeaponType.SHOOTER, SubWeapon.WALL, SpecialWeapon.TRIZOOKA));

        // Stringer:
        weapons.add(new Weapon("Tri-Stringer", "Tri-Stringer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Normal_00.png", WeaponType.STRINGER, SubWeapon.TOXIC, SpecialWeapon.WAIL));
        weapons.add(new Weapon("LACT-450", "REEF-LUX 450", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Short_00.png", WeaponType.STRINGER, SubWeapon.CURLING, SpecialWeapon.MISSILES));

        // Slosher:
        weapons.add(new Weapon("3R-Schwapper", "Tri-Slosher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Diffusion_00.png", WeaponType.SLOSHER, SubWeapon.TOXIC, SpecialWeapon.INKJET));
        weapons.add(new Weapon("3R-Schwapper Fresco", "Tri-Slosher Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Diffusion_01.png", WeaponType.SLOSHER, SubWeapon.FIZZY, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Schwapper", "Slosher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Strong_00.png", WeaponType.SLOSHER, SubWeapon.SPLATBOMB, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Wannen-Schwapper", "Bloblobber", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Bathtub_00.png", WeaponType.SLOSHER, SubWeapon.SPRINKLER, SpecialWeapon.STORM));
        weapons.add(new Weapon("Knall-Schwapper", "Explosher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Washtub_00.png", WeaponType.SLOSHER, SubWeapon.SENSOR, SpecialWeapon.STORM));
        weapons.add(new Weapon("Trommel-Schwapper", "Sloshing Machine", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Launcher_00.png", WeaponType.SLOSHER, SubWeapon.FIZZY, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Schwapper Deko", "Slosher Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Strong_01.png", WeaponType.SLOSHER, SubWeapon.ANGLE, SpecialWeapon.ZIPCASTER));

        // Brush:
        weapons.add(new Weapon("Kalligraf", "Octobrush", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Normal_00.png", WeaponType.BRUSH, SubWeapon.SUCTION, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("Quasto", "Inkbrush", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Mini_00.png", WeaponType.BRUSH, SubWeapon.SPLATBOMB, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Quasto Fresco", "Inkbrush Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Mini_01.png", WeaponType.BRUSH, SubWeapon.MINE, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Fächerfärber", "Painbrush", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Heavy_00.png", WeaponType.BRUSH, SubWeapon.CURLING, SpecialWeapon.WAVE));

        // Blaster:
        weapons.add(new Weapon("Blaster", "Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Middle_00.png", WeaponType.BLASTER, SubWeapon.AUTO, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Luna-Blaster", "Luna Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Short_00.png", WeaponType.BLASTER, SubWeapon.SPLATBOMB, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("Luna-Blaster Neo", "Luna Blaster Neo", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Short_01.png", WeaponType.BLASTER, SubWeapon.FIZZY, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Kontra-Blaster", "Clash Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_LightShort_00.png", WeaponType.BLASTER, SubWeapon.SPLATBOMB, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Kontra-Blaster Neo", "Clash Blaster Neo", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_LightShort_01.png", WeaponType.BLASTER, SubWeapon.CURLING, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("Fern-Blaster", "Range Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Long_00.png", WeaponType.BLASTER, SubWeapon.SUCTION, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Turbo-Blaster", "Rapid Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Light_00.png", WeaponType.BLASTER, SubWeapon.MINE, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Turbo-Blaster Plus", "Rapid Blaster Pro", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_LightLong_00.png", WeaponType.BLASTER, SubWeapon.TOXIC, SpecialWeapon.VAC));
        weapons.add(new Weapon("Turbo-Blaster Deko", "Rapid Blaster Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Light_01.png", WeaponType.BLASTER, SubWeapon.TORPEDO, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Turbo-Blaster Plus Deko", "Rapid Blaster Pro Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_LightLong_01.png", WeaponType.BLASTER, SubWeapon.ANGLE, SpecialWeapon.WAIL));
        weapons.add(new Weapon("S-BLAST92", "S-BLAST '92", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Precision_00.png", WeaponType.BLASTER, SubWeapon.SPRINKLER, SpecialWeapon.REEF));

        // Brella:
        weapons.add(new Weapon("Parapluviator", "Splat Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Normal_00.png", WeaponType.BRELLA, SubWeapon.SPRINKLER, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Camp-Pluviator", "Tenta Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Wide_00.png", WeaponType.BRELLA, SubWeapon.BEAKON, SpecialWeapon.VAC));
        weapons.add(new Weapon("UnderCover", "Undercover Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Compact_00.png", WeaponType.BRELLA, SubWeapon.MINE, SpecialWeapon.REEF));
        weapons.add(new Weapon("Sorella Camp-Pluviator", "Tenta Sorella Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Wide_01.png", WeaponType.BRELLA, SubWeapon.MINE, SpecialWeapon.TRIZOOKA));

        // Charger:
        weapons.add(new Weapon("E-liter 4K", "E-liter 4K", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Long_00.png", WeaponType.CHARGER, SubWeapon.MINE, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Ziel-E-liter 4K", "E-liter 4K Scope", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_LongScope_00.png", WeaponType.CHARGER, SubWeapon.MINE, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Klecks-Konzentrator", "Splat Charger", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Normal_00.png", WeaponType.CHARGER, SubWeapon.SPLATBOMB, SpecialWeapon.VAC));
        weapons.add(new Weapon("Ziel-Konzentrator", "Splatterscope", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_NormalScope_00.png", WeaponType.CHARGER, SubWeapon.SPLATBOMB, SpecialWeapon.VAC));
        weapons.add(new Weapon("Klotzer 14-A", "Bamboozler 14 Mk I", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Light_00.png", WeaponType.CHARGER, SubWeapon.AUTO, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Sepiator α", "Classic Squiffer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Quick_00.png", WeaponType.CHARGER, SubWeapon.SENSOR, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("T-Tuber", "Goo Tuber", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Keeper_00.png", WeaponType.CHARGER, SubWeapon.TORPEDO, SpecialWeapon.MISSILES));
        weapons.add(new Weapon("R-BLR/5H", "Snipewriter 5H", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Pencil_00.png", WeaponType.CHARGER, SubWeapon.SPRINKLER, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Zekkori-Klecks-Konzentrator", "Z+F Splat Charger", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Normal_01.png", WeaponType.CHARGER, SubWeapon.WALL, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Zekkori-Ziel-Konzentrator", "Z+F Splatterscope", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Normal_01.png", WeaponType.CHARGER, SubWeapon.WALL, SpecialWeapon.INKSTRIKE));

        // Dualies:
        weapons.add(new Weapon("Klecks-Doppler", "Splat Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Normal_00.png", WeaponType.DUALIES, SubWeapon.SUCTION, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Quadhopper Noir", "Dark Tetra Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Stepper_00.webp", WeaponType.DUALIES, SubWeapon.AUTO, SpecialWeapon.REEF));
        weapons.add(new Weapon("Quadhopper Blanc", "Light Tetra Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Stepper_01.png", WeaponType.DUALIES, SubWeapon.SPRINKLER, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("Sprenkler", "Dapple Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Short_00.png", WeaponType.DUALIES, SubWeapon.BEAKON, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Dual-Platscher", "Dualie Squelchers", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Dual_00.png", WeaponType.DUALIES, SubWeapon.SPLATBOMB, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Dual-Platscher SE", "Custom Dualie Squelchers", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Dual_01.png", WeaponType.DUALIES, SubWeapon.BEAKON, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("Kelvin 525", "Glooga Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Gallon_00.png", WeaponType.DUALIES, SubWeapon.WALL, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Sprenkler Fresco", "Dapple Dualies Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Short_01.png", WeaponType.DUALIES, SubWeapon.TORPEDO, SpecialWeapon.REEF));

        // Roller:
        weapons.add(new Weapon("Dynaroller", "Dynamo Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Heavy_00.png", WeaponType.ROLLER, SubWeapon.SPRINKLER, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Klecksroller", "Splat Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Normal_00.png", WeaponType.ROLLER, SubWeapon.CURLING, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Karbonroller", "Carbon Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Compact_00.png", WeaponType.ROLLER, SubWeapon.AUTO, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("Flex-Roller", "Flingza Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Hunter_00.png", WeaponType.ROLLER, SubWeapon.MINE, SpecialWeapon.MISSILES));
        weapons.add(new Weapon("Karbonroller Deko", "Carbon Roller Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Compact_01.png", WeaponType.ROLLER, SubWeapon.BURST, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Breitroller", "Big Swig Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Wide_00.png", WeaponType.ROLLER, SubWeapon.WALL, SpecialWeapon.VAC));
        weapons.add(new Weapon("Medusa-Klecksroller", "Krak-On Splat Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Normal_01.png", WeaponType.ROLLER, SubWeapon.BEAKON, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Breitroller Express", "Big Swig Roller Express", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Wide_01.png", WeaponType.ROLLER, SubWeapon.ANGLE, SpecialWeapon.STORM));

        // Splatana:
        weapons.add(new Weapon("Wischer-Splatana", "Splatana Wiper", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Lite_00.png", WeaponType.SPLATANA, SubWeapon.TORPEDO, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Stempel-Splatana", "Splatana Stamper", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Normal_00.png", WeaponType.SPLATANA, SubWeapon.BURST, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("Wischer-Splatana Deko", "Splatana Wiper Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Lite_01.png", WeaponType.SPLATANA, SubWeapon.BEAKON, SpecialWeapon.MISSILES));

        // Splatling:
        weapons.add(new Weapon("Splatling", "Heavy Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Standard_00.png", WeaponType.SPLATLING, SubWeapon.SPRINKLER, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Splatling Deko", "Heavy Splatling Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Standard_01.png", WeaponType.SPLATLING, SubWeapon.SENSOR, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Hydrant", "Hydra Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Hyper_00.png", WeaponType.SPLATLING, SubWeapon.AUTO, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Kuli-Splatling", "Ballpoint Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Downpour_00.png", WeaponType.SPLATLING, SubWeapon.FIZZY, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Klecks-Splatling", "Mini Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Quick_00.png", WeaponType.SPLATLING, SubWeapon.BURST, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Sagitron-Klecks-Splatling", "Zink Mini Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Quick_01.png", WeaponType.SPLATLING, SubWeapon.TOXIC, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Nautilus 47", "Nautilus 47", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Serein_00.png", WeaponType.SPLATLING, SubWeapon.SENSOR, SpecialWeapon.STORM));

        // 5.0.0
        weapons.add(new Weapon("Schwappwascher", "Dread Wringer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Double_00.png", WeaponType.SLOSHER, SubWeapon.SUCTION, SpecialWeapon.REEF));
        weapons.add(new Weapon("Kartuschierer", "Heavy Edit Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_HyperShort_00.png", WeaponType.SPLATLING, SubWeapon.CURLING, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Kalligraf Fresco", "Octobrush Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Normal_01.png", WeaponType.BRUSH, SubWeapon.BEAKON, SpecialWeapon.STORM));
        weapons.add(new Weapon("T-Tuber SE", "Custom Goo Tuber", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Keeper_01.png", WeaponType.CHARGER, SubWeapon.FIZZY, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Sorella-Parapluviator", "Splat Sorella Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Normal_01.png", WeaponType.BRELLA, SubWeapon.AUTO, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Wannen-Schwapper Deko", "Bloblobber Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Bathtub_01.png", WeaponType.SLOSHER, SubWeapon.ANGLE, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Kuli-Splatling Fresco", "Ballpoint Splatling Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Downpour_01.png", WeaponType.SPLATLING, SubWeapon.MINE, SpecialWeapon.VAC));
        weapons.add(new Weapon("Trommel-Schwapper Neo", "Neo Sloshing Machine", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Launcher_01.png", WeaponType.SLOSHER, SubWeapon.SENSOR, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Alpomar-Tri-Stringer", "Inkline Tri-Stringer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Normal_01.png", WeaponType.STRINGER, SubWeapon.SPRINKLER, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("Dynaroller Tesla", "Gold Dynamo Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Heavy_01.png", WeaponType.ROLLER, SubWeapon.SPLATBOMB, SpecialWeapon.CHUMP));

        // 6.0.0 weapons
        weapons.add(new Weapon("Quetscher Foil", "Foil Squeezer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Flash_01.png", WeaponType.SHOOTER, SubWeapon.AUTO, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Blaster SE", "Custom Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Middle_01.png", WeaponType.BLASTER, SubWeapon.SENSOR, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("S-BLAST91", "S-BLAST '91", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Precision_01.png", WeaponType.BLASTER, SubWeapon.BURST, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Fächerfärber Fresco", "Painbrush Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Heavy_01.png", WeaponType.BRUSH, SubWeapon.SENSOR, SpecialWeapon.MISSILES));
        weapons.add(new Weapon("R-BLR/5B", "Snipewriter 5B", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Pencil_01.png", WeaponType.CHARGER, SubWeapon.WALL, SpecialWeapon.STORM));
        weapons.add(new Weapon("Enperry-Klecks-Doppler", "Enperry Splat Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Normal_01.png", WeaponType.DUALIES, SubWeapon.CURLING, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("Sorella-UnderCover", "Undercover Sorella Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Compact_01.png", WeaponType.BRELLA, SubWeapon.TORPEDO, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("LACT-450 Deko", "REEF-LUX 450 Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Short_01.png", WeaponType.STRINGER, SubWeapon.WALL, SpecialWeapon.REEF));
        weapons.add(new Weapon("Stempel-Splatana Fresco", "Splatana Stamper Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Normal_01.png", WeaponType.SPLATANA, SubWeapon.TOXIC, SpecialWeapon.CRAB));

        // 7.0.0
        weapons.add(new Weapon(".52 Gallon Deko", ".52 Gal Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Gravity_01.png", WeaponType.SHOOTER, SubWeapon.CURLING, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Flex-Roller Fol", "Foil Flingza Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Hunter_01.png", WeaponType.ROLLER, SubWeapon.SUCTION, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Sepiator β", "New Squiffer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Quick_01.png", WeaponType.CHARGER, SubWeapon.AUTO, SpecialWeapon.ZIPCASTER));
        weapons.add(new Weapon("E-liter 4K SE", "Custom E-liter 4K", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Long_01.png", WeaponType.CHARGER, SubWeapon.BEAKON, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Ziel-E-liter 4K SE", "Custom E-liter 4K Scope", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_LongScope_01.png", WeaponType.CHARGER, SubWeapon.BEAKON, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Knall-Schwapper SE", "Custom Explosher", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Washtub_01.png", WeaponType.SLOSHER, SubWeapon.WALL, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("Schwappwascher D", "Dread Wringer D", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Double_01.png", WeaponType.SLOSHER, SubWeapon.BEAKON, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Nautilus 79", "Nautilus 79", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Serein_01.png", WeaponType.SPLATLING, SubWeapon.SUCTION, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("Kelvin 525 Deko", "Glooga Dualies Deco", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Gallon_01.png", WeaponType.DUALIES, SubWeapon.SENSOR, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Lösch-Doppler FW", "Douser Dualies FF", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Long_00.png", WeaponType.DUALIES, SubWeapon.MINE, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Bast-Pluviator 24-A", "Recycled Brella 24 Mk I", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Focus_00.png", WeaponType.BRELLA, SubWeapon.ANGLE, SpecialWeapon.BUBBLER));

        // 8.0.0
        weapons.add(new Weapon("Penta-Pumper", "Wellstring V", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Explosion_00.png", WeaponType.STRINGER, SubWeapon.AUTO, SpecialWeapon.STAMP));
        weapons.add(new Weapon("Penta-Pumper SE", "Custom Wellstring V", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Explosion_01.png", WeaponType.STRINGER, SubWeapon.SENSOR, SpecialWeapon.WAVE));
        weapons.add(new Weapon("Minz-Prophylator", "Mint Decavitator", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Heavy_00.png", WeaponType.SPLATANA, SubWeapon.SUCTION, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Carbo-Prophylator", "Charcoal Decavitator", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Heavy_01.png", WeaponType.SPLATANA, SubWeapon.WALL, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Klotzer 14-B", "Bamboozler 14 Mk II", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Light_01.png", WeaponType.CHARGER, SubWeapon.FIZZY, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("Hydrant SE", "Custom Hydra Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Hyper_01.png", WeaponType.SPLATLING, SubWeapon.MINE, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Kartuschierer Fresco", "Heavy Edit Splatling Nouveau", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_HyperShort_01.png", WeaponType.SPLATLING, SubWeapon.SPLATBOMB, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Lösch-Doppler FW SE", "Custom Douser Dualies FF", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Long_01.png", WeaponType.DUALIES, SubWeapon.BURST, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Bast-Pluviator 24-B", "Recycled Brella 24 Mk II", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Focus_01.png", WeaponType.BRELLA, SubWeapon.TOXIC, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("Fern-Blaster SE", "Custom Range Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Long_01.png", WeaponType.BLASTER, SubWeapon.SPLATBOMB, SpecialWeapon.KRAKEN));

        // 10.0.0
        weapons.add(new Weapon("Camp-Pluviator CR-MA", "Tenta Brella CRE-M", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Wide_02.png", WeaponType.BRELLA, SubWeapon.TOXIC, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("LACT-450 Q-MLCH", "REEF-LUX 450 MIL-K", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Short_02.png", WeaponType.STRINGER, SubWeapon.TORPEDO, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("S3 Tintenwerfer VIP-R", "H-3 Nozzlenose VIP-R", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleMiddle_02.png", WeaponType.SHOOTER, SubWeapon.SUCTION, SpecialWeapon.INKSTRIKE));
        weapons.add(new Weapon("Klecks-Splatling KLP-R", "Mini Splatling RTL-R", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Quick_02.png", WeaponType.SPLATLING, SubWeapon.BEAKON, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Platscher KB-RA", "Jet Squelcher COB-R", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Long_02.png", WeaponType.SHOOTER, SubWeapon.BURST, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("Turbo-Blaster Plus WNT-R", "Rapid Blaster Pro WNT-R", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_LightLong_02.png", WeaponType.BLASTER, SubWeapon.SUCTION, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Ziel-Konzentrator TAR-N", "Splatterscope CAM-O", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_NormalScope_02.png", WeaponType.CHARGER, SubWeapon.SPRINKLER, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Klecks-Konzentrator TAR-N", "Splat Charger CAM-O", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Charger_Normal_02.png", WeaponType.CHARGER, SubWeapon.SPRINKLER, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Profi-Kleckser CH-LL", "Splattershot Pro FRZ-N", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Expert_02.png", WeaponType.SHOOTER, SubWeapon.SPLATBOMB, SpecialWeapon.MISSILES));
        weapons.add(new Weapon("Wischer-Splatana RST-G", "Splatana Wiper RUS-T", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Lite_02.png", WeaponType.SPLATANA, SubWeapon.CURLING, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("3R-Schwapper OX-D", "Tri-Slosher ASH-N", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Diffusion_02.png", WeaponType.SLOSHER, SubWeapon.SPLATBOMB, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Fächerfärber BRN-Z", "Painbrush BRN-Z", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Heavy_02.png", WeaponType.BRUSH, SubWeapon.WALL, SpecialWeapon.TRIZOOKA));
        weapons.add(new Weapon("Sprenkler U-HU", "Dapple Dualies NOC-T", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Short_02.png", WeaponType.DUALIES, SubWeapon.SPLATBOMB, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Karbonroller ANG-LR", "Carbon Roller ANG-L", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Compact_02.png", WeaponType.ROLLER, SubWeapon.FIZZY, SpecialWeapon.CHUMP));
        weapons.add(new Weapon("Fein-Disperser GK-KO", "Splash-o-matic GCK-O", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Precision_02.png", WeaponType.SHOOTER, SubWeapon.TOXIC, SpecialWeapon.STORM));

        weapons.add(new Weapon("Chroma-Kleckser", "Glamorz Splattershot", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Normal_02.png", WeaponType.SHOOTER, SubWeapon.BURST, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Blaster GLAM", "Gleamz Blaster", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Blaster_Middle_02.png", WeaponType.BLASTER, SubWeapon.BEAKON, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Airbrush RGB", "Colorz Aerospray", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Blaze_02.png", WeaponType.SHOOTER, SubWeapon.BURST, SpecialWeapon.SCREEN));
        weapons.add(new Weapon(".96 Gallon Leo", "Clawz .96 Gal", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_Heavy_02.png", WeaponType.SHOOTER, SubWeapon.ANGLE, SpecialWeapon.COOLER));
        weapons.add(new Weapon("Dual-Platscher ZBR", "Hoofz Dualie Squelchers", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Dual_02.png", WeaponType.DUALIES, SubWeapon.SENSOR, SpecialWeapon.SCREEN));
        weapons.add(new Weapon("Schwappwascher Horn", "Hornz Dread Wringer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Slosher_Double_02.png", WeaponType.SLOSHER, SubWeapon.CURLING, SpecialWeapon.CRAB));
        weapons.add(new Weapon("Stempel-Splatana Sticker", "Stickerz Splatana Stamper", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Saber_Normal_02.png", WeaponType.SPLATANA, SubWeapon.AUTO, SpecialWeapon.BOOYAH));
        weapons.add(new Weapon("Hydrant ATÜ", "Torrentz Hydra Splatling", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Spinner_Hyper_02.png", WeaponType.SPLATLING, SubWeapon.SPRINKLER, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Flair-UnderCover", "Patternz Undercover Brella", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shelter_Compact_02.png", WeaponType.BRELLA, SubWeapon.CURLING, SpecialWeapon.WAIL));
        weapons.add(new Weapon("Glitz-Klecks-Doppler", "Twinklez Splat Dualies", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Maneuver_Normal_02.png", WeaponType.DUALIES, SubWeapon.FIZZY, SpecialWeapon.BUBBLER));
        weapons.add(new Weapon("Breitroller Exzell", "Planetz Big Swig Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Wide_02.png", WeaponType.ROLLER, SubWeapon.TORPEDO, SpecialWeapon.SPLASHDOWN));
        weapons.add(new Weapon("L3 Tintenwerfer Oro", "Glitterz L-3 Nozzlenose", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Shooter_TripleQuick_02.png", WeaponType.SHOOTER, SubWeapon.SPLATBOMB, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Kalligraf Astro", "Cometz Octobrush", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Brush_Normal_02.png", WeaponType.BRUSH, SubWeapon.AUTO, SpecialWeapon.KRAKEN));
        weapons.add(new Weapon("Prisma-Tri-Stringer", "Bulbz Tri-Stringer", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Stringer_Normal_02.png", WeaponType.STRINGER, SubWeapon.ANGLE, SpecialWeapon.INKJET));
        weapons.add(new Weapon("Dynaroller Galaxa", "Starz Dynamo Roller", "https://leanny.github.io/splat3/images/weapon_flat/Path_Wst_Roller_Heavy_02.png", WeaponType.ROLLER, SubWeapon.SENSOR, SpecialWeapon.WAIL));
    }

    public Weapon generateRandomWeapon(Object parameters) {
        if (parameters == null || parameters instanceof String string && string.equalsIgnoreCase("all")) {
            return weapons.get(rng.nextInt(weapons.size()));
        }

        ArrayList<Weapon> weaponsTemp = new ArrayList<>();
        if (parameters instanceof WeaponType type) {
            for (Weapon w : weapons) {
                if (w.getWeaponType().equals(type)) weaponsTemp.add(w);
            }
            return weaponsTemp.isEmpty() ? null : weaponsTemp.get(rng.nextInt(weaponsTemp.size()));
        }

        if (parameters instanceof SubWeapon sub) {
            for (Weapon w : weapons) {
                if (w.getSub().equals(sub)) weaponsTemp.add(w);
            }
            return weaponsTemp.isEmpty() ? null : weaponsTemp.get(rng.nextInt(weaponsTemp.size()));
        }

        if (parameters instanceof SpecialWeapon specialWeapon) {
            for (Weapon w : weapons) {
                if (w.getSpecialWeapon().equals(specialWeapon)) weaponsTemp.add(w);
            }
            return weaponsTemp.isEmpty() ? null : weaponsTemp.get(rng.nextInt(weaponsTemp.size()));
        }
        return null;
    }

    public Weapon generateRandomWeapon() {
        return generateRandomWeapon(null);
    }

    public Object convertParameter(String parameter) {
        if (Arrays.stream(WeaponType.values()).anyMatch(weaponType -> weaponType.name().equalsIgnoreCase(parameter))) {
            return WeaponType.valueOf(parameter);
        }

        if (Arrays.stream(SubWeapon.values()).anyMatch(subWeapon -> subWeapon.name().equalsIgnoreCase(parameter))) {
            return SubWeapon.valueOf(parameter);
        }

        if (Arrays.stream(SpecialWeapon.values()).anyMatch(specialWeapon -> specialWeapon.name().equalsIgnoreCase(parameter))) {
            return SpecialWeapon.valueOf(parameter);
        }

        return null;
    }

    public List<Weapon> getWeapons() {
        return List.copyOf(weapons);
    }
}
