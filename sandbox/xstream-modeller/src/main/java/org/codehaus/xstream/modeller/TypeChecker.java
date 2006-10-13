package org.codehaus.xstream.modeller;

public class TypeChecker {

	public static boolean isLong(String value) {
		try {
			Long.parseLong(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

}
