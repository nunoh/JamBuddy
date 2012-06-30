package pt.santos.nuno;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Note implements Comparable<Note> {
	
	public enum ToStringMode { PITCH, LETTER };
	
	public static ToStringMode toStringMode = ToStringMode.PITCH;	
	private int velocity;
	private int pitch;
	private int duration = 1;
	
	public Note(int pitch, int velocity) {
		this.pitch = pitch;
		this.velocity = velocity;
	}
	
	public Note(int pitch, int velocity, int duration) {
		this.pitch = pitch;
		this.velocity = velocity;
		this.duration = duration;
	}
	
	public Note(String note, int velocity) {
		this(Note.getMidiValue(note), velocity);
	}
	
	public Note(int pitch) {
		this(pitch, Api.DEFAULT_NOTE_VELOCITY);
	}
	
	public Note(String note) {
		this(Note.getMidiValue(note), Api.DEFAULT_NOTE_VELOCITY);
	}
	
	public String toString() {
		if (toStringMode == ToStringMode.LETTER)
			return getLetter(this.pitch);
		else if (toStringMode == ToStringMode.PITCH)
			return ""+pitch;
		else 
			return "(" + pitch + " " + velocity + ")";
	}
	
	public static String getLetter(int pitch) {
		int t = pitch % 12;		
		
		String ret;				
		if (t == 0) ret = "C";
		else if (t == 1) ret = "C#";
		else if (t == 2) ret = "D";
		else if (t == 3) ret = "D#";
		else if (t == 4) ret = "E";
		else if (t == 5) ret = "F";
		else if (t == 6) ret = "F#";
		else if (t == 7) ret = "G";
		else if (t == 8) ret = "G#";
		else if (t == 9) ret = "A";
		else if (t == 10) ret = "A#";
		else ret = "B";
		
		return ret;
	}

	// TODO refactoring so that the accidentals are relative and not absolute
	public static int getMidiValue(String note) {
		
		int offset;
				
		if (note.equals("B#")) 			offset = 0;
		else if (note.equals("C")) 		offset = 0;
		else if (note.equals("C#")) 	offset = 1;
		else if (note.equals("C##")) 	offset = 2;
		
		else if (note.equals("Dbb")) 	offset = 0;
		else if (note.equals("Db")) 	offset = 1;
		else if (note.equals("D")) 		offset = 2;
		else if (note.equals("D#")) 	offset = 3;
		else if (note.equals("D##")) 	offset = 4;
		
		else if (note.equals("Ebb")) 	offset = 2;
		else if (note.equals("Eb")) 	offset = 3;
		else if (note.equals("E")) 		offset = 4;
		else if (note.equals("E#")) 	offset = 5;
		else if (note.equals("E##")) 	offset = 6;
		
		else if (note.equals("Fbb")) 	offset = 3;
		else if (note.equals("Fb")) 	offset = 4;
		else if (note.equals("F")) 		offset = 5;
		else if (note.equals("F#")) 	offset = 6;
		else if (note.equals("F##")) 	offset = 7;	
		
		else if (note.equals("Gbb")) 	offset = 5;
		else if (note.equals("Gb")) 	offset = 6;
		else if (note.equals("G")) 		offset = 7;
		else if (note.equals("G#")) 	offset = 8;
		else if (note.equals("G##")) 	offset = 9;
		
		else if (note.equals("Abb")) 	offset = 7;
		else if (note.equals("Ab")) 	offset = 8;
		else if (note.equals("A")) 		offset = 9;
		else if (note.equals("A#")) 	offset = 10;
		else if (note.equals("A##")) 	offset = 11;
		
		else if (note.equals("Bbb")) 	offset = 9;
		else if (note.equals("Bb")) 	offset = 10;
		else if (note.equals("B")) 		offset = 11;
		
		else return -1;
		
		return 60 + offset;
	}
	
	public int getPitch() {
		return pitch;
	}
	
	public boolean equals(Object o) {
		Note n = (Note) o;
		return n.getPitchClass() == this.getPitchClass();
	}
	
	public int getPitchClass() {
		return pitch % 12;
	}
	
	public static void setToStringMode(ToStringMode mode) {
		toStringMode = mode;
	}
	
	public int getVelocity() {
		return this.velocity;
	}

	public void setVelocity(int vel) {
		this.velocity = vel;		
	}
	
	public int getOctave() {
		return this.pitch / 12;
	}

	public int compareTo(Note n) {	
		if (this.pitch < n.getPitch()) return -1;
		else if (this.pitch == n.getPitch()) return 0;
		else return 1;
	}
	
//	public void play() {		
//				
//		Api.getReceiver().send(getMidiMessage(), -1);
//		
//		try {
//			Thread.sleep(this.duration*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}

	public ShortMessage getNoteOnMessage() {
		ShortMessage ret = new ShortMessage();
		try {
			ret.setMessage(ShortMessage.NOTE_ON, 0, this.pitch, this.velocity);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}		
		return ret;
	}
	
	public ShortMessage getNoteOffMessage() {
		ShortMessage ret = new ShortMessage();
		try {
			ret.setMessage(ShortMessage.NOTE_ON, 0, this.pitch, 0);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}		
		return ret;
	}
	
	public void put(Track track, int bar, int tempo) {
		bar = bar - 1;
		tempo = tempo - 1;
		MidiEvent eventOn = new MidiEvent(getNoteOnMessage(), bar*4 + tempo);
		MidiEvent eventOff = new MidiEvent(getNoteOffMessage(), bar*4 + tempo + duration);		
		track.add(eventOn);
		track.add(eventOff);
	}

	public int getFunction(String key) {
		int iKey = Note.getMidiValue(key) - 60; //TODO
		return (getPitchClass() + (12 - iKey)) % 12;
	}
	
}

