package dsb.sunny.notifications.platform;

import com.github.scribejava.core.model.Response;
import dsb.sunny.BotProperties;
import dsb.sunny.DiscordBot;
import dsb.sunny.settings.SunnySettings;
import io.github.redouane59.twitter.IAPIEventListener;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Deprecated
public class TwitterNotificationService {

    @Deprecated
    public final Logger logger = LoggerFactory.getLogger(TwitterNotificationService.class);
    @Deprecated
    public Future<Response> currentStream = null;

    private TwitterClient twitterClient;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    @Deprecated
    public void startService() {
        try {
            BotProperties properties = DiscordBot.getProperties();

            twitterClient = new TwitterClient(TwitterCredentials.builder()
                    .bearerToken(properties.getProperty("twitter_bearer"))
                    .accessToken(properties.getProperty("twitter_accessToken"))
                    .accessTokenSecret(properties.getProperty("twitter_accessSecret"))
                    .apiKey(properties.getProperty("twitter_apiKey"))
                    .apiSecretKey(properties.getProperty("twitter_apiSecret"))
                    .build());

            logger.info("Login successful!");
            boolean dsbStreamRule = !twitterClient.retrieveFilteredStreamRules()
                    .stream()
                    .map(rule -> rule.getTag().equals("from:DSplatoonLiga"))
                    .toList()
                    .isEmpty();

            if (!dsbStreamRule) {
                twitterClient.addFilteredStreamRule("from:DSplatoonLiga", "from:DSplatoonLiga");
                logger.info("Added DSplatoonLiga as filtered stream rule");
            }
            createStream();
        } catch (Exception ex) {
            logger.error("Something bad happened, we don't know what, but here's the stacktrace", ex);
        }
    }

    private void createStream() {
        scheduledExecutorService.execute(() -> {
            this.currentStream = twitterClient.startFilteredStream(new IAPIEventListener() {

                @Override
                public void onUnknownDataStreamed(String json) {
                    logger.warn("Received unknown data: {}", json);
                }

                @Override
                public void onTweetStreamed(Tweet tweet) {
                    try {
                        if (tweet.getUser() != null) {
                            DiscordBot.getJDA().getTextChannelById(SunnySettings.SOCIAL_MEDIA.aLong("channel"))
                                    .sendMessage(
                                            String.format("<@&1028431936726777946> https://twitter.com/%s/status/%s",
                                                    tweet.getUser().getName(), tweet.getId()))
                                    .queue();
                        }
                    } catch (Exception ex) {
                        logger.error("Caught uncaught weird exception", ex);
                    }
                }

                @Override
                public void onStreamError(int httpCode, String error) {
                    logger.warn("An error occurred whilst running a twitter stream (Responsecode " + httpCode + "): "
                            + error);
                }

                @Override
                public void onStreamEnded(Exception e) {
                    logger.error("Twitter Client died, restarting in 3 seconds", e);
                    restartStream();
                }
            });
            logger.info("Setup successful!");
        });
    }

    private void restartStream() {
        if (currentStream != null) {
            twitterClient.stopFilteredStream(currentStream);
            currentStream = null;
        }
        createStream();
    }

    @Deprecated
    public void startRestartService() {
        logger.info("Restart service started successfully!");

        scheduledExecutorService.scheduleAtFixedRate(() -> {

            if (shouldRestart()) {
                logger.info("Triggered automatic restart.");
                restartStream();
            }

        }, 0, 1, TimeUnit.MINUTES);
    }

    private boolean shouldRestart() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MINUTE) == 0) {
            if (calendar.get(Calendar.HOUR_OF_DAY) == 15 || calendar.get(Calendar.HOUR_OF_DAY) == 2) {
                return true;
            }
        }
        return false;
    }

}
