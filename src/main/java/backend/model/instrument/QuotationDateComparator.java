package backend.model.instrument;

import java.util.Comparator;

/**
 * Compares two quotations by their date.
 * 
 * @author Michael
 */
public class QuotationDateComparator implements Comparator<Quotation> {
	@Override
	public int compare(Quotation quotation1, Quotation quotation2) {
		if(quotation1.getDate().getTime() < quotation2.getDate().getTime())
			return 1;
		else if(quotation1.getDate().getTime() > quotation2.getDate().getTime())
			return -1;
		else
			return 0;
	}
}
