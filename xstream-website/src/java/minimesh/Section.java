package minimesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A section in a website, holding pages.
 *
 * @author Joe Walnes
 */
public class Section {

    private final String name;
    private final List pages = new ArrayList();

    public Section(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List getPages() {
        return Collections.unmodifiableList(pages);
    }

    public void addPage(Page page) {
        pages.add(page);
    }
}
