package pt.santos.nuno;

/*
 * A chord inside a progression has both a number of bars and a pattern associated
 */
public class ChordProg extends Chord {
	
	private int bars;
	
	public ChordProg(Chord chord, int bars) {
		super(chord.root, chord.type);
		this.bars = bars;
	}
	
	public int getBeats() {
		return this.bars;
	}
		
	public String toString() {
		return super.toString() + " x " + bars;
	}
	
	public int getPattern(int i) {
		char c = Api.pattern.getDef().charAt(i);
		int ret;
		if (c == Pattern.REST)
			ret = -1;
		else
			ret = Integer.parseInt(""+c);
		return ret;
	}

}
