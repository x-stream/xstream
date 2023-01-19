package com.thoughtworks.xstream.converters.awt;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.awt.DisplayMode;

public class DisplayModeConverter implements Converter {

	@Override
	public boolean canConvert(final Class<?> type) {
		return type == DisplayMode.class;
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
		final DisplayMode displayMode = (DisplayMode) source;

		writer.startNode("width");
		writer.setValue(String.valueOf(displayMode.getWidth()));
		writer.endNode();

		writer.startNode("height");
		writer.setValue(String.valueOf(displayMode.getHeight()));
		writer.endNode();

		writer.startNode("bitDepth");
		writer.setValue(String.valueOf(displayMode.getBitDepth()));
		writer.endNode();

		writer.startNode("refreshRate");
		writer.setValue(String.valueOf(displayMode.getRefreshRate()));
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

		reader.moveDown();
		final int bitDepth = Integer.parseInt(reader.getValue());
		reader.moveUp();

		reader.moveDown();
		final int refreshRate = Integer.parseInt(reader.getValue());
		reader.moveUp();

		return new DisplayMode(width, height, bitDepth, refreshRate);
	}
}
