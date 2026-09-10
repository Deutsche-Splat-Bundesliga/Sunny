package dsb.sunny.gen.map.stage;

import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.LinkedList;
import java.util.List;

public enum Stage {
    SCORCH("Scorch Gorge", "Sengkluft"),
    EELTAIL("Eeltail Alley", "Streifenaal-Straße"),
    HAGGLEFISH("Hagglefish Market", "Schnapperchen-Basar"),
    UNDERTOW("Undertow Spillway", "Schwertmuschel-Reservoir"),
    MINCEMEAT("Mincemeat Metalworks", "Aalstahl-Metallwerk"),
    HAMMERHEAD("Hammerhead Bridge", "Makrelenbrücke"),
    MUSEUM("Museum d'Alfonsino", "Pinakoithek"),
    MAHI("Mahi-Mahi Resort", "Mahi-Mahi-Resort"),
    INKBLOT("Inkblot Art Academy", "Perlmutt-Akademie"),
    STURGEON("Sturgeon Shipyard", "Störwerft"),
    MAKO("MakoMart", "Cetacea-Markt"),
    WAHOO("Wahoo World", "Flunder-Funpark"),
    BRINEWATER("Brinewater Springs", "Kusaya-Quellen"),
    FLOUNDER("Flounder Heights", "Schollensiedlung"),
    UMAMI("Um'ami Ruins", "Um'ami-Ruinen"),
    MANTA("Manta Maria", "Manta Maria"),
    BARNACLE("Barnacle & Dime", "Talerfisch & Pock"),
    HUMPBACK("Humpback Pump Track", "Buckelwal-Piste"),
    CRABLEG("Crableg Capital", "Seespinnen-Skyline"),
    SHIPSHAPE("Shipshape Cargo Co.", "Frachtschiff Schwerfisch"),
    BLUEFIN("Bluefin Depot", "Blauflossen-Depot"),
    ROBO_ROM_EN("Robo ROM-en", "ROM & RAMen"),
    LA_OLA_AIRPORT("La Ola Airport", "Marlin Airport"),
    LEMURIA_HUB("Lemuria Hub", "Bahnhof Lemuria"),
    URCHIN_UNDERPASS("Urchin Underpass", "Dekabahnstation");

    private final String en;
    private final String de;

    public static final int GENERIC = 0;
    public static final int GERMAN = 1;
    public static final int ENGLISH = 2;
    public static final int BOTH = 3;

    Stage(String en, String de) {
        this.en = en;
        this.de = de;
    }

    public String getEnglishName() {
        return en;
    }

    public String getGermanName() {
        return de;
    }

    public static List<String> getNames(int type) {
        List<String> names = new LinkedList<>();
        for (Stage stage : values()) {
            switch (type) {
                case GENERIC -> names.add(stage.name());
                case GERMAN -> names.add(stage.getGermanName());
                case ENGLISH -> names.add(stage.getEnglishName());
            }
        }
        return new UnmodifiableList<>(names);
    }

    public static List<String> getNames() {
        return getNames(GENERIC);
    }

    public static Stage getStage(String name, int type) {
        if (type == GENERIC) {
            return valueOf(name);
        }

        for (Stage value : values()) {
            if (name.equals(type == GERMAN ? value.de : value.en)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Not a stage!");
    }
}
