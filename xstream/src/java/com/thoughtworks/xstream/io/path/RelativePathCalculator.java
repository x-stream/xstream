package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.StringStack;

import java.util.ArrayList;
import java.util.List;

public class RelativePathCalculator {

    public String relativePath(String from, String to) {
        String[] fromBits = split(from);
        String[] toBits = split(to);
        StringBuffer result = new StringBuffer();
        boolean nothingWrittenYet = true;

        int depthOfPathDivergence = depthOfPathDivergence(fromBits, toBits);

        for (int i = depthOfPathDivergence; i < fromBits.length; i++) {
            if (nothingWrittenYet) {
                nothingWrittenYet = false;
            } else {
                result.append('/');
            }
            result.append("..");
        }

        for (int j = depthOfPathDivergence; j < toBits.length; j++) {
            if (nothingWrittenYet) {
                nothingWrittenYet = false;
            } else {
                result.append('/');
            }
            result.append(toBits[j]);
        }

        if (nothingWrittenYet) {
            return ".";
        } else {
            return result.toString();
        }
    }

    private String[] split(String str) {
        // String.split() too slow. StringTokenizer too crappy.
        List result = new ArrayList();
        int currentIndex = 0;
        int nextSeperator;
        while ((nextSeperator = str.indexOf('/', currentIndex)) != -1) {
            result.add(str.substring(currentIndex, nextSeperator));
            currentIndex = nextSeperator + 1;
        }
        result.add(str.substring(currentIndex));
        String[] arr = new String[result.size()];
        result.toArray(arr);
        return arr;
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

    public String absolutePath(String currentPath, String relativePathOfReference) {
        StringStack absoluteStack = new StringStack(16);

        String[] currentPathChunks = split(currentPath);
        for (int i = 0; i < currentPathChunks.length; i++) {
            absoluteStack.push(currentPathChunks[i]);
        }

        String[] relativeChunks = split(relativePathOfReference);
        for (int i = 0; i < relativeChunks.length; i++) {
            String relativeChunk = relativeChunks[i];
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

        return result.toString();
    }

}
