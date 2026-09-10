package dsb.sunny.gen.map;

import dsb.sunny.DiscordBot;
import dsb.sunny.gen.map.combination.MapModeCombination;
import dsb.sunny.gen.map.mode.GameMode;
import dsb.sunny.gen.map.stage.Stage;
import dsb.sunny.mappool.MapPool;
import dsb.sunny.settings.SunnySettings;
import org.jetbrains.annotations.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class MapListGenerator {
    private String title;
    private Map<GameMode, List<Stage>> pool;
    private final SunnySettings settings = SunnySettings.MAPPOOL;

    private static final Logger LOG = LoggerFactory.getLogger(MapListGenerator.class);

    public MapListGenerator() {
        this.pool = new HashMap<>();
        this.title = settings.string("title");
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM MapPool;");

            while (rs.next()) {
                GameMode m = GameMode.valueOf(rs.getString(1));
                Stage s = Stage.valueOf(rs.getString(2));

                pool.putIfAbsent(m, new LinkedList<>());
                pool.get(m).add(s);
            }
            pool = Map.copyOf(pool);
        } catch (SQLException ex) {
            LOG.error("Couldn't load map pool! ", ex);
        }
    }

    public List<MapModeCombination> generateMaps(List<GameMode> modes, @Range(from = 1, to = 50) int amount) {
        if (modes.isEmpty()) {
            throw new IllegalArgumentException("modes is empty!");
        }

        List<GameMode> availableModes = new LinkedList<>(modes);
        for (GameMode gameMode : List.copyOf(availableModes)) {
            if (!pool.containsKey(gameMode)) {
                availableModes.remove(gameMode);
            }
        }
        modes = availableModes;

        Map<GameMode, List<Stage>> poolCopy = new HashMap<>();
        pool.forEach((gameMode, stages) -> poolCopy.put(gameMode, new LinkedList<>(stages)));

        Queue<GameMode> currentModes = new LinkedList<>(modes);
        Set<Stage> availableStages = createStageList(modes);
        List<MapModeCombination> polled = new LinkedList<>();

        for (List<Stage> value : poolCopy.values()) {
            Collections.shuffle(value);
        }

        for (int i = 0; i < amount; i++) {
            if (currentModes.isEmpty()) {
                currentModes.addAll(modes);
            }
            if (availableStages.isEmpty()) {
                availableStages = createStageList(modes);
                if (!polled.isEmpty()) {
                    availableStages.remove(polled.get(polled.size() - 1).stage());
                }
            }

            GameMode m = currentModes.poll();
            List<Stage> l = poolCopy.get(m);

            if (l.isEmpty()) {
                List<Stage> newStages = new LinkedList<>(pool.get(m));
                Collections.shuffle(newStages);
                l = newStages;
                poolCopy.put(m, newStages);
            }

            MapModeCombination comb = null;
            for (Stage stage : l) {
                if (availableStages.contains(stage)) {
                    comb = new MapModeCombination(m, stage);
                    l.remove(stage);
                    availableStages.remove(stage);
                    break;
                }
            }

            if (comb == null) {
                Stage recentStage = !polled.isEmpty() ? polled.get(polled.size() - 1).stage() : null;
                Stage polledStage = null;
                if (recentStage != null) {
                    for (Stage stage : List.copyOf(l)) {
                        if (!stage.equals(recentStage)) {
                            polledStage = stage;
                            break;
                        }
                    }
                }

                polledStage = polledStage == null ? l.get(0) : polledStage;
                comb = new MapModeCombination(m, polledStage);
                l.remove(polledStage);
            }
            polled.add(comb);
        }
        return polled;
    }

    private Set<Stage> createStageList(List<GameMode> modes) {
        Set<Stage> l = new HashSet<>();
        for (GameMode mode : modes) {
            l.addAll(pool.getOrDefault(mode, Collections.emptyList()));
        }
        return l;
    }

    public MapPool setMappool(String title, Map<GameMode, List<Stage>> pool) throws SQLException {
        settings.setValue("title", title);
        this.title = title;

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            conn.createStatement().execute("DELETE FROM MapPool;");
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO MapPool
                    VALUES (?, ?);""");

            for (GameMode gameMode : pool.keySet()) {
                List<Stage> l = pool.get(gameMode);
                for (Stage stage : l) {
                    ps.setString(1, gameMode.name());
                    ps.setString(2, stage.name());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }

        this.pool = Map.copyOf(pool);
        return new MapPool(title, pool);
    }

    public void setTitle(String title) throws SQLException {
        settings.setValue("title", this.title);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public MapPool getMapPool() {
        return pool.isEmpty() ? null : new MapPool(title, pool);
    }
}
