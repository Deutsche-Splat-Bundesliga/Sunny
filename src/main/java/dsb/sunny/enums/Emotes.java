package dsb.sunny.enums;

public enum Emotes {
    LEAVE("<:leave:998387086019272774> "),
    JOIN("<:join:998387084140228628> "),
    CB("<:CB:1015019420348846081> "),
    RM("<:RM:1015019421590372453> "),
    TC("<:TC:1015019423062560848> "),
    SZ("<:SZ:1015019419077980250> "),
    FRONTLINE("<:Frontline:1012320740336468000> "),
    SUPPORT("<:Support:1012320741976449165> "),
    BACKLINE("<:Backline:1012320738591658075> "),
    WAIT("<a:wait:1037147407793795203>");

    private final String text;

    Emotes(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
    }
