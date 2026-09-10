package dsb.sunny.listener;

import dsb.sunny.DiscordBot;
import dsb.sunny.enums.Emotes;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class EventListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger("Event Listener");

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Role r = event.getGuild().getRoleById(SunnySettings.JOINROLE.aLong("role"));

        if (r == null) {
            LOG.error("Could not assign joinrole to member {}: Role can not be null!", event.getMember().getId());
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(r)) {
            LOG.warn("Could not assign role {} to member {}: Role can not be higher than the members' highest role!",
                    r.getId(), event.getMember().getId());
            return;
        }

        event.getGuild().addRoleToMember(UserSnowflake.fromId(event.getUser().getId()), r).queue(success -> {
            LOG.info("Assigned role {} to member {}", r.getId(), event.getMember().getId());
        });

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            long id = event.getMember().getIdLong();
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT hasRole from nowriteroles WHERE userID = ?;""");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Role r1 = event.getMember().getGuild().getRoleById(rs.getLong(1));
                if (r1 != null) {
                    event.getMember().getGuild().addRoleToMember(event.getMember(), r).queue();
                }
            }
            rs.close();
        } catch (SQLException e) {
            LOG.error("Couldn't reassign roles to member: " + event.getMember().getUser().getName(), e);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        String[] argsModal = event.getModalId().split(":");
        switch (argsModal[0]) {
            case "lft" -> {
                TextChannel channel = event.getGuild().getTextChannelById(1013418618853281842L);

                event.deferEdit().queue();
                EmbedBuilder eb = new EmbedBuilder()
                        .setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl())
                        .setTitle("In-Game Name: " + event.getValue("ign").getAsString())
                        .addField("**Rolle:**", switch (event.getValue("role").getAsString().toLowerCase()) {
                            case "frontline" -> Emotes.FRONTLINE + "Frontline";
                            case "support" -> Emotes.SUPPORT + "Support";
                            case "backline" -> Emotes.BACKLINE + "Backline";
                            default -> event.getValue("role").getAsString();
                        }, true)
                        .addField("**Voice Chat Verfügbarkeit:**",
                                switch (event.getValue("vc").getAsString().toLowerCase()) {
                                    case "ja" -> "✅ Ja";
                                    case "manchmal" -> "🟡 Manchmal";
                                    case "nein" -> "❌ Nein";
                                    default -> event.getValue("vc").getAsString();
                                }, true)
                        .addField("**Erfahrungen:**", event.getValue("exp").getAsString(), false)
                        .addField("**sucht nach Skillklasse:**", event.getValue("skill").getAsString(), false)
                        .addField("Discord-Account:", event.getMember().getAsMention(), false)
                        .setTimestamp(OffsetDateTime.now());

                channel.sendMessageEmbeds(eb.build()).queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String[] args = event.getComponentId().split(":");
        String type = args[0];

        switch (type) {
            case "giverole" -> {
                Role role = event.getGuild().getRoleById(args[1]);
                if (role == null) {
                    event.reply("Diese Rolle existiert nicht mehr").setEphemeral(true).queue();
                    return;
                }
                if (!event.getGuild().getSelfMember().canInteract(role)) {
                    event.reply(
                            "Ich kann mit dieser Rolle nicht interagieren, bitte benachrichtige einen Turnierleiter.")
                            .queue();
                    return;
                }
                event.deferEdit().queue();
                if (event.getMember().getRoles().contains(role)) {
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(event.getMember().getId()), role)
                            .queue(success -> {
                                LOG.info("Removed role {} from member {}", role.getId(), event.getMember().getId());
                            });
                    return;
                }
                event.getGuild().addRoleToMember(UserSnowflake.fromId(event.getMember().getId()), role)
                        .queue(success -> {
                            LOG.info("Added role {} to member {}", role.getId(), event.getMember().getId());
                        });
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.isWebhookMessage() || event.getChannelType() != ChannelType.TEXT)
            return;
        try {
            if (!(event.getMember().hasPermission(Permission.MANAGE_ROLES) &&
                    event.getMember().hasPermission(Permission.MANAGE_CHANNEL))) {
                DiscordBot.getNwrm().handleEvent(event.getMember(), event.getChannel().asTextChannel());
            }
        } catch (Exception e) {
            LOG.error("Couldn't handle newest message: ", e);
        }
    }
}
