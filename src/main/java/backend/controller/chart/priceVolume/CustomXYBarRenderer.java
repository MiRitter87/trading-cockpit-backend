package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;

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
        Quotation quotation = this.instrument.getQuotations().get(this.volumeData.getItemCount(0) - column - 1);

        if (quotation.getClose().floatValue() > quotation.getOpen().floatValue()) {
            return Color.GREEN;
        } else if (quotation.getClose().floatValue() < quotation.getOpen().floatValue()) {
            return Color.RED;
        } else {
            return Color.BLUE;
        }
        /*
         * TODO Set color depending on performance. green on up-days, red on down-days, blue on neutral days.
         */
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
