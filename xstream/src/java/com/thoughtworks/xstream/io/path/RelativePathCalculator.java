package com.thoughtworks.xstream.io.path;

import com.thoughtworks.xstream.core.util.StringStack;

public class RelativePathCalculator {

    public String relativePath(String from, String to) {
        String[] fromBits = from.split("/");
        String[] toBits = to.split("/");
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

        String[] currentPathChunks = currentPath.split("/");
        for (int i = 0; i < currentPathChunks.length; i++) {
            absoluteStack.push(currentPathChunks[i]);
        }

        String[] relativeChunks = relativePathOfReference.split("/");
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
