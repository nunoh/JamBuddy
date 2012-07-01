package pt.santos.nuno;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class Main {

	private static Api app;
	
	public static void main(String[] args) throws Exception {
		Api app = new Api();
		app.loadXML();
		GUI gui = new GUI();
		gui.start();
	}
		
	public void markov() {

		app = new Api();
		app.loadXML();
		app.loadSongs();
		System.out.println(app.markov.map);

		for (int i = 0; i < 10; i++)
			System.out.println(app.markov.getNext());

	}

	public void progression() {
		app.openMidiDevice();

		Song s = app.getSongs().get(0);
		s.progression.setPattern(new Pattern("up"));

		app.openMidiDevice();

		app.setBPM(100);

		app.setProgression(s.progression);
		app.playProgression();

		app.closeMidiDevice();
	}

	public void previousWork() {
		app.openMidiDevice();

		Chord Cmaj7 = new Chord("C", "maj7");
		Chord Fmaj = new Chord("F", "maj");

		Progression prog = new Progression();
		prog.addChord(Cmaj7);
		prog.addChord(Fmaj);
		prog.setPattern(new Pattern("up"));
	app.setBPM(100);

	app.setProgression(prog);
	app.playProgression();

		app.closeMidiDevice();
	}

	public void midiStuff() throws Exception {

		Sequence seq = new Sequence(Sequence.PPQ, 1);
		Track t = seq.createTrack();

		Note c = new Note(60, 60, 1);
		Note e = new Note(64, 80, 1);
		Note g = new Note(67, 100, 1);

		c.put(t, 1, 1);
		e.put(t, 1, 2);
		g.put(t, 1, 3);
		g.put(t, 1, 4);

		app.sequencer.open();
		app.sequencer.setSequence(seq);
		app.sequencer.setTempoInBPM(120);
		app.sequencer.start();
	}

	public void midiStuff2() throws Exception {

		Synthesizer synth = MidiSystem.getSynthesizer();
		MidiChannel[] mcs = synth.getChannels();
		Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
		synth.loadInstrument(instruments[9]);

		mcs[0].programChange(instruments[404].getPatch().getBank(),
				instruments[404].getPatch().getProgram());
		mcs[0].noteOn(60, 100);
		mcs[0].noteOn(64, 100);
		mcs[0].noteOn(67, 100);

		Thread.sleep(2000);

		mcs[0].noteOff(24);
		mcs[0].noteOff(64);
		mcs[0].noteOff(67);

		synth.close();
	}
}
