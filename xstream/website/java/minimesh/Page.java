package minimesh;

import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.parser.HTMLPageParser;

import java.io.File;
import java.io.IOException;

/**
 * A single page in a website, including title, filename and content.
 *
 * All this information is loaded from an HTML file (using the SiteMesh library).
 *
 * @author Joe Walnes
 */
public class Page {

    private final HTMLPageParser htmlPageParser = new HTMLPageParser();
    private final FileSystem fileSystem = new FileSystem();

    private final HTMLPage sitemeshPage;
    private final String filename;

    public Page(File htmlFile) {
        try {
            sitemeshPage = (HTMLPage) htmlPageParser.parse(fileSystem.readFile(htmlFile));
            filename = htmlFile.getName();
        } catch (IOException e) {
            throw new CannotParsePageException(e);
        }
    }

    public String getTitle() {
        if (sitemeshPage.isPropertySet("meta.short")) {
            return sitemeshPage.getProperty("meta.short");
        } else {
            return sitemeshPage.getTitle();
        }
    }

    public String getHead() {
        return sitemeshPage.getHead();
    }

    public String getBody() {
        return sitemeshPage.getBody();
    }

    public String getFilename() {
        return filename;
    }

    public String getHref() {
        return getFilename();
    }

    public static class CannotParsePageException extends RuntimeException {
        public CannotParsePageException(Throwable cause) {
            super(cause);
        }
    }
}
