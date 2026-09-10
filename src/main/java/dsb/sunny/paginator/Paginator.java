package dsb.sunny.paginator;

import dsb.sunny.paginator.instance.PaginatorInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Paginator {

    private final Map<Message, PaginatorInstance> paginatorInstanceMap;

    public Paginator() {
        this.paginatorInstanceMap = new HashMap<>();
        ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);

        timer.scheduleAtFixedRate(this::sweepIdleInstances, 1, 1, TimeUnit.MINUTES);
    }

    public void replyPaginator(SlashCommandInteractionEvent event, List<MessageEmbed> embeds) {
        PaginatorInstance instance = new PaginatorInstance(event.getUser(), embeds);
        event.reply(instance.createMessage())
                .queue(o -> o.retrieveOriginal().queue(message -> {
                    if (embeds.size() > 1) {
                        paginatorInstanceMap.put(message, instance);
                    }
                }));
    }

    public PaginatorInstance getPaginatorInstance(Message message) {
        return paginatorInstanceMap.get(message);
    }

    public void sweepIdleInstances() {
        Set<Message> messages = Set.copyOf(paginatorInstanceMap.keySet());

        for (Message message : messages) {
            PaginatorInstance instance = paginatorInstanceMap.get(message);
            if (instance != null) {
                Duration duration = Duration.between(instance.getLastUsage(), Instant.now());
                if (duration.toMinutes() >= 3) {
                    message.editMessage(instance.deactivate()).queue();
                    paginatorInstanceMap.remove(message);
                }
            }
        }
    }
}
