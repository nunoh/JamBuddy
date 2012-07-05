package gam;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Synthesizer;

public class Utils {

	public static MidiDevice getMidiDevice(int iDevice) {		
		MidiDevice ret = null;
		try {
			ret = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[iDevice]);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String readLine() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ret = null;
		try {
			ret = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void printMidiDevices() {
		MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < devices.length; i++) {
			MidiDevice.Info device = devices[i];
			System.out.println(i + " " + device.getName() + " " + device.getDescription());
		}
	}
	
	public static void printInstruments() {
		try {
			Synthesizer synth = MidiSystem.getSynthesizer();
			Instrument[] instruments = synth.getDefaultSoundbank().getInstruments();
			for (int i = 0; i < instruments.length; i++) {
				Instrument instrument = instruments[i];
				System.out.println(i + " " + instrument.getName());
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}		
	}
	
	public static void saveSeqToFile(Sequence seq, String filename) {
		try {
			File f = new File(filename);
			MidiSystem.write(seq, 1, f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}