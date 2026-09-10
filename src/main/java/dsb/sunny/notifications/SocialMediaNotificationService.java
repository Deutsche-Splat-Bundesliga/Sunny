package dsb.sunny.notifications;

import dsb.sunny.notifications.platform.TwitchNotificationService;
import dsb.sunny.notifications.platform.YouTubeNotificationService;
import net.dv8tion.jda.api.JDA;

public class SocialMediaNotificationService {

    private static SocialMediaNotificationService INSTANCE;

    private TwitchNotificationService twitchNotificationService;
    private YouTubeNotificationService youtubeNotificationService;

    /**
     * Starts all social media services.<br>
     * <i>Who could've thought of that? Wow.</i>
     *
     * @param jda A {@link JDA} instance, can not be null
     */
    public void startServices(JDA jda) {
        if (jda == null) {
            throw new IllegalArgumentException("JDA can not be null!");
        }

        youtubeNotificationService = new YouTubeNotificationService();
        twitchNotificationService = new TwitchNotificationService(jda);

        youtubeNotificationService.startService();
        twitchNotificationService.startService();
    }

    public TwitchNotificationService getTwitchNotificationService() {
        return twitchNotificationService;
    }

    public YouTubeNotificationService getYoutubeNotificationService() {
        return youtubeNotificationService;
    }

    /**
     * Returns an instance of {@link SocialMediaNotificationService}.<br>
     * If the singleton has not been created yet, the method will create and return one.
     *
     * @return A {@link SocialMediaNotificationService} singleton
     */
    public static SocialMediaNotificationService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocialMediaNotificationService();
        }
        return INSTANCE;
    }
}
