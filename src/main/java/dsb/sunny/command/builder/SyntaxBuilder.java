package dsb.sunny.command.builder;

import dsb.sunny.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.time.OffsetDateTime;

public class SyntaxBuilder {

    public Command data;
    public EmbedBuilder builder;

    public SyntaxBuilder(Command data) {
        this.data = data;
        this.builder = new EmbedBuilder();
    }

    private void buildData() {

        String syntax = data.getName();
        if (data.getSubcommands().isEmpty()) {
            for (Command.Option option : data.getOptions()) {
                if (option.isRequired()) {
                    syntax+=" [" + option.getName() + "]";
                    continue;
                }
                syntax+=" <" + option.getName() + ">";
            }
            builder.addField(syntax, "> *" + data.getDescription() + "*", false);
            return;
        }

        for (Command.Subcommand subcommand : data.getSubcommands()) {
            syntax = subcommand.getName();

            for (Command.Option option : subcommand.getOptions()) {
                if (option.isRequired()) {
                    syntax+=" [" + option.getName() + "]";
                    continue;
                }
                syntax+=" <" + option.getName() + ">";
            }
            builder.addField(data.getName() + " " + syntax, "> *" + subcommand.getDescription() + "*", false);
        }
    }

    public MessageEmbed build() {
        builder.setThumbnail(DiscordBot.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setColor(0x5865F2);
        builder.setTitle("<:slash:998986781938679930> Slash-Command " + data.getAsMention());
        builder.setDescription("```" + data.getDescription() + "```");
        builder.setFooter("[Benötigtes Argument]  | <Optionales Argument>");
        builder.setTimestamp(OffsetDateTime.now());

        buildData();
        return builder.build();
    }

}
