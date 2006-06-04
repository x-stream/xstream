package com.thoughtworks.xstream.io.binary;

import junit.framework.TestCase;

import java.io.*;

public class TokenTest extends TestCase {

    private Token.Formatter tokenFormatter;
    private ByteArrayOutputStream buffer;
    private DataOutput out;

    protected void setUp() throws Exception {
        super.setUp();
        tokenFormatter = new Token.Formatter();
        buffer = new ByteArrayOutputStream();
        out = new DataOutputStream(buffer);
    }

    public void testDoesNotSupportNegativeIds() throws IOException {
        Token.StartNode token = new Token.StartNode(-5);
        try {
            writeOneToken(token);
            fail("Expected exception");
        } catch (IOException expectedException) {
            // expected exception
        }
    }

    public void testUsesOneExtraByteForIdsThatCanBeRepresentedAsByte() throws IOException {
        Token.StartNode token = new Token.StartNode(255);
        writeOneToken(token);
        assertEquals(2, buffer.size()); // One byte already written for token type.
        assertEquals(token, readOneToken());
    }

    public void testUsesTwoExtraBytesForIdsThatCanBeRepresentedAsShort() throws IOException {
        Token.StartNode token = new Token.StartNode(30000);
        writeOneToken(token);
        assertEquals(3, buffer.size()); // One byte already written for token type.
        assertEquals(token, readOneToken());
    }

    public void testUsesFourExtraBytesForIdsThatCanBeRepresentedAsShort() throws IOException {
        Token.StartNode token = new Token.StartNode(Integer.MAX_VALUE);
        writeOneToken(token);
        assertEquals(5, buffer.size()); // One byte already written for token type.
        assertEquals(token, readOneToken());
    }

    public void testUsesEightExtraBytesForIdsThatCanBeRepresentedAsLong() throws IOException {
        Token.StartNode token = new Token.StartNode(324234325543L);
        writeOneToken(token);
        assertEquals(9, buffer.size()); // One byte already written for token type.
        assertEquals(token, readOneToken());
    }

    private Token readOneToken() throws IOException {
        return tokenFormatter.read(new DataInputStream(new ByteArrayInputStream(buffer.toByteArray())));
    }

    private void writeOneToken(Token.StartNode token) throws IOException {
        tokenFormatter.write(out, token);
    }

}
