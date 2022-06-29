package backend.model.scan;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of scans.
 * 
 * @author Michael
 */
public class ScanArray {
	/**
	 * A list of scans.
	 */
	private List<Scan> scans;
	

	/**
	 * @return the scans
	 */
	@XmlElementWrapper(name="scans")
    @XmlElement(name="scan")
	public List<Scan> getScans() {
		return scans;
	}

	
	/**
	 * @param scans the scans to set
	 */
	public void setScans(List<Scan> scans) {
		this.scans = scans;
	}
}
