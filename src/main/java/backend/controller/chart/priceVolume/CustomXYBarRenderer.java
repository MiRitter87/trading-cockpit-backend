package backend.controller.chart.priceVolume;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.renderer.xy.XYBarRenderer;

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
     * Determines the color of the item.
     */
    @Override
    public Paint getItemPaint(final int row, final int column) {
        /**
         * TODO Set color depending on performance. green on up-days, red on down-days, blue on neutral days.
         */
        return Color.BLUE;
    }
}
