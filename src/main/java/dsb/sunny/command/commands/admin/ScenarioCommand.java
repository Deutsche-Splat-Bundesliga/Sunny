package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.scenarios.SplatoonScenarios;
import dsb.sunny.scenarios.scenario.Scenario;
import dsb.sunny.utils.SunnyUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class ScenarioCommand implements SlashCommand {
    private SplatoonScenarios scenarios = null;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (scenarios == null) {
            scenarios = DiscordBot.getScenarios();
        }

        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            switch (event.getSubcommandName()) {
                case "createchannel" -> onCreateChannel(event);
                case "createscenario" -> onCreateScenario(event);
                case "listscenarios" -> onListScenarios(event);
                case "delscenario" -> onDeleteScenario(event);
                case "setcategory" -> onSetCategory(event);
                case "addcontradiction" -> onAddContradiction(event);
                case "delcontradiction" -> onDeleteContradiction(event);
                case "editscenario" -> onEditScenario(event);
                case "roll" -> onRoll(event);
            }
        } else {
            switch (event.getSubcommandName()) {
                case "roll" -> onRoll(event);
                case "createchannel" -> onCreateChannel(event);
                default -> event.reply("Nur Turnierleiter können diese Commands ausführen.").setEphemeral(true).queue();
            }
        }
    }

    private void onEditScenario(SlashCommandInteractionEvent event) {
        int id = event.getOption("id", OptionMapping::getAsInt);
        if (scenarios.getScenario(id) == null) {
            event.reply("Es gibt kein Szenario mit dieser ID.").setEphemeral(true).queue();
            return;
        }

        TextInput title = TextInput.create("title", TextInputStyle.SHORT)
                .setPlaceholder("S-BLAST-MANIA!")
                .setRequired(true)
                .setRequiredRange(1, MessageEmbed.TITLE_MAX_LENGTH)
                .build();

        TextInput rulesDe = TextInput.create("rules_de", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Nur S-Blasts (beide Varianten) dürfen genutzt werden!")
                .setRequired(true)
                .setRequiredRange(1, TextInput.MAX_VALUE_LENGTH)
                .build();

        TextInput rulesEn = TextInput.create("rules_en", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Only S-BLASTs (both variants) can be used!")
                .setRequired(true)
                .setRequiredRange(1, TextInput.MAX_VALUE_LENGTH)
                .build();

        Modal modal = Modal.create("scenario:edit;" + id, "Bearbeite ein Szenario")
                .addComponents(
                        Label.of("Titel", title),
                        Label.of("Regeln (Deutsch)", rulesDe),
                        Label.of("Rules (English)", rulesEn))
                .build();

        event.replyModal(modal).queue();
    }

    private void onDeleteContradiction(SlashCommandInteractionEvent event) throws SQLException {
        int firstId = event.getOption("first_id").getAsInt();
        int secondId = event.getOption("second_id").getAsInt();

        Scenario s1 = scenarios.getScenario(firstId);
        Scenario s2 = scenarios.getScenario(secondId);

        try {
            scenarios.removeContradictingScenario(s1, s2);
            event.reply("Die sich widersprechenden Szenarien wurden entfernt!").setEphemeral(true).queue();
        } catch (IllegalArgumentException e) {
            if (s1 == null) {
                event.reply("Das erste Szenario mit der ID %d gibt es nicht.".formatted(firstId)).setEphemeral(true).queue();
            } else if (s2 == null) {
                event.reply("Das zweite Szenario mit der ID %d gibt es nicht.".formatted(secondId)).setEphemeral(true).queue();
            } else {
                event.reply("Ein Szenario kann sich nicht selbst widersprechen.").setEphemeral(true).queue();
            }
        }
    }

    private void onAddContradiction(SlashCommandInteractionEvent event) throws SQLException {
        int firstId = event.getOption("first_id").getAsInt();
        int secondId = event.getOption("second_id").getAsInt();

        Scenario s1 = scenarios.getScenario(firstId);
        Scenario s2 = scenarios.getScenario(secondId);

        try {
            scenarios.addContradictingScenario(s1, s2);
            event.reply("Die sich widersprechenden Szenarien wurden gespeichert!").setEphemeral(true).queue();
        } catch (IllegalArgumentException e) {
            if (s1 == null) {
                event.reply("Das erste Szenario mit der ID %d gibt es nicht.".formatted(firstId)).setEphemeral(true).queue();
            } else if (s2 == null) {
                event.reply("Das zweite Szenario mit der ID %d gibt es nicht.".formatted(secondId)).setEphemeral(true).queue();
            } else {
                event.reply("Ein Szenario kann sich nicht selbst widersprechen.").setEphemeral(true).queue();
            }
        }
    }

    private void onRoll(SlashCommandInteractionEvent event) {
        int amount = SplatoonScenarios.RANDOM_AMOUNT;
        OptionMapping option = event.getOption("amount");
        if (option != null) {
            amount = option.getAsInt();
        }

        try {
            List<Scenario> l = scenarios.generateRandomScenarios(amount);
            MessageCreateBuilder builder = new MessageCreateBuilder();

            for (Scenario scenario : l) {
                builder.addEmbeds(scenario.getAsEmbed(true));
            }

            if (amount == l.size() || amount == SplatoonScenarios.RANDOM_AMOUNT) {
                builder.setContent("**" + SunnyUtils.singularReplace(l.size(),
                        "Ein Szenario** wurde ausgegeben:",
                        "Szenarien** wurden ausgegeben:"));
            } else {
                builder.setContent("**" + SunnyUtils.singularReplace(l.size(),
                        "Ein Szenario** wurde ausgegeben (nicht genügend Szenarien für %d):".formatted(amount),
                        "Szenarien** wurden ausgegeben (nicht genügend Szenarien für %d):".formatted(amount)));
            }

            event.reply(builder.build()).queue();
        } catch (IllegalStateException e) {
            event.reply("Es wurden noch keine Szenarien geladen.").setEphemeral(true).queue();
        } catch (IllegalArgumentException e) {
            event.reply("Bitte gebe mir eine positive natürliche Zahl.").setEphemeral(true).queue();
        }
    }

    private void onSetCategory(SlashCommandInteractionEvent event) throws SQLException {
        GuildChannelUnion category = event.getOption("category").getAsChannel();
        try {
            if (!category.getType().equals(ChannelType.CATEGORY)) {
                throw new ClassCastException("not a category");
            }
            scenarios.setChannelCategory(category.asCategory());
            event.reply("Die Kanalkategorie wurde gesetzt!").setEphemeral(true).queue();
        } catch (ClassCastException e) {
            event.reply("Dieser Kanal ist keine Kanalkategorie!").setEphemeral(true).queue();
        }
    }

    private void onDeleteScenario(SlashCommandInteractionEvent event) throws SQLException {
        int id = event.getOption("id").getAsInt();
        try {
            scenarios.removeScenario(id);
            event.reply("Das Szenario wurde entfernt!").setEphemeral(true).queue();
        } catch (NoSuchElementException e) {
            event.reply("Es gibt kein Szenario mit dieser ID.").setEphemeral(true).queue();
        }
    }

    private void onListScenarios(SlashCommandInteractionEvent event) {
        List<MessageEmbed> scenarioEmbeds = scenarios.getScenarios().stream().map(s -> {
                    EmbedBuilder eb = new EmbedBuilder(s.getAsEmbed(false));
                    if (!s.getContradictingScenarios().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Scenario s2 : s.getContradictingScenarios()) {
                            sb.append(", `").append("#%03d".formatted(s2.getId())).append("`");
                        }
                        eb.addField("Widersprechend (wird nicht zusammen gezogen):", sb.substring(2), false);
                    }
                    return eb.build();
                }
        ).toList();
        if (scenarioEmbeds.isEmpty()) {
            event.reply("Es wurden noch keine Szenarien erstellt.").setEphemeral(true).queue();
        } else {
            DiscordBot.getPaginator().replyPaginator(event, scenarioEmbeds);
        }
    }

    private void onCreateScenario(SlashCommandInteractionEvent event) {
        TextInput title = TextInput.create("title", TextInputStyle.SHORT)
                .setPlaceholder("S-BLAST-MANIA!")
                .setRequired(true)
                .setRequiredRange(1, MessageEmbed.TITLE_MAX_LENGTH)
                .build();

        TextInput rulesDe = TextInput.create("rules_de", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Nur S-Blasts (beide Varianten) dürfen genutzt werden!")
                .setRequired(true)
                .setRequiredRange(1, TextInput.MAX_VALUE_LENGTH)
                .build();

        TextInput rulesEn = TextInput.create("rules_en", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Only S-BLASTs (both variants) can be used!")
                .setRequired(true)
                .setRequiredRange(1, TextInput.MAX_VALUE_LENGTH)
                .build();

        Modal modal = Modal.create("scenario:create", "Erstelle ein Szenario")
                .addComponents(
                        Label.of("Titel", title),
                        Label.of("Regeln (Deutsch)", rulesDe),
                        Label.of("Regeln (English)", rulesEn))
                .build();

        event.replyModal(modal).queue();
    }

    private void onCreateChannel(SlashCommandInteractionEvent event) {
        try {
            Member member = event.getOption("user").getAsMember();
            if (member == null) {
                throw new IllegalArgumentException("user is null");
            }

            String teamName = member.getUser().getName();
            OptionMapping option = event.getOption("team_name");
            if (option != null) {
                teamName = option.getAsString();
            }
            scenarios.createChannel(member, teamName);
            event.reply("Der Kanal wurde erfolgreich gestellt.").setEphemeral(true).queue();
        } catch (IllegalStateException e) {
            event.reply("Es wurde noch keine Kanalkategorie mit `/roll setcategory` gesetzt.").setEphemeral(true).queue();
        } catch (IllegalArgumentException e) {
            event.reply("Dieser Nutzer ist nicht auf diesem Server.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("scenario", "Rolle ein Splatoon-Szenario für ein Team.")
                .addSubcommands(new SubcommandData("setcategory", "Setze die Kategorie.")
                        .addOption(OptionType.CHANNEL, "category", "Die Kanalkategorie.", true))

                .addSubcommands(new SubcommandData("createchannel", "Erstelle ein Channel für ein Team.")
                        .addOption(OptionType.USER, "user", "Der Captain des Teams.", true)
                        .addOption(OptionType.STRING, "team_name", "Der Team-Name.", false))

                .addSubcommands(new SubcommandData("addcontradiction", "Markiere zwei sich widersprechende Szenarien")
                        .addOption(OptionType.INTEGER, "first_id", "Die ID des ersten Szenario", true)
                        .addOption(OptionType.INTEGER, "second_id", "Die ID des zweiten Szenario", true))

                .addSubcommands(new SubcommandData("delcontradiction", "Entferne den Widerspruch zweier Szenarien")
                        .addOption(OptionType.INTEGER, "first_id", "Die ID des ersten Szenario", true)
                        .addOption(OptionType.INTEGER, "second_id", "Die ID des zweiten Szenario", true))

                .addSubcommands(new SubcommandData("createscenario", "Erstelle ein Szenario."))
                .addSubcommands(new SubcommandData("listscenarios", "Liste alle Szenarien auf."))
                .addSubcommands(new SubcommandData("delscenario", "Lösche ein Szenario.")
                        .addOption(OptionType.INTEGER, "id", "Die ID des Szenarios", true))

                .addSubcommands(new SubcommandData("editscenario", "Bearbeite ein Szenario.")
                        .addOption(OptionType.INTEGER, "id", "Die ID des Szenarios", true))

                .addSubcommands(new SubcommandData("roll", "Rolle ein Szenario.")
                        .addOptions(new OptionData(OptionType.INTEGER, "amount", "Die Menge der Szenarien, die ausgeworfen werden sollen")
                                .setRequiredRange(1, 8)));
    }
}
