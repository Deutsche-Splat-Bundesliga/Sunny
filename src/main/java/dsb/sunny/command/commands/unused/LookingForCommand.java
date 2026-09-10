package dsb.sunny.command.commands.unused;

import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.modals.Modal;

public class LookingForCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        String sub = event.getSubcommandName();
        if (sub != null) {
            switch (sub) {
                case "team" -> {
                    TextInput ign = TextInput.create("ign", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("Agent4_Cyo")
                            .setMaxLength(10)
                            .build();

                    TextInput role = TextInput.create("role", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("Frontline / Support / Backline / FlexTape (oder was anderes)")
                            .setMaxLength(20)
                            .build();

                    TextInput vc = TextInput.create("vc", TextInputStyle.SHORT)
                            .setPlaceholder("Ja / Manchmal / Nein")
                            .setMaxLength(8)
                            .setRequired(true)
                            .build();

                    TextInput exp = TextInput.create("exp", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("2nd DSB Season 2 Div 2")
                            .build();

                    TextInput skill = TextInput.create("skill", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("LUTI Div 3 / DSB Div 2-1")
                            .setMaxLength(50)
                            .build();

                    Modal modal = Modal.create("lft:send", "Erstelle ein FA-Post:")
                            .addComponents(
                                    Label.of("IGN (In-Game Name):", ign),
                                    Label.of("Rolle", role),
                                    Label.of("Voice Chat Verfügbarkeit", vc),
                                    Label.of("Erfahrungen / Wissenswerte Resultate", exp),
                                    Label.of("Nach welcher Skillklasse suchst du?", skill)
                            )
                            .build();

                    event.replyModal(modal).queue();
                }
                case "player" -> {

                    TextInput teamName = TextInput.create("team", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("Newer Squidbeak Splatoon")
                            .setMaxLength(50)
                            .build();

                    TextInput role = TextInput.create("role", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("+1 Frontline / Support / Backline / FlexTape")
                            .setMaxLength(20)
                            .build();

                    TextInput aboutUs = TextInput.create("aboutus", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("""
                                    Wir sind ein neues Team mit Limone, Aioli und Kap'n!""")
                            .build();

                    TextInput requirements = TextInput.create("requirements", TextInputStyle.PARAGRAPH).build();

                    TextInput skill = TextInput.create("skill", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setPlaceholder("LUTI Div 3 / DSB Div 2-1")
                            .setMaxLength(50)
                            .build();

                    Modal modal = Modal.create("lfp:send", "Erstelle ein Recruitment-Post:")
                            .addComponents(
                                    Label.of("Teamname:", teamName),
                                    Label.of("Nach welcher Rolle sucht ihr?", role),
                                    Label.of("Wer seid ihr? Stellt euch vor!", aboutUs),
                                    Label.of("Anforderungen an eure neuen Teammates:", requirements),
                                    Label.of("Nach welcher Skillklasse sucht ihr?", skill)
                            )
                            .build();

                    event.replyModal(modal).queue();
                }
                default -> throw new IllegalStateException("Unexpected value: " + sub);
            }
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("looking4", "Schreibe ein \"Looking for\"-Text. Cooldown ist eine Woche.")
                .addSubcommands(new SubcommandData("team", "Schreibe ein FA-Post in #looking4team. (Für suchende Spieler)"))
                .addSubcommands(new SubcommandData("player", "Schreibe ein Recruitment-Post in #looking4player. (Für suchende Teams)"));
    }
}
