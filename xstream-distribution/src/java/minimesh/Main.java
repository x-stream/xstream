package minimesh;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Command line entry point for building website.
 *
 * @author Joe Walnes
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java " + Main.class.getName()
                    + " <path-to-website.xml> <path-to-skin> <output-dir>");
            System.exit(-1);
        }

        File websitefile = new File(args[0]);
        File skinFile = new File(args[1]);
        File outputDirectory = new File(args[2]);

        // Load sitemap and content
        SiteMapLoader siteMapLoader = new SiteMapLoader();
        SiteMap siteMap = siteMapLoader.loadFrom(websitefile);

        // Apply skin to each page
        Skin skin = new Skin(skinFile);
        outputDirectory.mkdirs();
        for (Iterator iterator = siteMap.getAllPages().iterator(); iterator.hasNext();) {
            Page page = (Page) iterator.next();
            System.out.println("Skinning " + page.getFilename() + " (" + page.getTitle() + ")");
            skin.skin(page, siteMap, outputDirectory);
        }

        // Copy additional resources (css, images, etc) to output
        FileSystem fileSystem = new FileSystem();
        fileSystem.copyAllFiles(websitefile.getParentFile(), outputDirectory, "html,xml");
        fileSystem.copyAllFiles(skinFile.getParentFile(), outputDirectory, "html,xml");

        // Verify links
        LinkChecker linkChecker = new LinkChecker(siteMap, new LinkChecker.Reporter() {
            public void badLink(Page page, String link) {
                System.err.println("Invalid link on page " + page.getFilename() + " : " + link);
            }
        });
        if (!linkChecker.verify()) {
            System.err.println("INVALID LINKS FOUND");
            System.exit(-1);
        }
    }

}
