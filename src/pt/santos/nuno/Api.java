package pt.santos.nuno;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Api implements MetaEventListener {
	
	// constants
	public static final String PATH_XML_CONFIG = "src/config.xml";	
	public static final int DEFAULT_NOTE_VELOCITY = 80;
	public static final int DEFAULT_MIDI_DEVICE = 2;
	public static final int DEFAULT_PPQ_TICKS = 1;
	public static final int DEFAULT_SEQUENCE_BPM = 80;
	public static final String chordsDelimiter = "|";
	
	// private
	private static ArrayList<ChordDef> chords;
	private static ArrayList<Pattern> patterns;
	private static ArrayList<Song> songs;
	
	// public static 
	public static MidiDevice midiDevice;
	public static Receiver receiver;
	public static Sequencer sequencer;
	public static Sequence sequence;
	public static Track track;	
	public static Pattern pattern;
	public static Markov markov;
	
	// public non static
	public Progression progression;
	public int bpm;
	public int key;			

	public Api() {
		
		chords = new ArrayList<ChordDef>();
		patterns = new ArrayList<Pattern>();
		songs = new ArrayList<Song>();
		midiDevice = Utils.getMidiDevice(DEFAULT_MIDI_DEVICE);
		try {			
			receiver = midiDevice.getReceiver();
			sequencer = MidiSystem.getSequencer();
			sequence = new Sequence(Sequence.PPQ, DEFAULT_PPQ_TICKS);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		sequencer.addMetaEventListener(this);
		track = sequence.createTrack();
		
		bpm = DEFAULT_SEQUENCE_BPM;
		
		key = 2; // C
		
		markov = new Markov(1);
		
		openMidiDevice();
	}

	public void loadXML() {
		try {			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			DocumentBuilder db = dbf.newDocumentBuilder();			
			Document dom = db.parse(PATH_XML_CONFIG);
			dom.getDocumentElement().normalize();
						
			NodeList childs = dom.getDocumentElement().getChildNodes();
			
			// first level parsing to get chords and patterns element reference
			NodeList chordsNode = null;
			NodeList patternsNode = null;
			NodeList songsNode = null;
			for (int i = 0; i < childs.getLength(); i++) {
				Node node = childs.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					if (node.getNodeName().equals("chords"))
						chordsNode = elem.getElementsByTagName("chord");
					else if (node.getNodeName().equals("patterns"))
						patternsNode = elem.getElementsByTagName("pattern");
					else if (node.getNodeName().equals("songs"))
						songsNode = elem.getElementsByTagName("song");
					else {
						System.err.println("error parsing XML file");
						throw new Exception(node.getNodeName() + " is not valid");
					}
				}
			}
			
			// parsing chords
			for (int i = 0; i < chordsNode.getLength(); i++) {						
				Element elem = (Element) chordsNode.item(i);
				int type = Integer.parseInt(elem.getAttribute("type"));
				String name = elem.getAttribute("name");
				String def = elem.getTextContent();
				chords.add(new ChordDef(type, name, def));
			}
			
			// parsing patterns
			for (int i = 0; i < patternsNode.getLength(); i++) {						
				Element elem = (Element) patternsNode.item(i);
				int type = Integer.parseInt(elem.getAttribute("type"));
				String name = elem.getAttribute("name");
				String def = elem.getTextContent();
				patterns.add(new Pattern(type, name, def));
			}
			
			// parsing songs
			for (int i = 0; i < songsNode.getLength(); i++) {
				Element elem = (Element) songsNode.item(i);
				int id = Integer.parseInt(elem.getAttribute("id"));
				String name = elem.getAttribute("name");
				String genre = elem.getAttribute("genre");
				String measure = elem.getAttribute("measure");
				String key = elem.getAttribute("key");
				String sProg = elem.getTextContent();
				sProg = sProg.substring(sProg.indexOf(chordsDelimiter), sProg.lastIndexOf(chordsDelimiter)+1);
				songs.add(new Song(id, name, genre, measure, key, sProg));
			}
			
		}
		
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public ArrayList<ChordDef> getChords() {
		return chords;
	}
	
	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}
	
	public ArrayList<Song> getSongs() {
		return songs;
	}
	
	public static ChordDef getChord(String name) {
		for (int i = 0; i < chords.size(); i++)
			if (chords.get(i).getName().equals(name))
				return chords.get(i);
		return null;
	}

	public static ChordDef getChord(int type) {
		for (int i = 0; i < chords.size(); i++)
			if (chords.get(i).getType() == type)
				return chords.get(i);
		return null;
	}

	public static int getChordType(String name) {
		for (int i = 0; i < chords.size(); i++)
			if (chords.get(i).getName().equals(name))
				return chords.get(i).getType();
		return -1;
	}
	
	public static String getChordName(int type) {
		for (int i = 0; i < chords.size(); i++)
			if (chords.get(i).getType() == type)
				return chords.get(i).getName();
		return null;
	}
			
	public static void chooseMidiDevice() {		
		Utils.printMidiDevices();		
		System.out.print("? ");		
		String sDevice = Utils.readLine();
		midiDevice = Utils.getMidiDevice(Integer.parseInt(sDevice));
		System.out.println("device chosen is '" + midiDevice.getDeviceInfo().getName() + "'");		
	}
	
	public static MidiDevice getMidiDevice() {
		return midiDevice;
	}
	
	public static Receiver getReceiver() {
		return receiver;
	}
	
	public void openMidiDevice() {
		try {
			midiDevice.open();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void closeMidiDevice() {
		midiDevice.close();
	}
	
	public static Pattern getPattern(String name) {
		for (Pattern pattern : patterns) {
			if (pattern.getName().equals(name))
				return pattern;
		}
		return null;
	}

	public void meta(MetaMessage meta) {
		if (meta.getType() == 47) {
			sequencer.close();
		}
	}
		
	public void setProgression(Progression prog) {
		this.progression = prog;		
	}
	
	public void playProgression() {
		
		if (progression == null) {
			System.err.println("error: no progression to play");
			return;
		}
		
		try {
			sequence = new Sequence(Sequence.PPQ, DEFAULT_PPQ_TICKS);
		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
		}
		
		Track track = sequence.createTrack();
		this.progression.generate(track);
		
		try {
			sequencer.open();
			sequencer.setSequence(sequence);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
				
		sequencer.setTempoInBPM(bpm);		
		sequencer.start();
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}

	public static void loadSongs() {				
		for (Song song : songs) {										
			for (ChordProg chord : song.progression) {
				int function = chord.getNote(0).getFunction(song.getKey());
				int type = chord.getType();			
				String sNode = "(" + function + "," + type + ")";			
				markov.add(sNode);
			}
		}
	}

	public void setPattern(Pattern pattern) {
		Api.pattern = pattern;
	}
	
}
