package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.FastStack;

import java.util.List;
import java.util.ArrayList;

public class Path {

    private String[] chunks;
    private String s;

    public Path(String s) {
        // String.split() too slow. StringTokenizer too crappy.
        List result = new ArrayList();
        int currentIndex = 0;
        int nextSeperator;
        while ((nextSeperator = s.indexOf('/', currentIndex)) != -1) {
            result.add(s.substring(currentIndex, nextSeperator));
            currentIndex = nextSeperator + 1;
        }
        result.add(s.substring(currentIndex));
        String[] arr = new String[result.size()];
        result.toArray(arr);
        chunks = arr;
        this.s = s;
    }

    public Path(String[] chunks, String s) {
        this.chunks = chunks;
        this.s = s;
    }

    public String toString() {
        return s.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;

        final Path path = (Path) o;

        if (s != null ? !s.equals(path.s) : path.s != null) return false;

        return true;
    }

    public int hashCode() {
        return (s != null ? s.hashCode() : 0);
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
