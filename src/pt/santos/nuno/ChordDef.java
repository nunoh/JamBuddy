package pt.santos.nuno;

public class ChordDef {
	
	private int type;
	private String name;
	private String def;

	public ChordDef(int type, String name, String def) {
		this.type = type;
		this.name = name;
		this.def = def;
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
}