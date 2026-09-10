package dsb.sunny.command.commands.user;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.enums.ChannelReferences;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.time.OffsetDateTime;

public class GiveCapRoleCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        Role divRole = event.getMember().getRoles().stream().filter(role -> role.getName().contains("Division"))
                .findFirst().orElse(null);
        if (divRole == null) {
            event.reply("Ich kann schlecht eine Rolle vergeben die du nicht hast, gell?")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Regex regex = new Regex("\\[(.+)\\].*");
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();
        Member newCaptain = event.getOption("user").getAsMember();
        MatchResult matchResult;

        if (member.getNickname() == null || (matchResult = regex.find(member.getNickname(), 0)) == null) {
            event.reply("Ich weiß nicht, für welches Team du spielst. Bitte melde dich beim " + ChannelReferences.HELPDESK + ".")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (newCaptain == null) {
            event.reply("Dieser User ist nicht auf dem Server.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (!selfMember.canInteract(divRole) || !selfMember.canInteract(member) || !selfMember.canInteract(newCaptain)) {
            event.reply("Ich kann nicht mit den Usern oder mit den Rollen interagieren. Bitte melde dich beim " + ChannelReferences.HELPDESK + ".")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        String teamName = matchResult.getGroupValues().get(1);
        boolean ongoingDropRequest = DiscordBot.getChallonge().hasActiveDropRequest(teamName);

        if (ongoingDropRequest) {
            event.reply("Für dein Team läuft gerade ein Drop-Request und wir brauchen dich als Ansprechpartner.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (newCaptain.getId().equals(member.getId())) {
            event.reply("Beschenkst du dich zu Weihnachten und zum Geburtstag auch selber? Sollen wir Freunde werden? c:")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        boolean hasCaptain = newCaptain.getRoles().stream().map(role -> role.getName().contains("Division")).findFirst().orElse(false);
        if (hasCaptain) {
            event.reply("Dieser User ist bereits (Co-)Captain.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        if (newCaptain.getUser().isBot()) {
            event.reply("Ein Bot kann kein Captain sein.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(divRole.getColor())
                .setAuthor("Bestätigung: Übergabe der Cap-Rolle", null, null)
                .setDescription("Möchtest du deine Cap-Rolle an den folgenden User übergeben?")
                .addField("User:", newCaptain.getAsMention(), false)
                .setTimestamp(OffsetDateTime.now());

        Button accept = Button.success(String.format("%d:CAPROLE:%d", event.getUser().getIdLong(), newCaptain.getIdLong()), "Jo, stimmt alles!").asEnabled();
        Button decline = Button.danger(String.format("%d:CAPROLE:decline", event.getUser().getIdLong()), "Nope, lieber nicht...").asEnabled();

        event.replyEmbeds(eb.build())
                .addComponents(ActionRow.of(accept, decline))
                .queue();
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("givecaprole", "Übergibt deine Kapitänrolle an eine andere Person. Ist irreversibel, also sei vorsichtig.")
                .addOption(OptionType.USER, "user", "Der User, welche deine Kapitänrolle bekommen soll.", true);
    }
}
