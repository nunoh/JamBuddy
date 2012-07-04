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
	
	//TODO pode ser melhor reescrito ainda
	public static int getMidiValue(String note) {
		
		int offset;
		
		if (note.contains("C")) offset = 0;
		else if (note.contains("D")) offset = 0;
		else if (note.contains("E")) offset = 0;
		else if (note.contains("F")) offset = 0;
		else if (note.contains("G")) offset = 0;
		else if (note.contains("A")) offset = 0;
		else if (note.contains("B")) offset = 0;
		else return -1;
		
		if (note.contains("##")) offset += 2;
		else if (note.contains("#")) offset += 1;
		else if (note.contains("bb")) offset -= 2;
		else if (note.contains("b")) offset -= 1;
		else  offset += 0;
		
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

	public static int getPitchClass(int pitch) {
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

