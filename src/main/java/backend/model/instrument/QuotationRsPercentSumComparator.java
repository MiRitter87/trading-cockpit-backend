package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by the rsPercentSum of their Indicator.
 * 
 * @author Michael
 */
public class QuotationRsPercentSumComparator implements Comparator<Quotation> {
	@Override
	public int compare(Quotation o1, Quotation o2) {
		//Handle possible null values of Indicator.
		if(o1.getIndicator() == null && o2.getIndicator() == null )
			return 0;
		
		if(o1.getIndicator() == null && o2.getIndicator() != null)
			return 1;
		
		if(o1.getIndicator() != null && o2.getIndicator() == null)
			return -1;
		
		//Compare if both indicators are defined.
		if(o1.getIndicator().getRsPercentSum() > o2.getIndicator().getRsPercentSum())
			return -1;
		else if(o1.getIndicator().getRsPercentSum() < o2.getIndicator().getRsPercentSum())
			return 1;
		else
			return 0;
	}
}
