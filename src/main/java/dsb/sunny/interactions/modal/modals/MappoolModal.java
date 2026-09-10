package dsb.sunny.interactions.modal.modals;

import dsb.sunny.DiscordBot;
import dsb.sunny.embeds.StandardEmbeds;
import dsb.sunny.gen.map.mode.GameMode;
import dsb.sunny.gen.map.stage.Stage;
import dsb.sunny.interactions.modal.handler.SunnyModal;
import dsb.sunny.mappool.MapPool;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MappoolModal implements SunnyModal {
    @Override
    public void handle(@NotNull ModalInteractionEvent event, String modalAction) throws Exception {
        InteractionHook hook = event.deferReply(true).complete();
        int type = Integer.parseInt(modalAction);
        String title = event.getValue("title").getAsString();

        String[] zones = event.getValue("zones").getAsString().split("\\n");
        String[] tower = event.getValue("tower").getAsString().split("\\n");
        String[] rain = event.getValue("rain").getAsString().split("\\n");
        String[] clam = event.getValue("clam").getAsString().split("\\n");

        Map<GameMode, List<Stage>> pool = new HashMap<>();
        List<String> allStages = Stage.getNames(type);

        for (String s : zones) {
            ExtractedResult r = FuzzySearch.extractOne(s, allStages);
            pool.putIfAbsent(GameMode.ZONES, new LinkedList<>());
            pool.get(GameMode.ZONES).add(Stage.getStage(r.getString(), type));
        }
        for (String s : tower) {
            ExtractedResult r = FuzzySearch.extractOne(s, allStages);
            pool.putIfAbsent(GameMode.TOWER, new LinkedList<>());
            pool.get(GameMode.TOWER).add(Stage.getStage(r.getString(), type));
        }
        for (String s : rain) {
            ExtractedResult r = FuzzySearch.extractOne(s, allStages);
            pool.putIfAbsent(GameMode.RAIN, new LinkedList<>());
            pool.get(GameMode.RAIN).add(Stage.getStage(r.getString(), type));
        }
        for (String s : clam) {
            ExtractedResult r = FuzzySearch.extractOne(s, allStages);
            pool.putIfAbsent(GameMode.CLAM, new LinkedList<>());
            pool.get(GameMode.CLAM).add(Stage.getStage(r.getString(), type));
        }

        MapPool mapPool = DiscordBot.getMapListGenerator().setMappool(title, pool);

        MessageEditBuilder reply = new MessageEditBuilder()
                .setContent("Der Map-Pool wurde erfolgreich eingestellt!");

        EmbedBuilder eb = StandardEmbeds.mapPool(mapPool, type);
        if (eb != null) {
            eb.setAuthor("Deutsche Splatoon Bundesliga", null, event.getGuild().getIconUrl())
                    .setFooter("Sunny (DSB)", event.getJDA().getSelfUser().getEffectiveAvatarUrl());

            reply.setEmbeds(eb.build());
        }
        hook.editOriginal(reply.build()).queue();
    }
}
