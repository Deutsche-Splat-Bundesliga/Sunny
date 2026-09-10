package dsb.sunny.challonge.report;

public class TeamReport {

    private String teamName;
    private int points;

    public TeamReport(String value) {
        String[] split = value.split(" - ");

        this.teamName = split[0];
        this.points = Integer.parseInt(split[1]);
    }

    public int getPoints() {
        return points;
    }

    public String getTeamName() {
        return teamName;
    }
}
