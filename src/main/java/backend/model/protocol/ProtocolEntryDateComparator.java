package backend.model.protocol;

import java.util.Comparator;

/**
 * Compares two protocol entries by their date.
 * 
 * @author Michael
 */
public class ProtocolEntryDateComparator implements Comparator<ProtocolEntry> {
	@Override
	public int compare(ProtocolEntry protocolEntry1, ProtocolEntry protocolEntry2) {
		if(protocolEntry1.getDate().getTime() < protocolEntry2.getDate().getTime())
			return 1;
		else if(protocolEntry1.getDate().getTime() > protocolEntry2.getDate().getTime())
			return -1;
		else
			return 0;
	}
}
