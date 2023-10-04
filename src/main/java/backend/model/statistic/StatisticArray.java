package backend.model.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import backend.tools.DateTools;

/**
 * An array of statistics.
 *
 * @author Michael
 */
public class StatisticArray {
    /**
     * The statistics.
     */
    private List<Statistic> statistics;

    /**
     * Default constructor.
     */
    public StatisticArray() {
        this.statistics = new ArrayList<>();
    }

    /**
     * Initializes the StatisticArray with the given statistics.
     *
     * @param statistics The statistics with which the array is initialized.
     */
    public StatisticArray(final List<Statistic> statistics) {
        this.statistics = statistics;
    }

    /**
     * @return the statistics
     */
    @XmlElementWrapper(name = "statistics")
    @XmlElement(name = "statistic")
    public List<Statistic> getStatistics() {
        return statistics;
    }

    /**
     * @param statistics the statistics to set
     */
    public void setStatistics(final List<Statistic> statistics) {
        this.statistics = statistics;
    }

    /**
     * Gets all statistics sorted by date.
     *
     * @return All statistics sorted by date.
     */
    @JsonIgnore
    public List<Statistic> getStatisticsSortedByDate() {
        Collections.sort(this.statistics, new StatisticDateComparator());

        return this.statistics;
    }

    /**
     * Adds the given Statistic to the array.
     *
     * @param statistic The statistic to be added.
     */
    public void addStatistic(final Statistic statistic) {
        this.statistics.add(statistic);
    }

    /**
     * Determines the statistic with the given date. Only day, month and year are taken into account for date lookup.
     *
     * @param date The date for which the Statistic is requested.
     * @return The Statistic of the given date.
     */
    public Statistic getStatisticOfDate(final Date date) {
        Date requestDate;
        Date statisticDate;

        requestDate = DateTools.getDateWithoutIntradayAttributes(date);

        for (Statistic statistic : this.statistics) {
            statisticDate = DateTools.getDateWithoutIntradayAttributes(statistic.getDate());

            if (statisticDate.getTime() == requestDate.getTime()) {
                return statistic;
            }
        }

        return null;
    }
}
