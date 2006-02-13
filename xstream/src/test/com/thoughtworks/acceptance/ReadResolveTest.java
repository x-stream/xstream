package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.StatusEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Chris Kelly
 * @author Joe Walnes
 */ 
public class ReadResolveTest extends AbstractAcceptanceTest {

    public void testReadResolveWithDefaultSerialization() throws IOException, ClassNotFoundException {
        StatusEnum status = StatusEnum.STARTED;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(status);

        byte[] bArray = bout.toByteArray();
        StatusEnum rStatus = null;
        ObjectInputStream in = null;

        ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        rStatus = (StatusEnum) in.readObject();
        assertNotNull(rStatus);

        assertSame(status, rStatus);
    }

    public void testReadResolveWithXStream() {
        StatusEnum status = StatusEnum.STARTED;

        String xml = xstream.toXML(status);
        StatusEnum rStatus = (StatusEnum) xstream.fromXML(xml);

        assertSame(status, rStatus);
    }
}
