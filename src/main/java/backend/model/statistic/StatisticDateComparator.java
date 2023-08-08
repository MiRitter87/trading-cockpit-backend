package backend.model.statistic;

import java.util.Comparator;

/**
 * Compares two statistics by their date.
 *
 * @author Michael
 */
public class StatisticDateComparator implements Comparator<Statistic> {
    /**
     * Compares its two statistics for order by their date.
     */
    @Override
    public int compare(final Statistic statistic1, final Statistic statistic2) {
        if (statistic1.getDate().getTime() < statistic2.getDate().getTime()) {
            return 1;
        } else if (statistic1.getDate().getTime() > statistic2.getDate().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }
}
