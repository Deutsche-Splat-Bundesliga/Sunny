package dsb.sunny;

import dsb.sunny.challonge.ChallongeModule;
import dsb.sunny.channelcleaner.ChannelCleaner;
import dsb.sunny.command.handler.SlashCommandHandler;
import dsb.sunny.constellations.WeaponConstellations;
import dsb.sunny.dbcontroller.DatabaseController;
import dsb.sunny.gen.map.MapListGenerator;
import dsb.sunny.gen.weapon.RandomWeaponGenerator;
import dsb.sunny.interactions.button.handler.SunnyButtonHandler;
import dsb.sunny.interactions.modal.handler.SunnyModalHandler;
import dsb.sunny.listener.EventListener;
import dsb.sunny.notifications.SocialMediaNotificationService;
import dsb.sunny.nowrite.NoWriteRoleModule;
import dsb.sunny.paginator.Paginator;
import dsb.sunny.reminder.ReminderModule;
import dsb.sunny.restapi.RestApiServer;
import dsb.sunny.scenarios.SplatoonScenarios;
import dsb.sunny.selectionroles.SelectionRolesManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DiscordBot {

    private static final Logger LOG = LoggerFactory.getLogger("Discord Bot");
    private static final BotProperties properties = new BotProperties();
    private static DatabaseController databaseController;
    private static Guild guild;
    private static ChallongeModule srm;
    private static NoWriteRoleModule nwrm;
    private static MapListGenerator mapListGenerator;
    private static RandomWeaponGenerator randomWeaponGenerator;
    private static ChannelCleaner channelCleaner;
    private static Paginator paginator;
    private static ReminderModule reminder;
    private static SplatoonScenarios scenarios;
    private static WeaponConstellations constellations;
    private static JDA jda;

    public static void main(String[] args) {
        properties.loadProperties();
        databaseController = new DatabaseController();

        JDABuilder builder = JDABuilder.createDefault(properties.getProperty("token"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.addEventListeners(
                new SlashCommandHandler(),
                new EventListener(),
                new SunnyButtonHandler(),
                new SunnyModalHandler(),
                new SelectionRolesManager());

        try {
            jda = builder.build().awaitReady();
            guild = jda.getGuildById(properties.getProperty("guildId"));
            srm = new ChallongeModule(jda);
            nwrm = new NoWriteRoleModule(jda);
            mapListGenerator = new MapListGenerator();
            randomWeaponGenerator = new RandomWeaponGenerator();
            channelCleaner = new ChannelCleaner(guild);
            paginator = new Paginator();
            reminder = new ReminderModule();
            scenarios = new SplatoonScenarios(guild);
            constellations = new WeaponConstellations();
            jda.addEventListener(reminder);
            jda.addEventListener(constellations);

            SocialMediaNotificationService.getInstance().startServices(jda);
            RestApiServer.startServer();
        } catch (InterruptedException e) {
            LOG.error("Koitus interruptus", e);
        }
    }

    public static BotProperties getProperties() {
        return properties;
    }

    public static ChallongeModule getChallonge() {
        return srm;
    }

    public static NoWriteRoleModule getNwrm() {
        return nwrm;
    }

    public static Guild getDSBGuild() {
        return guild;
    }

    public static Connection borrowDatabaseConnection() throws SQLException {
        return databaseController.borrowConection();
    }

    public static MapListGenerator getMapListGenerator() {
        return mapListGenerator;
    }

    public static RandomWeaponGenerator getRandomWeaponGenerator() {
        return randomWeaponGenerator;
    }

    public static JDA getJDA() {
        return jda;
    }

    public static ChannelCleaner getChannelCleaner() {
        return channelCleaner;
    }

    public static Paginator getPaginator() {
        return paginator;
    }

    public static ReminderModule getReminder() {
        return reminder;
    }

    public static SplatoonScenarios getScenarios() {
        return scenarios;
    }

    public static WeaponConstellations getConstellations() {
        return constellations;
    }
}
