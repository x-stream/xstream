package com.thoughtworks.xstream.io;

/**
 * @author Joe Walnes
 */
public interface HierarchicalStreamWriter {

    void startNode(String name);

    void addAttribute(String name, String value);

    /**
     * Write the value (text content) of the current node.
     */
    void setValue(String text);

    void endNode();

    /**
     * Flush the writer, if necessary.
     */
    void flush();

    /**
     * Close the writer, if necessary.
     */
    void close();

    /**
     * Return the underlying HierarchicalStreamWriter implementation.
     *
     * <p>If a Converter needs to access methods of a specific HierarchicalStreamWriter implementation that are not
     * defined in the HierarchicalStreamWriter interface, it should call this method before casting. This is because
     * the writer passed to the Converter is often wrapped/decorated by another implementation to provide additional
     * functionality (such as XPath tracking).</p>
     *
     * <p>For example:</p>
     * <pre>MySpecificWriter mySpecificWriter = (MySpecificWriter)writer; <b>// INCORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();</pre>

     * <pre>MySpecificWriter mySpecificWriter = (MySpecificWriter)writer.underlyingWriter();  <b>// CORRECT!</b>
     * mySpecificWriter.doSomethingSpecific();</pre>
     *
     * <p>Implementations of HierarchicalStreamWriter should return 'this', unless they are a decorator, in which case
     * they should delegate to whatever they are wrapping.</p>
     */
    HierarchicalStreamWriter underlyingWriter();

}
