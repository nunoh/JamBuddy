package pt.santos.nuno;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.Track;

/*
 * A progression has many chordprogs (with a number of bars and pattern)
 * and 
 */
public class Progression implements Iterable<ChordProg> {
	
	private ArrayList<ChordProg> chords;
	private int bpm;
	private int bar = 0; // the ongoin bar during generation
	
	private boolean sameBar = false;
	
	public Progression() {
		chords = new ArrayList<ChordProg>();
	}
	
	public Progression(String sProgression) {
		
		chords = new ArrayList<ChordProg>();
		
		String tokens[] = sProgression.split("\\" + Api.chordsDelimiter);
		for (int i = 1; i < tokens.length; i++) {
			String token = tokens[i].trim();
			if (token.contains(" ")) {
				String chords12[] = token.split(" ");
				String chord1 = chords12[0];
				String chord2 = chords12[1];
				chords.add(new ChordProg(new Chord(chord1), 2));
				chords.add(new ChordProg(new Chord(chord2), 2));
			}
			else { 
				chords.add(new ChordProg(new Chord(token), 4));
			}
		}
	}
	
	public Progression(ArrayList<ChordProg> chords) {
		this.chords = chords; 
	}
	
	public void addChord(Chord chord, int bars) {
		chords.add(new ChordProg(chord, bars));
	}
	
	public void addChord(Chord chord) {
		chords.add(new ChordProg(chord, 1));
	}

	public void generate(Track track) {		
		for (int i = 0; i < chords.size(); i++) {		
			// if first chord, then no voice leading, just play the first chord in root position
			if (i == 0) {
				playPattern(chords.get(i), track);
			} 
			else {
				voiceLeading(chords.get(i-1), chords.get(i));
				playPattern(chords.get(i), track);
			}
			
		}
	}

	private void voiceLeading(ChordProg c1, ChordProg c2) {
				
		// the resulting voicing of the chord
		List<Note> ret = new ArrayList<Note>();
		
		// notes of the second chord that aren't in the first one
		List<Note> difs = new ArrayList<Note>();
		
		for (Note n1 : c1) {
			for (Note n2 : c2) {
				// maitain the common notes (using pitch class to compare)
				if (c2.contains(n1) && !ret.contains(n1)) 
					ret.add(n1);				
				else if (!c1.contains(n2) && !difs.contains(n2))
					difs.add(n2);
			}
		}
		
//		System.out.println("ret : " + ret);
//		System.out.println("difs : " + difs);

		// for each pitch class of the 2nd chord that isn't in the 1st chord, 
		// check which pitch of that pitch class note is nearest to any pitch 
		// of the notes of the 1st chord
		for (Note n2 : difs) {						
			int min = Integer.MAX_VALUE;
			int opt = 0;
			for (Note n1 : c1) {
				// there are only 2 options for each note
				int opt1 = n1.getOctave()*12 + n2.getPitchClass();	// one octave below
				int opt2 = (n1.getOctave()-1)*12 + n2.getPitchClass(); // one octave above
				int dif1 = Math.abs(n1.getPitch() - opt1); 
				int dif2 = Math.abs(n1.getPitch() - opt2);
				// keep the note with less distance
				if (dif1 < min) {
					min = dif1;
					opt = opt1;
				}
				if (dif2 < min) {
					min = dif2;
					opt = opt2;
				}
			}
			ret.add(new Note(opt));
		}
		
		// sort array by pitch
		Collections.sort(ret);
		
		c2.setNotes(ret);
		
//		System.out.println("ret : " + ret);		
	}

	public void setPattern(Pattern pattern) {
		for (ChordProg chord : chords) {
			chord.setPattern(pattern);
		}
	}
	
	public int getBPM() {
		return this.bpm;
	}
	
	public void setBPM(int bpm) {
		this.bpm = bpm;
	}
	
	public void playPattern(ChordProg chord, Track track) {
				
		System.out.println(chord.getRoot() + " " + chord.getBeats());
				
		for (int i = 0; i < chord.getPatternSize(); i++) {
			int iNote = chord.getPattern(i);
			if (iNote > chord.getNumNotes()) {
				System.err.println("not playing note, because it doesn't fit in chord.");
				continue;
			}
			
			else if (i+1 > chord.getBeats()) {
				System.err.println("not playing note, because it doesn't fit in bar.");
				if (sameBar) { sameBar = false; bar++; }
				else sameBar = true;
				return;
			}
			else if (iNote == -1) {
				// is a rest, do nothing
			}
			else {
				Note n = chord.getNote(iNote-1);				
				if (!sameBar) {
					n.put(track, bar+1, i+1);
				}
				else { 
					n.put(track, bar+1, i+1+2);
				}
			}
		}
		if (!sameBar) bar++;
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (ChordProg chord : chords)
			ret += chord + "\n";
		return ret;
	}
	
	public ArrayList<ChordProg> getChords() {
		return chords;
	}

	public Iterator<ChordProg> iterator() {
		return chords.iterator();
	}
		
}
