package dsb.sunny.notifications.client;

import dsb.sunny.BotProperties;
import dsb.sunny.DiscordBot;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.signature.TwitterCredentials;

public class SunnyTwitterClient {

    private static TwitterClient client;

    public static TwitterClient getClient() {
        BotProperties properties = DiscordBot.getProperties();
        if (client == null) {
            client = new TwitterClient(TwitterCredentials.builder()
                    .bearerToken(properties.getProperty("twitter_bearer"))
                    .accessToken(properties.getProperty("twitter_accessToken"))
                    .accessTokenSecret(properties.getProperty("twitter_accessSecret"))
                    .apiKey(properties.getProperty("twitter_apiKey"))
                    .apiSecretKey(properties.getProperty("twitter_apiSecret"))
                    .build());
        }
        return client;
    }
}
