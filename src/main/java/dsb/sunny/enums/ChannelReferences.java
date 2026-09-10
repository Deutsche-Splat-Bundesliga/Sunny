package dsb.sunny.enums;

public enum ChannelReferences {
    HELPDESK(707547802170949724L),
    BOT_CHANNEL(1007789283542839438L);

    private final long id;

    ChannelReferences(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getAsMention() {
        return "<#" + id + ">";
    }

    @Override
    public String toString() {
        return "<#" + id + ">";
    }
}
