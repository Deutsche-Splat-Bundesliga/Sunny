package dsb.sunny.mappool;

import dsb.sunny.enums.Emotes;


public record MapModeCombo(Emotes mode, String mapName) {

    @Override
    public String toString() {
        return mode + "auf " + mapName;
    }
}
