package dsb.sunny.mvpreport;

public class MVPTeamReportObject {

    private int rank;
    private String name;
    private int count;
    private boolean tied;

    public MVPTeamReportObject(int rank, String name, int count) {
        this.rank = rank;
        this.name = name;
        this.count = count;
        this.tied = false;
    }

    public String toFormattedString() {
        String leftAlignFormat = "%1s%2d. | %-10s | %-2d MVP(s) |%n";
        return String.format(leftAlignFormat, tied ? "=" : "", rank, name.replace("`", ""), count);
    }

    public void setTied() {
        this.tied = true;
    }

    public int getCount() {
        return count;
    }
}
