package backend.model.list;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;

/**
 * A list of lists.
 *
 * @author Michael
 */
public class ListArray {
    /**
     * A list of lists.
     */
    private List<backend.model.list.List> lists;

    /**
     * @return the lists
     */
    @XmlElementWrapper(name = "lists")
    @XmlElement(name = "list")
    public List<backend.model.list.List> getLists() {
        return lists;
    }

    /**
     * @param lists the lists to set
     */
    public void setLists(final List<backend.model.list.List> lists) {
        this.lists = lists;
    }
}
