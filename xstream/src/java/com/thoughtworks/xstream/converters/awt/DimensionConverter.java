package com.thoughtworks.xstream.converters.awt;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.awt.Dimension;

public class DimensionConverter implements Converter {
	@Override
	public boolean canConvert(final Class<?> type) {
		return type == Dimension.class;
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final Dimension dimension = (Dimension) source;

		writer.startNode("width");
		writer.setValue(String.valueOf(dimension.getWidth()));
		writer.endNode();

		writer.startNode("height");
		writer.setValue(String.valueOf(dimension.getHeight()));
		writer.endNode();
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
		reader.moveDown();
		final int width = Integer.parseInt(reader.getValue());
		reader.moveUp();

		reader.moveDown();
		final int height = Integer.parseInt(reader.getValue());
		reader.moveUp();

		return new Dimension(width, height);
	}
}

