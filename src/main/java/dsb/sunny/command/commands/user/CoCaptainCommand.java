package dsb.sunny.command.commands.user;

import dsb.sunny.cocap.CoCaptainModule;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.utils.SunnyUtils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.SQLException;
import java.util.Objects;

public class CoCaptainCommand implements SlashCommand {
    CoCaptainModule module = new CoCaptainModule();

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (SunnyUtils.CAPTAIN_NAME_REGEX.matches(event.getMember().getEffectiveName())
                && event.getMember().getRoles().stream().anyMatch(r -> r.getName().contains("Division"))) {
            String subCommand = Objects.requireNonNull(event.getSubcommandName());
            switch (subCommand) {
                case "give" -> handleGive(event);
                case "remove" -> handleRemove(event);
            }
        } else {
            event.reply("Du bist kein Captain. Melde dich im Helpdesk, wenn dies ein Fehler sein soll.").setEphemeral(true).queue();
        }
    }

    private void handleGive(SlashCommandInteractionEvent event) throws SQLException {
        Member newCoCaptain = event.getOption("member", OptionMapping::getAsMember);
        if (newCoCaptain == null) {
            event.reply("Dieser Member ist nicht auf diesem Server.").setEphemeral(true).queue();
        } else if (newCoCaptain.getRoles().stream().anyMatch(r -> r.getName().contains("Division"))) {
            event.reply("Dieser Member ist bereits (Co-)Captain.").setEphemeral(true).queue();
        } else if (newCoCaptain.getUser().isBot()) {
            event.reply("Ein Bot kann kein Co-Captain werden.").setEphemeral(true).queue();
        } else {
            event.reply("Ok, der Co-Captain wird gesetzt" + (module.makeCoCaptain(event.getMember(), newCoCaptain) ? " und der alte Co-Captain entfernt" : "")
                            + ". Es wird eine Weile dauern, bis der Vorgang abgeschlossen wird.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void handleRemove(SlashCommandInteractionEvent event) throws SQLException {
        event.reply(module.removeCoCap(event.getMember()) ?
                        "Ok, der Co-Captain wird entfernt! Es kann einen Augenblick dauern, bis der Vorgang abgeschlossen wird." :
                        "Du hast keinen Co-Captain gesetzt.")
                .setEphemeral(true).queue();
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("cocap", "Verwalte deine CoCap-Rolle")
                .addSubcommands(new SubcommandData("give", "Vergib eine Co-Captain-Rolle an wen anders.")
                        .addOption(OptionType.USER, "member", "Ein User, der weder Captain noch Co-Captain ist.", true))
                .addSubcommands(new SubcommandData("remove", "Entferne die Co-Captain-Rolle, sofern du eine vergeben hattest."));
    }
}
