package org.codehaus.xstream.modeller;

public enum Quantity {

	ONE {
		public String getCodeAsMember(String type, String name) {
			return "\tprivate " + type + " " + name + ";\n";
		}
	}

	,
	MANY {
		public String getCodeAsMember(String type, String name) {
			return "\tprivate List<" + type + "> " + name + ";\n";
		}
	}

	;

	public abstract String getCodeAsMember(String type, String name);

}
