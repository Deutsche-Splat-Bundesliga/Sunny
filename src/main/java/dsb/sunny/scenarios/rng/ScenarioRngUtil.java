package dsb.sunny.scenarios.rng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ScenarioRngUtil {
    private static final Random RNG = new Random();

    public static <T extends Enum<T>> List<T> fromEnum(Class<T> enumClass, int amount, boolean redraw) {
        List<T> enumList = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
        List<T> polledList = new ArrayList<>();
        while (amount-- > 0 && !enumList.isEmpty()) {
            T polled = enumList.get(RNG.nextInt(enumList.size()));
            polledList.add(polled);
            if (!redraw) {
                enumList.remove(polled);
            }
        }
        return polledList;
    }
}
