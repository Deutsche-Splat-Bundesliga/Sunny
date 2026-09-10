package dsb.sunny.mappool;

import dsb.sunny.gen.map.mode.GameMode;
import dsb.sunny.gen.map.stage.Stage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record MapPool(String title, Map<GameMode, List<Stage>> pool) {

    public List<String> getNames(GameMode gameMode, int type) {
        List<String> names = new LinkedList<>();
        for (Stage stage : pool.getOrDefault(gameMode, Collections.emptyList())) {
            names.add(switch (type) {
                case Stage.GENERIC -> stage.name();
                case Stage.GERMAN -> stage.getGermanName();
                case Stage.ENGLISH -> stage.getEnglishName();
                case Stage.BOTH -> stage.getGermanName() + " / " + stage.getEnglishName();
                default -> throw new IllegalStateException("Unexpected value: " + type);
            });
        }
        return names;
    }
}
