package com.thoughtworks.xstream.converters.reflection;

import java.util.Map;

/**
 * An interface capable of sorting fields. Implement this interface if you want
 * to customize the field order in which XStream serializes objects.
 *
 * @author Guilherme Silveira
 * @since upcoming
 */
public interface FieldKeySorter {

	Map sort(Class definedIn, Map keyedByFieldKey);

}
