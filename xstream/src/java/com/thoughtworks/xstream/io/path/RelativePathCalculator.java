package com.thoughtworks.xstream.io.path;

/**
 * @deprecated Use the Path object instead.
 * @see Path
 */
public class RelativePathCalculator {

    /**
     * @deprecated Use {@link Path#relativeTo(Path)} instead.
     */
    public String relativePath(String from, String to) {
        return new Path(from).relativeTo(new Path(to)).toString();
    }

    /**
     * @deprecated Use {@link Path#apply(Path)} instead.
     */
    public String absolutePath(String currentPath, String relativePathOfReference) {
        return new Path(currentPath).apply(new Path(relativePathOfReference)).toString();
    }

}
