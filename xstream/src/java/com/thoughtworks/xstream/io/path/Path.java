package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.FastStack;

import java.util.List;
import java.util.ArrayList;

public class Path {

    private final String[] chunks;
    private transient String pathAsString;

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

    public Path(String[] chunks, String pathAsString) {
        this.chunks = chunks;
        this.pathAsString = pathAsString;
    }

    public String toString() {
        if (pathAsString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < chunks.length; i++) {
                buffer.append('/');
                buffer.append(chunks[i]);
            }
            pathAsString = buffer.toString();
        }
        return pathAsString.toString();
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
        // todo: more unique algorithm
        int result = 543645643;
        for (int i = 0; i < chunks.length; i++) {
            result += chunks[i].hashCode() * i;
        }
        return result;
    }

    public Path relativeTo(Path that) {
        StringBuffer result = new StringBuffer();
        boolean nothingWrittenYet = true;

        int depthOfPathDivergence = depthOfPathDivergence(chunks, that.chunks);

        for (int i = depthOfPathDivergence; i < chunks.length; i++) {
            if (nothingWrittenYet) {
                nothingWrittenYet = false;
            } else {
                result.append('/');
            }
            result.append("..");
        }

        for (int j = depthOfPathDivergence; j < that.chunks.length; j++) {
            if (nothingWrittenYet) {
                nothingWrittenYet = false;
            } else {
                result.append('/');
            }
            result.append(that.chunks[j]);
        }

        if (nothingWrittenYet) {
            return new Path(".");
        } else {
            return new Path(result.toString());
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

        StringBuffer result = new StringBuffer();
        int size = absoluteStack.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                result.append('/');
            }
            result.append(absoluteStack.get(i));
        }

        return new Path(result.toString());
    }
}
