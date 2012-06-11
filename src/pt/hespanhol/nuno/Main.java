package pt.hespanhol.nuno;


public class Main {
	
	public static void main(String[] args) throws Exception {

		Api app = new Api();		
		app.loadXML();
		app.openMidiDevice();

		Chord Cmaj = new Chord("C", "maj");
		Chord Fmaj = new Chord("F", "maj"); 

		Progression prog = new Progression();
		prog.addChord(Cmaj, 4);
		prog.addChord(Fmaj, 4);
		prog.addChord(Cmaj, 4);
		prog.setPattern(new Pattern("down"));

		prog.play();

		app.closeMidiDevice();

//		Sequence seq = new Sequence(Sequence.PPQ, 1);
//		Track t = seq.createTrack();
//
//		Note c = new Note(60, 60, 1);
//		Note e = new Note(64, 80, 1);
//		Note g = new Note(67, 100, 1);
//
//		c.put(t, 1, 1);
//		e.put(t, 1, 2);
//		g.put(t, 1, 3);
//		g.put(t, 1, 4);
//
//		app.sequencer.open();
//		app.sequencer.setSequence(seq);				
//		app.sequencer.setTempoInBPM(180);		
//		app.sequencer.start();
	}

	// public void old() {
	// MidiChannel[] mcs = synth.getChannels();
	// Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
	// synth.loadInstrument(instruments[9]);

	// mcs[0].programChange(instruments[404].getPatch().getBank(),
	// instruments[404].getPatch().getProgram());
	//
	// mcs[0].noteOn(60, 100);
	// mcs[0].noteOn(64, 100);
	// mcs[0].noteOn(67, 100);
	//
	// Thread.sleep(2000);
	//
	// mcs[0].noteOff(24);
	// mcs[0].noteOff(64);
	// mcs[0].noteOff(67);
	//
	// synth.close();
	// }

}
