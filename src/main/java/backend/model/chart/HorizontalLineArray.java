package backend.model.chart;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of horizontal lines.
 * 
 * @author Michael
 */
public class HorizontalLineArray {
	/**
	 * A list of horizontal lines.
	 */
	private List<HorizontalLine> horizontalLines = null;

	
	/**
	 * @return the horizontalLines
	 */
	@XmlElementWrapper(name="horizontalLines")
    @XmlElement(name="horizontalLine")
	public List<HorizontalLine> getHorizontalLines() {
		return horizontalLines;
	}

	
	/**
	 * @param horizontalLines the horizontalLines to set
	 */
	public void setHorizontalLines(List<HorizontalLine> horizontalLines) {
		this.horizontalLines = horizontalLines;
	}
}
