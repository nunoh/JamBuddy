package pt.hespanhol.nuno;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Chord implements Iterable<Note> {
	
	protected int type;
	protected int root;
	protected List<Note> notes;
	
	// eg new Chord(60, 1);
	public Chord(int root, int type) {
		this.root = root;
		this.type = type;
		this.notes = new ArrayList<Note>();
		String def = Api.getChord(type).getDef();
		buildChord(def);
	}
	
	// eg new Chord(60, "maj");
	public Chord(int root, String name) {
		this(root, Api.getChordType(name));		
	}
	
	// eg new Chord("C", "maj");
	public Chord(String root, int type) {		
		this(Note.getMidiValue(root), type);
	}
	
	// eg new Chord("C", "maj");
	public Chord(String root, String name) {
		this(Note.getMidiValue(root), Api.getChordType(name));
	}
	
	// eg new Chord("Cmin7");
	public Chord(String chord) {
		//TODO
	}
		
	private void buildChord(String def) {
		String[] values = def.split(" ");
		for (int i = 0; i < values.length; i++) {
			this.addNote(new Note(this.root + Integer.parseInt(values[i])));
		}
	}
	
	public void addNote(Note n) {
		notes.add(n);
	}
	
	public String toString() {
		return Note.getLetter(this.root) + Api.getChordName(this.type) + " " + notes;
	}
	
	public List<Note> getNotes() {
		return notes;
	}
	
	public boolean equals(Object o) {

		Chord c = (Chord) o;
		
		// if the number of notes in each chord is different, then the chords are not the same
		if (c.getNumNotes() != this.notes.size())
			return false;
		
		// if there is at least one note in any chord that is different from the other, than the chords are not the same
		for (int i = 0; i < c.getNumNotes(); i++)
			if (c.getNotes().get(i).getPitch() != this.notes.get(i).getPitch())
				return false;
		
		// if there isn't any different note and the chords have the same note, then they are the same
		return true;
	}
	
	public Note getLowestNote() {
		Note ret = null;
		int min = Integer.MAX_VALUE;		
		for (Note n : notes) {
			if (n.getPitch() < min)
				ret = n;
		}
		return ret;
	}
	
	public Note getHighestNote() {
		Note ret = null;
		int max = Integer.MIN_VALUE;
		for (Note n : notes) {
			if (n.getPitch() > max) 
				ret = n;
		}
		return ret;
	}
	
	public int getNumNotes() {
		return notes.size();
	}
	
	public void transpose(int semitones) {
		this.root += semitones;
		ArrayList<Note> transposed = new ArrayList<Note>();
		if (notes != null) {
			for (int i = 0; i < this.notes.size(); i++) {
				Note n = this.notes.get(i);
				transposed.add(new Note(n.getPitch() + semitones, n.getVelocity()));
			}
		}
		this.notes = transposed;
	}
	
	public void setVelocity(int vel) {
		for (Note n : notes) {
			n.setVelocity(vel);
		}
	}

	public Iterator<Note> iterator() {
		return notes.iterator();
	}
	
	public boolean contains(Note n) {
		return this.notes.contains(n);
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public int getType() {
		return type;
	}

	public int getRoot() {
		return root;
	}
	
	public Note getNote(int i) {
		return notes.get(i);
	}
	
	
	
}
