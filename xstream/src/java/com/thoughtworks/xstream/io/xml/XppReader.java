package com.thoughtworks.xstream.io.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.io.StreamException;

/**
 * XStream reader that pulls directly from the stream using the XmlPullParser API.
 *
 * @author Joe Walnes
 */
public class XppReader extends AbstractPullReader {

    private final XmlPullParser parser;
    private final BufferedReader reader;

    public XppReader(Reader reader) {
        this(reader, new XmlFriendlyReplacer());
    }

    /**
     * @since 1.2
     */
    public XppReader(Reader reader, XmlFriendlyReplacer replacer) {
        super(replacer);
        try {
            parser = createParser();
            this.reader = new BufferedReader(reader);
            parser.setInput(this.reader);
            moveDown();
        } catch (XmlPullParserException e) {
            throw new StreamException(e);
        }
    }
    
    /**
     * To use another implementation of org.xmlpull.v1.XmlPullParser, override this method.
     */
    protected XmlPullParser createParser() {
        return new MXParser();
    }

    protected int pullNextEvent() {
        try {
            switch(parser.next()) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.START_TAG:
                    return START_NODE;
                case XmlPullParser.END_DOCUMENT:
                case XmlPullParser.END_TAG:
                    return END_NODE;
                case XmlPullParser.TEXT:
                    return TEXT;
                case XmlPullParser.COMMENT:
                    return COMMENT;
                default:
                    return OTHER;
            }
        } catch (XmlPullParserException e) {
            throw new StreamException(e);
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

    protected String pullElementName() {
        return parser.getName();
    }

    protected String pullText() {
        return parser.getText();
    }

    public String getAttribute(String name) {
        return parser.getAttributeValue(null, name);
    }

    public String getAttribute(int index) {
        return parser.getAttributeValue(index);
    }

    public int getAttributeCount() {
        return parser.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return unescapeXmlName(parser.getAttributeName(index));
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("line number", String.valueOf(parser.getLineNumber()));
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new StreamException(e);
        }
    }

}