package dsb.sunny.mvpreport;

import java.util.List;

public record MVPDivisionLeaderboard(List<MVPDivisionReportObject> mvpList) {

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (MVPDivisionReportObject o : mvpList) {
            s.append(o.toFormattedString());
        }
        return s.toString();
    }
}
