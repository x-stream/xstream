package com.thoughtworks.xstream.converters.collections;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Converts a java.util.TreeMap to XML, and serializes
 * the associated java.util.Comparator.
 *
 * @author Joe Walnes
 */
public class TreeMapConverter extends MapConverter {

    public TreeMapConverter(Mapper mapper) {
        super(mapper);
    }

    public boolean canConvert(Class type) {
        return type.equals(TreeMap.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        TreeMap treeMap = (TreeMap) source;
        Comparator comparator = treeMap.comparator();
        if (comparator == null) {
            writer.startNode("no-comparator");
            writer.endNode();
        } else {
            writer.startNode("comparator");
            writer.addAttribute("class", mapper().serializedClass(comparator.getClass()));
            context.convertAnother(comparator);
            writer.endNode();
        }
        super.marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        TreeMap result;
        if (reader.getNodeName().equals("comparator")) {
            String comparatorClass = reader.getAttribute("class");
            Comparator comparator = (Comparator) context.convertAnother(null, mapper().realClass(comparatorClass));
            result = new TreeMap(comparator);
        } else if (reader.getNodeName().equals("no-comparator")) {
            result = new TreeMap();
        } else {
            throw new ConversionException("TreeMap does not contain <comparator> element");
        }
        reader.moveUp();
        super.populateMap(reader, context, result);
        return result;
    }

}
