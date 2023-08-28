package backend.model.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

import backend.tools.DateTools;

/**
 * A list of quotations.
 *
 * @author Michael
 */
public class QuotationArray {
    /**
     * A list of quotations.
     */
    private List<Quotation> quotations;

    /**
     * Default constructor.
     */
    public QuotationArray() {
        this.quotations = new ArrayList<>();
    }

    /**
     * Initializes the QuotationArray with the given quotations.
     *
     * @param quotations The quotations.
     */
    public QuotationArray(final List<Quotation> quotations) {
        this.quotations = quotations;
    }

    /**
     * @return the quotations
     */
    @XmlElementWrapper(name = "quotations")
    @XmlElement(name = "quotation")
    public List<Quotation> getQuotations() {
        return quotations;
    }

    /**
     * @param quotations the quotations to set
     */
    public void setQuotations(final List<Quotation> quotations) {
        this.quotations = quotations;
    }

    /**
     * Returns all quotations of the Instrument with the given Id.
     *
     * @param instrumentId The instrument ID.
     * @return All quotations of the Instrument with the given ID.
     */
    public List<Quotation> getQuotationsByInstrumentId(final Integer instrumentId) {
        List<Quotation> quotationsOfInstrument = new ArrayList<>();

        for (Quotation quotation : this.quotations) {
            if (quotation.getInstrument() != null && quotation.getInstrument().getId().equals(instrumentId)) {
                quotationsOfInstrument.add(quotation);
            }
        }

        return quotationsOfInstrument;
    }

    /**
     * Gets the index of the Quotation with the given date. Intraday attributes of the date (hours, minutes, ...) are
     * not taken into account for lookup of matching Quotation. If no Quotation exists on the given day, the index of
     * the first Quotation coming afterwards is determined.
     *
     * @param date The date.
     * @return The index of the Quotation. -1, if no Quotation was found.
     */
    public int getIndexOfQuotationWithDate(final Date date) {
        Quotation quotation;
        Date quotationDate;
        Date inputDate;
        int indexOfQuotation = -1;

        inputDate = DateTools.getDateWithoutIntradayAttributes(date);

        for (int i = 0; i < this.quotations.size(); i++) {
            quotation = this.quotations.get(i);
            quotationDate = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());

            if (inputDate.getTime() <= quotationDate.getTime()) {
                indexOfQuotation = i;
            }
        }

