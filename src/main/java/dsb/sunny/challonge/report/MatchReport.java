package dsb.sunny.challonge.report;

import dsb.sunny.challonge.Division;

public class MatchReport{

    private MatchReportStatus status;
    private Division division;
    private String team1, team2, divisionUrl, weekName; //mvp1, mvp2;
    private int score1, score2;
    private boolean forfeited, scoring, isPlayoff;

    /**
     * Constructor to construct invalid MatchReportStatus reports.<br>
     * <b>Should only be used to construct an invalid status for better readability in the code.</b>
     * @see MatchReport#getStatus() MatchReport.getStatus()
     *
     * @param status The {@link MatchReportStatus}
     */
    public MatchReport(MatchReportStatus status) {
        this.status = status;
    }

    /**
     * Constructor to construct valid MatchReportStatus reports.<br>
     * <b>Use {@link MatchReport#MatchReport(MatchReportStatus)} to construct invalid reports.</b>
     *
     * @param status The {@link MatchReportStatus}
     * @param division The {@link Division}
     * @param team1 The first teams name
     * @param team2 The second teams name
     * @param divisionUrl The current division url used in the embed title
     * @param weekName The current weekName used in the embed title
     * @param score1 Score Report for {@link MatchReport#team1}
     * @param score2 Score Report for {@link MatchReport#team2}
     * @param forfeited If the match has been forfeited
     * @param scoring If the match has scored
     * @param isPlayoff If the match is a playoff match.
     */
    public MatchReport(MatchReportStatus status, Division division, String team1, String team2, String divisionUrl, String weekName,
                       /*String mvp1, String mvp2,*/ int score1, int score2, boolean forfeited, boolean scoring,
                       boolean isPlayoff) {
        this.status = status;
        this.division = division;
        this.team1 = team1;
        this.team2 = team2;
        this.divisionUrl = divisionUrl;
        this.weekName = weekName;
        //this.mvp1 = mvp1;
        //this.mvp2 = mvp2;
        this.score1 = score1;
        this.score2 = score2;
        this.forfeited = forfeited;
        this.scoring = scoring;
        this.isPlayoff = isPlayoff;
    }

    public MatchReportStatus getStatus() {
        return status;
    }

    public Division getDivision() {
        return division;
    }

    public String getTeam1() {
        return team1;
    }

    public String getTeam2() {
        return team2;
    }

    public String getDivisionUrl() {
        return divisionUrl;
    }

    public String getWeekName() {
        return weekName;
    }

    /*
    public String getMVP1() {
        return mvp1;
    }

    public String getMVP2() {
        return mvp2;
    }
    */

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public boolean isForfeited() {
        return forfeited;
    }

    public boolean isScoring() {
        return scoring;
    }

    public boolean isPlayoff() {
        return isPlayoff;
    }
}
