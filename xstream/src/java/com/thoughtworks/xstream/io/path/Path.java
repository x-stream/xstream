package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.FastStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a path (subset of XPath) to a single node in the tree.
 *
 * <p>Two absolute paths can also be compared to calculate the relative path between them.
 * A relative path can be applied to an absolute path to calculate another absolute path.</p>
 *
 * <p>Note that the paths produced are XPath compliant, so can be read by other XPath engines.
 * The following are examples of path expressions that the Path object supports:</p>
 * <ul>
 *     <li>/</li>
 *     <li>/some/node</li>
 *     <li>/a/b/c/b/a</li>
 *     <li>/some[3]/node[2]/a</li>
 *     <li>../../../another[3]/node</li>
 * </ul>
 *
 * <h3>Example<h3>
 *
 * <pre>
 * Path a = new Path("/html/body/div/table[2]/tr[3]/td/div");
 * Path b = new Path("/html/body/div/table[2]/tr[6]/td/form");
 *
 * Path relativePath = a.relativeTo(b); // produces: "../../../tr[6]/td/form"
 * Path c = a.apply(relativePath); // same as Path b.
 * </pre>
 *
 * @see PathTracker
 *
 * @author Joe Walnes
 */
public class Path {

    private final String[] chunks;
    private transient String pathAsString;
    private static final Path DOT = new Path(new String[] {"."});

    public Path(String pathAsString) {
        // String.split() too slow. StringTokenizer too crappy.
        List result = new ArrayList();
        int currentIndex = 0;
        int nextSeperator;
        while ((nextSeperator = pathAsString.indexOf('/', currentIndex)) != -1) {
            result.add(pathAsString.substring(currentIndex, nextSeperator));
            currentIndex = nextSeperator + 1;
        }
        result.add(pathAsString.substring(currentIndex));
        String[] arr = new String[result.size()];
        result.toArray(arr);
        chunks = arr;
        this.pathAsString = pathAsString;
    }

    public Path(String[] chunks) {
        this.chunks = chunks;
    }

    public String toString() {
        if (pathAsString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < chunks.length; i++) {
                if (i > 0) buffer.append('/');
                buffer.append(chunks[i]);
            }
            pathAsString = buffer.toString();
        }
        return pathAsString;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;

        final Path other = (Path) o;
        if (chunks.length != other.chunks.length) return false;
        for (int i = 0; i < chunks.length; i++) {
            if (!chunks[i].equals(other.chunks[i])) return false;
        }

        return true;
    }

    public int hashCode() {
        int result = 543645643;
        for (int i = 0; i < chunks.length; i++) {
            result = 29 * result + chunks[i].hashCode();
        }
        return result;
    }

    public Path relativeTo(Path that) {
        int depthOfPathDivergence = depthOfPathDivergence(chunks, that.chunks);
        String[] result = new String[chunks.length + that.chunks.length - 2 * depthOfPathDivergence];
        int count = 0;

        for (int i = depthOfPathDivergence; i < chunks.length; i++) {
            result[count++] = "..";
        }
        for (int j = depthOfPathDivergence; j < that.chunks.length; j++) {
            result[count++] = that.chunks[j];
        }

        if (count == 0) {
            return DOT;
        } else {
            return new Path(result);
        }
    }

    private int depthOfPathDivergence(String[] path1, String[] path2) {
        int minLength = Math.min(path1.length, path2.length);
        for (int i = 0; i < minLength; i++) {
            if (!path1[i].equals(path2[i])) {
                return i;
            }
        }
        return minLength;
    }

    public Path apply(Path relativePath) {
        FastStack absoluteStack = new FastStack(16);

        for (int i = 0; i < chunks.length; i++) {
            absoluteStack.push(chunks[i]);
        }

        for (int i = 0; i < relativePath.chunks.length; i++) {
            String relativeChunk = relativePath.chunks[i];
            if (relativeChunk.equals("..")) {
                absoluteStack.pop();
            } else if (!relativeChunk.equals(".")) {
                absoluteStack.push(relativeChunk);
            }
        }

        String[] result = new String[absoluteStack.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (String) absoluteStack.get(i);
        }

        return new Path(result);
    }
    
    public boolean isAncestor(Path child) {
        if (child == null || child.chunks.length < chunks.length) {
            return false;
        }
        for (int i = 0; i < chunks.length; i++) {
            if (!chunks[i].equals(child.chunks[i])) {
                return false;
            }
        }
        return true;
    }
}