        return indexOfQuotation;
    }

    /**
     * Checks if a Quotation with the given date exists. Intraday attributes of the date (hours, minutes, ...) are not
     * taken into account for lookup of matching Quotation.
     *
     * @param date The date.
     * @return true, if Quotation exists; false if not.
     */
    public boolean isQuotationOfDateExisting(final Date date) {
        Quotation quotation;
        Date quotationDate;
        Date inputDate;

        inputDate = DateTools.getDateWithoutIntradayAttributes(date);

        for (int i = 0; i < this.quotations.size(); i++) {
            quotation = this.quotations.get(i);
            quotationDate = DateTools.getDateWithoutIntradayAttributes(quotation.getDate());

            if (inputDate.getTime() == quotationDate.getTime()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the age of the newest Quotation in days.
     *
     * @return The age of the newest Quotation in days.
     */
    public long getAgeOfNewestQuotationInDays() {
        Quotation newestQuotation;
        Date currentDate = new Date();
        LocalDate currentDateLocal = LocalDate.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
        LocalDate newestQuotationDateLocal;
        long days;

        if (this.quotations == null || this.quotations.size() == 0) {
            return 0;
        }

        this.sortQuotationsByDate();

        newestQuotation = this.quotations.get(0);
        newestQuotationDateLocal = LocalDate.ofInstant(newestQuotation.getDate().toInstant(), ZoneId.systemDefault());

        days = ChronoUnit.DAYS.between(newestQuotationDateLocal, currentDateLocal);

        return days;
    }

    /**
     * Gets the quotations that are older than the given date but are still at the same day.
     *
     * @param date The date for which older quotations of the same day are requested.
     * @return Older quotations of the same day.
     */
    public List<Quotation> getOlderQuotationsOfSameDay(final Date date) {
        List<Quotation> olderQuotationsSameDay = new ArrayList<>();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate tempQuotationLocalDate;

        this.sortQuotationsByDate();

        for (Quotation tempQuotation : this.quotations) {
            tempQuotationLocalDate = tempQuotation.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (localDate.equals(tempQuotationLocalDate)) {
                if (tempQuotation.getDate().getTime() < date.getTime()) {
                    olderQuotationsSameDay.add(tempQuotation);
                }
            } else {
                // The quotations are sorted by date. All quotations coming afterwards are not at the same day.
                break;
            }
        }

        return olderQuotationsSameDay;
    }

    /**
     * Determines a List of weekly quotations based on the quotations stored in the QuotationArray.
     *
     * @return A List of weekly quotations.
     */
    public List<Quotation> getWeeklyQuotations() {
        List<Quotation> weeklyQuotations = new ArrayList<>();
        Quotation weeklyQuotation = new Quotation();
        Quotation previousQuotation = null;

        this.sortQuotationsByDate();

        for (Quotation dailyQuotation : this.quotations) {
            if (this.isNewWeek(dailyQuotation, previousQuotation)) {
                if (previousQuotation != null) {
                    weeklyQuotation.setDate(previousQuotation.getDate());
                    weeklyQuotation.setOpen(previousQuotation.getOpen());
                    weeklyQuotations.add(weeklyQuotation);
                }

                weeklyQuotation = new Quotation();
                weeklyQuotation.setClose(dailyQuotation.getClose());
                weeklyQuotation.setHigh(dailyQuotation.getHigh());
                weeklyQuotation.setLow(dailyQuotation.getLow());
                weeklyQuotation.setCurrency(dailyQuotation.getCurrency());
            } else {
                if (dailyQuotation.getHigh() != null
                        && (dailyQuotation.getHigh().doubleValue() > weeklyQuotation.getHigh().doubleValue())) {
                    weeklyQuotation.setHigh(dailyQuotation.getHigh());
                }

                if (dailyQuotation.getLow() != null
                        && (dailyQuotation.getLow().doubleValue() < weeklyQuotation.getLow().doubleValue())) {
                    weeklyQuotation.setLow(dailyQuotation.getLow());
                }
            }

            weeklyQuotation.setVolume(weeklyQuotation.getVolume() + dailyQuotation.getVolume());

            previousQuotation = dailyQuotation;
        }

        return weeklyQuotations;
    }

    /**
     * Checks two quotations to determine if the newer Quotation is part of a new week.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return true, if the current Quotation is part of a new week; false if not.
     */
    private boolean isNewWeek(final Quotation currentQuotation, final Quotation previousQuotation) {
        Calendar calendarCurrent = Calendar.getInstance();
        Calendar calendarPrevious = Calendar.getInstance();

        if (previousQuotation == null && currentQuotation instanceof Quotation) {
            return true;
        }

        if (currentQuotation == null) {
            return false;
        }

        calendarCurrent.setTime(currentQuotation.getDate());
        calendarPrevious.setTime(previousQuotation.getDate());

        if (calendarCurrent.get(Calendar.DAY_OF_WEEK) > calendarPrevious.get(Calendar.DAY_OF_WEEK)) {
            return true;
        }

        return false;
    }

    /**
     * Sorts all quotations by date.
     */
    public void sortQuotationsByDate() {
        Collections.sort(this.quotations, new QuotationDateComparator());
    }

    /**
     * Sorts all quotations by the symbol of their Instrument.
     */
    public void sortQuotationsBySymbol() {
        Collections.sort(this.quotations, new QuotationSymbolComparator());
    }

    /**
     * Determines the price high of all quotations within the QuotationArray.
     *
     * @return The price high.
     */
    public BigDecimal getPriceHigh() {
        BigDecimal high = new BigDecimal(0);

        for (Quotation quotation : this.quotations) {
            if (quotation.getHigh().doubleValue() > high.doubleValue()) {
                high = quotation.getHigh();
            }
        }

        return high;
    }

    /**
     * Determines the price low of all quotations within the QuotationArray.
     *
     * @return The price low.
     */
    public BigDecimal getPriceLow() {
        BigDecimal low = new BigDecimal(0);
        Quotation quotation;

        if (this.quotations.size() > 0) {
            quotation = this.quotations.get(0);
            low = quotation.getLow();
        } else {
            return low;
        }

        for (Quotation tempQuotation : this.quotations) {
            if (tempQuotation.getLow().doubleValue() < low.doubleValue()) {
                low = tempQuotation.getLow();
            }
        }

        return low;
    }
}
