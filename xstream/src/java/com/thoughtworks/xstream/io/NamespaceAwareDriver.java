package com.thoughtworks.xstream.io;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

import java.io.Writer;

/**
 * A Namespace Aware Driver which must take control of how output is
 * created - rather than using the regular {@link PrettyPrintWriter}.
 *
 * @author James Strachan
 * @version $Revision: 1.1 $
 */
public interface NamespaceAwareDriver extends HierarchicalStreamDriver {

    HierarchicalStreamWriter createWriter(Writer out);
}
