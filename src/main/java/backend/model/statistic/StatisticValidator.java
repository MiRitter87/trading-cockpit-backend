package backend.model.statistic;

import java.util.Objects;

/**
 * Performs attribute validations of the Statistic model.
 *
 * @author Michael
 */
public class StatisticValidator {
    /**
     * The statistic to be validated.
     */
    private Statistic statistic;

    /**
     * Initializes the StatisticValidator.
     *
     * @param statistic The statistic to be validated.
     */
    public StatisticValidator(final Statistic statistic) {
        this.statistic = statistic;
    }

    /**
     * Indicates whether some other Statistic is "equal to" this one.
     *
     * @param obj The other Statistic.
     * @return true, is this Statistic is equal to the given object; false, if not.
     */
    public boolean isStatisticEqual(final Object obj) {
        if (this.statistic == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.statistic.getClass() != obj.getClass()) {
            return false;
        }
        Statistic other = (Statistic) obj;
        if (this.statistic.getDate() == null && other.getDate() != null) {
            return false;
        }
        if (this.statistic.getDate() != null && other.getDate() == null) {
            return false;
        }
        if (this.statistic.getDate() != null && other.getDate() != null) {
            if (this.statistic.getDate().getTime() != other.getDate().getTime()) {
                return false;
            }
        }

        return this.statistic.getAdvanceDeclineNumber() == other.getAdvanceDeclineNumber()
                && Objects.equals(this.statistic.getId(), other.getId())
                && Objects.equals(this.statistic.getIndustryGroupId(), other.getIndustryGroupId())
                && Objects.equals(this.statistic.getSectorId(), other.getSectorId())
                && this.statistic.getInstrumentType() == other.getInstrumentType()
                && this.statistic.getNumberAboveSma50() == other.getNumberAboveSma50()
                && this.statistic.getNumberAdvance() == other.getNumberAdvance()
                && this.statistic.getNumberAtOrBelowSma50() == other.getNumberAtOrBelowSma50()
                && this.statistic.getNumberAboveSma200() == other.getNumberAboveSma200()
                && this.statistic.getNumberAtOrBelowSma200() == other.getNumberAtOrBelowSma200()
                && this.statistic.getNumberDecline() == other.getNumberDecline()
                && this.statistic.getNumberOfInstruments() == other.getNumberOfInstruments()
                && this.statistic.getNumberRitterMarketTrend() == other.getNumberRitterMarketTrend()
                && this.statistic.getNumberUpOnVolume() == other.getNumberUpOnVolume()
                && this.statistic.getNumberDownOnVolume() == other.getNumberDownOnVolume()
                && this.statistic.getNumberBearishReversal() == other.getNumberBearishReversal()
                && this.statistic.getNumberBullishReversal() == other.getNumberBullishReversal()
                && this.statistic.getNumberChurning() == other.getNumberChurning()
                && Float.floatToIntBits(this.statistic.getPercentAboveSma50()) == Float
                        .floatToIntBits(other.getPercentAboveSma50())
                && Float.floatToIntBits(this.statistic.getPercentAboveSma200()) == Float
                        .floatToIntBits(other.getPercentAboveSma200());
    }
}
