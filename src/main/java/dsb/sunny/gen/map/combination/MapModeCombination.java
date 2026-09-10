package dsb.sunny.gen.map.combination;

import dsb.sunny.gen.map.stage.Stage;
import dsb.sunny.gen.map.mode.GameMode;

public record MapModeCombination(GameMode gameMode, Stage stage) {
    public String getFormatted(int lang) {
        return gameMode + switch (lang) {
            case Stage.GENERIC -> stage.name();
            case Stage.GERMAN -> stage.getGermanName();
            case Stage.ENGLISH -> stage.getEnglishName();
            case Stage.BOTH -> (stage.getGermanName() + " / " + stage.getEnglishName());
            default -> throw new IllegalStateException("Unexpected value: " + lang);
        };
    }
}
