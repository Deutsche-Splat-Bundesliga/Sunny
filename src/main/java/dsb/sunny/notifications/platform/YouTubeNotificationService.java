package dsb.sunny.notifications.platform;

import dsb.sunny.DiscordBot;
import dsb.sunny.settings.SunnySettings;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YouTubeNotificationService {

    public final Logger LOG = LoggerFactory.getLogger(YouTubeNotificationService.class);
    public final String SEARCH_URL_FORMAT = "https://www.youtube.com/feeds/videos.xml?channel_id=%s";

    public ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public String latestVideoId;

    public void startService() {
        String channelId = DiscordBot.getProperties().getProperty("youtubechannelid");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .cache(null)
                .build();

        Request request = new Request.Builder()
                .url(String.format(SEARCH_URL_FORMAT, channelId))
                .build();

        executor.scheduleAtFixedRate(() -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    LOG.error("Could not retrieve latest youtube videos", e);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        LOG.error("Received invalid response from youtube: {}", response);
                        response.close();
                        return;
                    }

                    try(ResponseBody body = response.body()) {
                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(body.byteStream());

                        NodeList nodes = doc.getElementsByTagName("entry");
                        Node node = nodes.item(0);
                        if (node.getNodeType() != Node.ELEMENT_NODE) {
                            return;
                        }

                        Element element = (Element) node;
                        String currentVideoId = element.getElementsByTagName("yt:videoId").item(0).getTextContent();
                        if (latestVideoId == null) {
                            latestVideoId = currentVideoId;
                            LOG.info("Loaded latest video id {}", latestVideoId);
                            return;
                        }

                        if (latestVideoId.equals(currentVideoId)) {
                            return;
                        }

                        // Now check for publish date to check if video has been uploaded in the last 45 minutes
                        String publishDateString = element.getElementsByTagName("published").item(0).getTextContent();
                        OffsetDateTime publishDate = OffsetDateTime.parse(publishDateString);
                        if (System.currentTimeMillis()-(publishDate.toEpochSecond()*1000) >= 45*60*1000) {
                            LOG.info("Detected video ({}) older than 45 minutes, won't send notifications for that.", currentVideoId);
                            return;
                        }
                        latestVideoId = currentVideoId;

                        LOG.info("Detected latest video id change, sending notification...");
                        DiscordBot.getJDA().getTextChannelById(SunnySettings.SOCIAL_MEDIA.aLong("channel"))
                                .sendMessage("<@&1028431936726777946> Die DSB hat ein neues Video hochgeladen!\nhttps://youtu.be/" + currentVideoId)
                                .queue();
                    } catch(SAXException | ParserConfigurationException ex) {
                        LOG.error("Could not parse XML Feed", ex);
                    }
                }
            });

        }, 0, 1, TimeUnit.MINUTES);
    }

}
