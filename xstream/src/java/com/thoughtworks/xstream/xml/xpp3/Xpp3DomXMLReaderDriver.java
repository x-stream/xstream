package com.thoughtworks.xstream.xml.xpp3;

import com.thoughtworks.xstream.xml.CannotParseXMLException;
import com.thoughtworks.xstream.xml.XMLReader;
import com.thoughtworks.xstream.xml.XMLReaderDriver;

import java.io.StringReader;

public class Xpp3DomXMLReaderDriver
    implements XMLReaderDriver
{
    public XMLReader createReader( String xml )
    {
        try
        {
            return new Xpp3DomXMLReader( Xpp3DomBuilder.build( new StringReader( xml ) ) );
        }
        catch ( Exception e )
        {
            throw new CannotParseXMLException( e );
        }
    }
}
