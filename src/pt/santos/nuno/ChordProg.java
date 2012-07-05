package pt.santos.nuno;

import java.util.ArrayList;

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
	
	public int getPatternNote(int i) {
		char c = Api.pattern.getDef().charAt(i);
		int ret;
		if (c == Pattern.REST)
			ret = -1;
		else
			ret = Integer.parseInt(""+c);
		return ret;
	}
	
	// return an array with the notes of the pattern vertically (same beat)
	public ArrayList<Integer> getPatternNotes(int i) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String[] lines = Api.pattern.getLines();
		for (String s : lines) {
			char c = s.charAt(i);
			ret.add(Integer.parseInt(""+c));
		}
		return ret;		
	}
	
	public Pattern getPattern() {
		return Api.pattern;
	}

}
