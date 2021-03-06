package gam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.Track;

public class Progression implements Iterable<ChordProg> {

	private ArrayList<ChordProg> chords;
	private int bpm;
	private int bar = 0; // the ongoin bar during generation

//	private boolean sameBar = false;

	public Progression() {
		chords = new ArrayList<ChordProg>();
	}

	public Progression(String[] progression) {

		chords = new ArrayList<ChordProg>();

		for (int i = 0; i < progression.length; i++) {
			handleProgressionLine(progression[i]);
		}			

		//		System.out.println("progression is " + chords.toString());
	}

	//TODO change chord duration from 2 and 4 constants
	private void handleProgressionLine(String progression) {
		String tokens[] = progression.split("\\" + Api.CHORDS_DELIMITER);
		for (int i = 1; i < tokens.length; i++) {

			String token = tokens[i].trim();

			if (token.equals(Api.CHORD_PREVIOUS_SYMBOL)) {
				token = tokens[i-1];
			}

			//TODO better support for two chords in a bar
			if (token.contains(" ")) {
				//				String chords12[] = token.split(" ");
				//				String chord1 = chords12[0];
				//				String chord2 = chords12[1];
				//				chords.add(new ChordProg(new Chord(chord1), 2));
				//				chords.add(new ChordProg(new Chord(chord2), 2));
			}

			else { 
				try {
					chords.add(new ChordProg(new Chord(token), 4));
				}
				catch (Exception e) {
					System.err.println("erro ao criar acorde '" + token + "'");
				}
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
		chords.add(new ChordProg(chord, 4));
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

	public static void generateCountIn(Track track) {
		Note c3 = new Note(80, 10, 2);

		//		c3.put(track, 1, 1, 1);
		//		c3.put(track, 1, 2, 1);		
		//		c3.put(track, 1, 3, 1);
		//		c3.put(track, 1, 3, 2);
		//		c3.put(track, 1, 4, 1);
		//		c3.put(track, 1, 4, 2);

		c3.put(track, 1, 1);
		c3.put(track, 1, 2);
		c3.put(track, 1, 3);
		c3.put(track, 1, 4);
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

	}

	public int getBPM() {
		return this.bpm;
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}

	public void playPattern(ChordProg chord, Track track) {
		
		int root = chord.getRoot();
		System.out.println(root-24);
		Note rootNote = new Note(root-24, 60, 4);
		rootNote.put(track, bar+1, 1);


		if (Api.pattern.isMultiLine()) {
			playMultiLinePattern(chord, track);
			return;
		}

		for (int i = 0; i < Api.pattern.getSize(); i++) {
			int iNote = chord.getPatternNote(i);
			if (iNote > chord.getNumNotes()) {
				System.err.println("not playing note, because it doesn't fit in chord.");
				continue;
			}

			else if (iNote == -1) {
				// is a rest, do nothing
			}
			else {
				Note n = chord.getNote(iNote-1);					
				n.put(track, bar+1, i+1);		
			}
		} 
		bar++;
	}

	//TODO must refactor! this code is terrible
	private void playMultiLinePattern(ChordProg chord, Track track) {
//		for (int i = 0; i < chord.getPatternNotes(0).size(); i++) {
		for (int i = 0; i < 3; i++) {
			ArrayList<Integer> notes = chord.getPatternNotes(i);
			for (int n = 0; n < notes.size(); n++) {
				int iNote = notes.get(n);
				if (iNote > chord.getNumNotes()) {
					System.err.println("not playing note, because it doesn't fit in chord.");
					continue;
				}

				else if (iNote == -1) {
					// if is a rest, do nothing
//					Note ret = new Note(60, 0);
//					ret.put(track, bar+1, i+1);
				}
				else {
					Note note = chord.getNote(iNote-1);					
					note.put(track, bar+1, i+1);		
				}
			} 			 
		}
		bar++;
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
