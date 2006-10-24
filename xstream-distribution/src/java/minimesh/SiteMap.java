package minimesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the structure of a website.
 *
 * @author Joe Walnes
 */
public class SiteMap {

    private List sections = new ArrayList();
    private List pages = new ArrayList();

    public void addSection(Section section) {
        sections.add(section);
    }

    public void addPage(Page page) {
        pages.add(page);
    }

    public List getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List getAllPages() {
        List result = new ArrayList();
        for (Iterator i = sections.iterator(); i.hasNext();) {
            Section section = (Section) i.next();
            for (Iterator iterator = section.getPages().iterator(); iterator.hasNext();) {
                Object item = iterator.next();
                if (item instanceof Page) {
                    result.add(item);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }

}
