package com.thoughtworks.xstream.xml.xpp3;

import com.thoughtworks.xstream.xml.XMLWriter;

import java.util.LinkedList;

public class Xpp3DomXMLWriter
    implements XMLWriter
{
    private LinkedList elementStack = new LinkedList();

    private Xpp3Dom configuration;

    public Xpp3DomXMLWriter()
    {
    }

    public Xpp3Dom getConfiguration()
    {
        return configuration;
    }

    public void startElement( String name )
    {
        Xpp3Dom configuration = new Xpp3Dom( name );

        if ( this.configuration == null )
        {
            this.configuration = configuration;
        }
        else
        {
            top().addChild( configuration );
        }

        elementStack.addLast( configuration );
    }

    public void writeText( String text )
    {
        top().setValue( text );
    }

    public void addAttribute( String key, String value )
    {
        top().setAttribute( key, value );
    }

    public void endElement()
    {
        elementStack.removeLast();
    }

    private Xpp3Dom top()
    {
        return (Xpp3Dom) elementStack.getLast();
    }
}
