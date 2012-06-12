package pt.hespanhol.nuno;

public class ChordProg extends Chord {
	
	private int bars;
	private Pattern pattern;
	
	public ChordProg(Chord chord, int bars) {
		super(chord.root, chord.type);
		this.bars = bars;
	}
	
	public int getBars() {
		return this.bars;
	}
		
	public String toString() {
		return super.toString() + " x " + bars;
	}
	
	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public int getPattern(int i) {
		char c = this.pattern.getDef().charAt(i);
		int ret;
		if (c == Pattern.REST)
			ret = -1;
		else
			ret = Integer.parseInt(""+c);
		return ret;
	}

	public int getPatternSize() {
		return this.pattern.getDef().length();
	}
			
}
