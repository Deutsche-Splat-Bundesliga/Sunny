package dsb.sunny.notifications.platform;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.TwitchClientHelper;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import dsb.sunny.DiscordBot;
import dsb.sunny.notifications.client.SunnyTwitterClient;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger("Twitch Bot");
    private final JDA jda;
    private final ScheduledExecutorService t;
    private TwitchClient client;
    private TwitchClientBuilder builder;
    private TwitchClientHelper helper;
    private EventManager manager;
    private Message currentMessage = null;
    private boolean liveFlag = false;
    private Stream stream;

    public TwitchNotificationService(JDA jda) {
        this.jda = jda;
        this.t = Executors.newScheduledThreadPool(1);
    }

    public void startService() {
        this.builder = TwitchClientBuilder.builder();
        this.client = builder
                .withClientId(DiscordBot.getProperties().getProperty("clientId"))
                .withClientSecret(DiscordBot.getProperties().getProperty("clientSecret"))
                .withEnableHelix(true).build();
        this.helper = client.getClientHelper();
        this.manager = client.getEventManager();

        if (helper.enableStreamEventListener("deutschesplbundesliga") == null) {
            LOG.warn("Stream Event Listener didn't load!");
        }

        manager.onEvent(ChannelGoLiveEvent.class, ev -> {
            if (!liveFlag) {
                this.stream = ev.getStream();

                MessageEmbed me = new EmbedBuilder()
                        .setColor(new Color(0xC5003D))
                        .setAuthor("Die DSB ist LIVE!", null, "https://cdn.discordapp.com/attachments/944399291915571201/944745286822133791/Live.png")
                        .setTitle(ev.getStream().getTitle(), "https://www.twitch.tv/" + ev.getStream().getUserName())
                        .setImage(ev.getStream().getThumbnailUrl(1280, 720))
                        .setThumbnail(DiscordBot.getDSBGuild().getIconUrl())
                        .build();
                TextChannel textChannel = jda.getTextChannelById(SunnySettings.LIVESTREAM.aLong("channel"));
                if (textChannel == null) {
                    LOG.error("Could not send twitch announcement: Channel does not exist");
                    return;
                }
                if (!textChannel.canTalk()) {
                    LOG.error("Could not send twitch announcement: Missing permissions to talk in this channel");
                    return;
                }
                textChannel.sendMessageEmbeds(me).setContent("<@&741758600581480518> Die DSB ist LIVE!!").queue(success -> {
                    currentMessage = success;
                });

                SunnyTwitterClient.getClient().postTweet("%s %s"
                        .formatted(ev.getStream().getTitle(), "https://www.twitch.tv/" + ev.getStream().getUserName()));

                liveFlag = true;
                t.schedule(() -> liveFlag = false, 30, TimeUnit.MINUTES);
            }
        });

        manager.onEvent(ChannelGoOfflineEvent.class, ev -> {
            if (currentMessage != null) {
                currentMessage.delete().queue(null, fail -> LOG.warn("Message could not be deleted", fail));
                currentMessage = null;
                stream = null;
            }
        });

        LOG.info("TwitchClient successfully initialized!");
    }

    public Stream getStream() {
        return stream;
    }
}