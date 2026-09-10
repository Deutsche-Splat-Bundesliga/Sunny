package dsb.sunny.interactions.button.buttons;

import dsb.sunny.cocap.CoCaptainModule;
import dsb.sunny.enums.ChannelReferences;
import dsb.sunny.enums.Emotes;
import dsb.sunny.interactions.button.handler.SunnyButton;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class CapRoleButton implements SunnyButton {

    private static final Logger log = LoggerFactory.getLogger(CapRoleButton.class);

    @Override
    public void handle(ButtonInteractionEvent event, String action) throws Exception {
        event.deferEdit().complete();

        Guild guild = event.getGuild();
        Message message = event.getMessage();
        if (action.equalsIgnoreCase("decline")) {
            message.editMessage("Vorgang abgebrochen.")
                    .setReplace(true)
                    .queue();
            return;
        }

        message.editMessage(String.format("%s Einen Augenblick bitte...", Emotes.WAIT))
                .setReplace(true)
                .complete();

        Member oldCaptain = event.getMember();
        Member self = guild.getSelfMember();
        List<Role> divRoles = oldCaptain.getRoles().stream().filter(r -> r.getName().contains("Division")).toList();
        if (divRoles.isEmpty()) {
            message.editMessage("Ich kann schlecht eine Rolle vergeben die du nicht hast, gell?")
                    .setReplace(true)
                    .queue();
            return;
        }


        guild.retrieveMemberById(action).queue(newCaptain -> {
            Regex regex = new Regex("\\[(.+)\\].*");
            MatchResult matchResult;
            if (oldCaptain.getNickname() == null || (matchResult = regex.find(oldCaptain.getNickname(), 0)) == null) {
                message.editMessage(String.format("Ich weiß nicht, für welches Team du spielst. Bitte melde dich beim %s.", ChannelReferences.HELPDESK))
                        .setReplace(true)
                        .queue();
                return;
            }

            boolean canInteract = true;
            for (Role divRole : divRoles) {
                if (!self.canInteract(divRole)) {
                    canInteract = false;
                    break;
                }
            }

            String teamName = matchResult.getGroupValues().get(1);
            if (!self.canInteract(oldCaptain) || !self.canInteract(newCaptain) || !canInteract) {
                message.editMessage(String.format("Ich kann nicht mit den Rollen interagieren. Bitte melde dich beim %s.", ChannelReferences.HELPDESK))
                        .setReplace(true)
                        .queue();
                return;
            }

            try {
                new CoCaptainModule().removeCoCap(oldCaptain);
            } catch (SQLException ex) {
                LoggerFactory.getLogger(getClass()).warn("Couldn't remove Co-Cap!", ex);
            }

            String newNickname = String.format("[%s] %s", teamName, newCaptain.getEffectiveName());
            if (newNickname.length() >= 32) {
                newNickname = String.format("[%s]", teamName);
            }

            guild.modifyNickname(newCaptain, newNickname).queue(success -> log.info("Modified nickname of newCaptain."));
            guild.modifyNickname(oldCaptain, null).queue(success -> log.info("Modified nickname of oldCaptain."));

            for (Role divRole : divRoles) {
                guild.addRoleToMember(newCaptain, divRole).queue(roleAdded -> {
                    log.info("Gave newCaptain the {}!", divRole);

                    guild.removeRoleFromMember(oldCaptain, divRole).queue(roleRemoved -> {
                        log.info("Removed oldCaptain the {}!", divRole);
                        message.editMessage("Die Kapitänsrolle wurde erfolgreich übergeben! Solltest du einen Co-Captain gesetzt haben, muss dieser neu gesetzt werden.")
                                .setReplace(true)
                                .queue();
                    });
                });
            }
        }, fail -> message.editMessage("Ein Fehler ist aufgetreten. Entweder gibt es den User nicht mehr auf diesem Server oder etwas anderes ist passiert.")
                    .setReplace(true)
                    .queue()
        );
    }
}
