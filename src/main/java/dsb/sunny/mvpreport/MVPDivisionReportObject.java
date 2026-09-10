package dsb.sunny.mvpreport;

public class MVPDivisionReportObject {

    private int rank;
    private boolean tied;
    private String team;
    private String name;
    private int count;

    public MVPDivisionReportObject(int rank, String team, String name, int count) {
        this.rank = rank;
        this.team = team;
        this.name = name;
        this.count = count;
        this.tied = false;
    }

    public String toFormattedString() {
        String leftAlignFormat = "%1s%2d. | %-20s | %-10s | %-2d MVP(s) |%n";
        return String.format(leftAlignFormat, tied ? "=" : "", rank, abbreviateIfNessesary(team, 20), abbreviateIfNessesary(name, 10), count);
    }

    public void setTied() {
        this.tied = true;
    }

    public int getCount() {
        return count;
    }

    private String abbreviateIfNessesary(String s, int maxLength) {
        if (s.length() <= maxLength) return s;

        return s.substring(0, maxLength - 3) + "...";
    }
}
