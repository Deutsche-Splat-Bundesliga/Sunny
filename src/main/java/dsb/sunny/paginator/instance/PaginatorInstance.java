package dsb.sunny.paginator.instance;

import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.time.Instant;
import java.util.List;

public class PaginatorInstance {

    private final long userId;
    private int currentIndex = 0;
    private Instant lastUsage;
    private final List<MessageEmbed> pages;

    public PaginatorInstance(UserSnowflake userSnowflake, List<MessageEmbed> pages) {
        this.userId = userSnowflake.getIdLong();
        this.pages = pages;
        this.lastUsage = Instant.now();
    }

    public MessageCreateData createMessage() {
        currentIndex = 0;
        MessageEmbed firstPage = pages.get(currentIndex);
        MessageCreateBuilder data = new MessageCreateBuilder();
        data.setEmbeds(firstPage);
        data.setComponents(buildButtons());
        return data.build();
    }

    public MessageEditData goToFirstPage() {
        return MessageEditData.fromCreateData(createMessage());
    }

    public MessageEditData goToPrevPage() {
        MessageEmbed currentPage = pages.get(--currentIndex);
        return new MessageEditBuilder()
                .setEmbeds(currentPage)
                .setComponents(buildButtons())
                .build();
    }

    public MessageEditData goToNextPage() {
        MessageEmbed currentPage = pages.get(++currentIndex);
        return new MessageEditBuilder()
                .setEmbeds(currentPage)
                .setComponents(buildButtons())
                .build();
    }

    public MessageEditData goToLastPage() {
        currentIndex = pages.size() - 1;
        MessageEmbed currentPage = pages.get(currentIndex);
        return new MessageEditBuilder()
                .setEmbeds(currentPage)
                .setComponents(buildButtons())
                .build();
    }

    public MessageEditData turnToPage(int index) {
        currentIndex = index;
        MessageEmbed currentPage = pages.get(currentIndex);
        return new MessageEditBuilder()
                .setEmbeds(currentPage)
                .setComponents(buildButtons())
                .build();
    }

    private ActionRow buildButtons() {
        Button first = firstPage(userId);
        Button prev = previousPage(userId);
        Button select = pageSelectButton(userId, currentIndex, pages.size());
        Button next = nextButton(userId);
        Button last = lastButton(userId);

        if (currentIndex == 0) {
            first = first.asDisabled();
            prev = prev.asDisabled();
        }

        if (pages.size() == 1) {
            select = select.asDisabled();
        }

        if (currentIndex == pages.size() - 1) {
            next = next.asDisabled();
            last = last.asDisabled();
        }

        this.lastUsage = Instant.now();
        return ActionRow.of(first, prev, select, next, last);
    }

    public MessageEditData deactivate() {
        Button first = firstPage(userId).asDisabled();
        Button prev = previousPage(userId).asDisabled();
        Button select = pageSelectButton(userId, currentIndex, pages.size()).asDisabled();
        Button next = nextButton(userId).asDisabled();
        Button last = lastButton(userId).asDisabled();

        MessageEmbed currentPage = pages.get(currentIndex);
        return new MessageEditBuilder()
                .setEmbeds(currentPage)
                .setComponents(ActionRow.of(first, prev, select, next, last))
                .build();
    }

    public static Button firstPage(long userId) {
        return Button.secondary(String.format("%d:PAGINATOR:first", userId), Emoji.fromUnicode("⏮"));
    }

    public static Button previousPage(long userId) {
        return Button.secondary(String.format("%d:PAGINATOR:prev", userId), Emoji.fromUnicode("◀"));
    }

    public static Button nextButton(long userId) {
        return Button.success(String.format("%d:PAGINATOR:next", userId), Emoji.fromUnicode("▶"))
                .withLabel("Nächste Seite");
    }

    public static Button lastButton(long userId) {
        return Button.secondary(String.format("%d:PAGINATOR:last", userId), Emoji.fromUnicode("⏭"));
    }

    public static Button pageSelectButton(long userId, int currentPage, int maxPages) {
        return Button.primary(String.format("%d:PAGINATOR:select", userId), Emoji.fromUnicode("📖"))
                .withLabel(String.format("%d/%d", currentPage + 1, maxPages));
    }

    public Instant getLastUsage() {
        return lastUsage;
    }
}
