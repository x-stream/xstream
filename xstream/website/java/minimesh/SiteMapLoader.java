package minimesh;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Loads a SiteMap from an XML file.
 *
 * @author Joe Walnes
 */
public class SiteMapLoader {

    public SiteMap loadFrom(File contentXml) throws IOException {
        XStream xstream = new XStream();
        xstream.alias("section", Section.class);
        xstream.alias("page", Page.class);
        xstream.alias("link", Link.class);
        xstream.alias("sitemap", SiteMap.class);
        xstream.addImplicitCollection(Section.class, "pages");
        xstream.addImplicitCollection(SiteMap.class, "sections");
        xstream.registerConverter(new PageConverter(contentXml.getParentFile()));
        xstream.registerConverter(new LinkConverter());

        Reader reader = new FileReader(contentXml);
        try {
            return (SiteMap) xstream.fromXML(reader);
        } finally {
            reader.close();
        }
    }

    private static class PageConverter extends AbstractBasicConverter {

        private final File baseDirectory;

        public PageConverter(File baseDirectory) {
            this.baseDirectory = baseDirectory;
        }

        public boolean canConvert(Class type) {
            return type == Page.class;
        }

        protected Object fromString(String text) {
            return new Page(new File(baseDirectory, text));
        }

        protected String toString(Object o) {
            Page page = (Page) o;
            return page.getFilename();
        }
    }

    private static class LinkConverter implements Converter {

        public boolean canConvert(Class type) {
            return type == Link.class;
        }

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Link link = (Link) source;
            writer.addAttribute("title", link.getTitle());
            writer.setValue(link.getHref());
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            String title = reader.getAttribute("title");
            String href = reader.getValue();
            return new Link(title, href);
        }
    }
}
