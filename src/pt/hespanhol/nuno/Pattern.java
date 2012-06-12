package pt.hespanhol.nuno;


public class Pattern {
	
	// constants
	public static final char REST = '-';
	
	// variables
	private int type;
	private String name;
	private String def;
	
	public Pattern(int type, String name, String def) {
		this.type = type;
		this.name = name;
		this.def = def;
	}
	
	public Pattern(String name) {
		this(Api.getPattern(name).getType(), name, Api.getPattern(name).getDef());		
	}
	
	public String toString() {
		return type + " " + name + " " + def;
	}

	public int getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getDef() {
		return def;
	}
	
	public int getSize() {
		return def.length();
	}
	
}
