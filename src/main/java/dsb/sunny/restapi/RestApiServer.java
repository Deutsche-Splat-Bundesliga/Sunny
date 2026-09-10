package dsb.sunny.restapi;


import com.github.twitch4j.helix.domain.Stream;
import com.sun.net.httpserver.HttpServer;
import dsb.sunny.DiscordBot;
import dsb.sunny.notifications.SocialMediaNotificationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RestApiServer {

    public static final Logger logger = LoggerFactory.getLogger(RestApiServer.class);

    public static void startServer() {
        try {
            int port = Integer.parseInt(DiscordBot.getProperties().getProperty("restapi_port"));
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);

            httpServer.createContext("/streaminfo", ex -> {
                JSONObject object = new JSONObject();
                Stream stream = SocialMediaNotificationService.getInstance().getTwitchNotificationService().getStream();

                object.put("live", stream != null);
                object.put("title", stream != null ? stream.getTitle() : JSONObject.NULL);
                object.put("category", stream != null ? stream.getGameName() : JSONObject.NULL);

                try (OutputStream os = ex.getResponseBody()) {
                    ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                    ex.sendResponseHeaders(200, 0L);

                    os.write(object.toString().getBytes());
                    os.close();

                    logger.info("{} {} | UA: {} | {}: {} ",
                            ex.getRequestMethod(), ex.getRequestURI().toString(),
                            ex.getRequestHeaders().getFirst("User-Agent"), 200, object);
                } catch(IOException exception) {
                    logger.error("Could not process request", exception);
                }
            });

            httpServer.start();
            logger.info("RestApi server started on port {}!", port);
        } catch (IOException e) {
            logger.error("Could not start restapi server!", e);
        }
    }
}
