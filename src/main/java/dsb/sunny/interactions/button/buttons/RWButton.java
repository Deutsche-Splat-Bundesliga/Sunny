package dsb.sunny.interactions.button.buttons;

import dsb.sunny.DiscordBot;
import dsb.sunny.interactions.button.handler.SunnyButton;
import dsb.sunny.gen.weapon.RandomWeaponGenerator;
import dsb.sunny.gen.weapon.Weapon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Random;

public class RWButton implements SunnyButton {
    @Override
    public void handle(ButtonInteractionEvent event, String action) throws Exception {
        event.deferEdit().queue();
        RandomWeaponGenerator rw = DiscordBot.getRandomWeaponGenerator();
        String[] args = action.split(";");
        String param = args[0];
        int amount = Integer.parseInt(args[1]);

        ArrayList<Weapon> listWeapons = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            listWeapons.add(rw.generateRandomWeapon(rw.convertParameter(param)));
        }

        event.getMessage().editMessageEmbeds(buildEmbed(event.getUser(), listWeapons)).queue();
    }

    private MessageEmbed buildEmbed(User issuedBy, ArrayList<Weapon> listWeapons) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Zufällige Waffe!", null, "https://cdn.discordapp.com/attachments/944399291915571201/1017462604463095859/ShopMiniIcon_01w.png")
                .setColor(new Random().nextInt(0xFFFFFF + 1))
                .setFooter(String.format("Ausgeführt von %s", issuedBy.getName()), issuedBy.getEffectiveAvatarUrl())
                .setTimestamp(OffsetDateTime.now());
        switch (listWeapons.size()) {
            case 0 -> {
                return null;
            }
            case 1 -> {
                Weapon polled = listWeapons.get(0);
                eb.setTitle(polled.getFormattedDe())
                        .setImage(polled.getPictureUrl());
            }
            default -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < listWeapons.size(); i++) {
                    if (i % 4 == 0) {
                        sb.append("\n");
                    }
                    sb.append(String.format("%d) %s", i + 1, listWeapons.get(i).getFormattedDe())).append("\n");
                }
                eb.setTitle("Deine gezogenen Waffen:")
                        .setDescription(sb.toString());
            }
        }
        return eb.build();
    }
}
