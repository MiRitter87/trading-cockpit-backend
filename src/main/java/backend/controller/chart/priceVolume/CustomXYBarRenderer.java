package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;

import backend.calculator.PerformanceCalculator;
import backend.model.instrument.Instrument;
import backend.model.instrument.Quotation;

/**
 * A custom XYBarRenderer, that allows to set bar colors based on the price performance.
 *
 * @author MiRitter87
 */
public class CustomXYBarRenderer extends XYBarRenderer {
    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = 5254375026527844812L;

    /**
     * The Instrument whose volume data are being rendered.
     */
    private Instrument instrument;

    /**
     * The volume data.
     */
    private IntervalXYDataset volumeData;

    /**
     * Determines the color of the item.
     */
    @Override
    public Paint getItemPaint(final int row, final int column) {
        Quotation currentQuotation = this.instrument.getQuotations().get(this.volumeData.getItemCount(0) - column - 1);
        Quotation previousQuotation = null;

        if ((this.volumeData.getItemCount(0) - column) < this.volumeData.getItemCount(0)) {
            previousQuotation = this.instrument.getQuotations().get(this.volumeData.getItemCount(0) - column);
        }

        if (previousQuotation != null) {
            return this.getPerformanceBasedColor(currentQuotation, previousQuotation);
        } else {
            return getCloseBasedColor(currentQuotation);
        }
    }

    /**
     * Determines the bar color based on the relation of opening and closing price.
     *
     * @param quotation The Quotation.
     * @return The color.
     */
    private Color getCloseBasedColor(final Quotation quotation) {
        if (quotation == null) {
            return this.getNeutralBarColor();
        }

        if (quotation.getClose().floatValue() > quotation.getOpen().floatValue()) {
            return this.getUpBarColor();
        } else if (quotation.getClose().floatValue() < quotation.getOpen().floatValue()) {
            return this.getDownBarColor();
        } else {
            return this.getNeutralBarColor();
        }
    }

    /**
     * Determines the bar color based on the performance.
     *
     * @param currentQuotation  The current Quotation.
     * @param previousQuotation The previous Quotation.
     * @return The color.
     */
    private Color getPerformanceBasedColor(final Quotation currentQuotation, final Quotation previousQuotation) {
        PerformanceCalculator calculator = new PerformanceCalculator();
        float performance;

        if (currentQuotation == null || previousQuotation == null) {
            return this.getNeutralBarColor();
        }

        performance = calculator.getPerformance(currentQuotation, previousQuotation);

        if (performance > 0) {
            return this.getUpBarColor();
        } else if (performance < 0) {
            return this.getDownBarColor();
        } else {
            return this.getNeutralBarColor();
        }
    }

    /**
     * Gets the volume bar color of an up-day.
     *
     * @return The Color.
     */
    private Color getUpBarColor() {
        final int rValue = 0;
        final int gValue = 128;
        final int bValue = 0;

        return new Color(rValue, gValue, bValue);
    }

    /**
     * Gets the volume bar color of a down-day.
     *
     * @return The Color.
     */
    private Color getDownBarColor() {
        final int rValue = 255;
        final int gValue = 0;
        final int bValue = 0;

        return new Color(rValue, gValue, bValue);
    }

    /**
     * Gets the volume bar color of an unchanged day.
     *
     * @return The Color.
     */
    private Color getNeutralBarColor() {
        final int rValue = 87;
        final int gValue = 174;
        final int bValue = 251;

        return new Color(rValue, gValue, bValue);
    }

    /**
     * @return the instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * @param instrument the instrument to set
     */
    public void setInstrument(final Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * @return the volumeData
     */
    public IntervalXYDataset getVolumeData() {
        return volumeData;
    }

    /**
     * @param volumeData the volumeData to set
     */
    public void setVolumeData(final IntervalXYDataset volumeData) {
        this.volumeData = volumeData;
    }
}
