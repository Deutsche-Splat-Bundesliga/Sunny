package dsb.sunny.mvpreport;

import java.util.List;

public record MVPTeamLeaderboard(List<MVPTeamReportObject> listMVP) {

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (MVPTeamReportObject o : listMVP) {
            s.append(o.toFormattedString());
        }
        return s.toString();
    }
}
