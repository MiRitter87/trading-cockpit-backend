package backend.model.priceAlert;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of price alerts.
 *
 * @author Michael
 */
public class PriceAlertArray {
    /**
     * A list of price alerts.
     */
    private List<PriceAlert> priceAlerts = null;

    /**
     * @return the priceAlerts
     */
    @XmlElementWrapper(name = "priceAlerts")
    @XmlElement(name = "priceAlert")
    public List<PriceAlert> getPriceAlerts() {
        return priceAlerts;
    }

    /**
     * @param priceAlerts the priceAlerts to set
     */
    public void setPriceAlerts(final List<PriceAlert> priceAlerts) {
        this.priceAlerts = priceAlerts;
    }
}
