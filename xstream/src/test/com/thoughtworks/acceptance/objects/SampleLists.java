package com.thoughtworks.acceptance.objects;

import java.util.ArrayList;
import java.util.List;

public class SampleLists {
    public List good = new ArrayList();
    public List bad = new ArrayList();

    public boolean equals(Object obj) {
        if (obj instanceof SampleLists) {
            SampleLists sampleLists = (SampleLists) obj;
            return good.equals(sampleLists.good)
                    && bad.equals(sampleLists.bad);
        }
        return false;
    }
}
