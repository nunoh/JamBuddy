package pt.hespanhol.nuno;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Api implements MetaEventListener {
	
	// constants
	private static final String PATH_XML_CONFIG = "src/config.xml";	
	public static final int DEFAULT_NOTE_VELOCITY = 80;
	public static final int DEFAULT_MIDI_DEVICE = 2;
	public static final int DEFAULT_PPQ_TICKS = 1;
	
	// private variables
	private static ArrayList<ChordDef> CHORDS;
	private static ArrayList<Pattern> PATTERNS;
	
	// public
	public static MidiDevice midiDevice;
	public static Receiver receiver;
	public Sequencer sequencer;
	public Sequence sequence;

	public Api() {		
		CHORDS = new ArrayList<ChordDef>();
		PATTERNS = new ArrayList<Pattern>();
		midiDevice = Utils.getMidiDevice(DEFAULT_MIDI_DEVICE);
		try {			
			receiver = midiDevice.getReceiver();
			sequencer = MidiSystem.getSequencer();
			sequence = new Sequence(Sequence.PPQ, DEFAULT_PPQ_TICKS);
		} catch (Exception e) {
			e.printStackTrace();
			
		sequencer.addMetaEventListener(this);
		
		
//		loadXML();
	}

	public void loadXML() {
		try {			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();			
			Document dom = db.parse(PATH_XML_CONFIG);
			dom.getDocumentElement().normalize();
						
			NodeList childs = dom.getDocumentElement().getChildNodes();
			
			// first level parsing to get chords and patterns element reference
			NodeList chords = null;
			NodeList patterns = null;
			for (int i = 0; i < childs.getLength(); i++) {
				Node node = childs.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					if (node.getNodeName().equals("chords"))
						chords = elem.getElementsByTagName("chord");
					else if (node.getNodeName().equals("patterns"))
						patterns = elem.getElementsByTagName("pattern");
					else {
						System.err.println("error parsing XML file");
						throw new Exception(node.getNodeName() + " is not valid");
					}
				}
			}
			
			// parsing chords
			for (int i = 0; i < chords.getLength(); i++) {						
				Element elem = (Element) chords.item(i);
				int type = Integer.parseInt(elem.getAttribute("type"));
				String name = elem.getAttribute("name");
				String def = elem.getTextContent();
				CHORDS.add(new ChordDef(type, name, def));
			}
			
			// parsing patterns
			for (int i = 0; i < patterns.getLength(); i++) {						
				Element elem = (Element) patterns.item(i);
				int type = Integer.parseInt(elem.getAttribute("type"));
				String name = elem.getAttribute("name");
				String def = elem.getTextContent();
				PATTERNS.add(new Pattern(type, name, def));
			}
			
		}
		
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
	
	public ArrayList<ChordDef> getChords() {
		return CHORDS;
	}
	
	public ArrayList<Pattern> getPatterns() {
		return PATTERNS;
	}
	
	public static ChordDef getChord(String name) {
		for (int i = 0; i < CHORDS.size(); i++)
			if (CHORDS.get(i).getName().equals(name))
				return CHORDS.get(i);
		return null;
	}

	public static ChordDef getChord(int type) {
		for (int i = 0; i < CHORDS.size(); i++)
			if (CHORDS.get(i).getType() == type)
				return CHORDS.get(i);
		return null;
	}

	public static int getChordType(String name) {
		for (int i = 0; i < CHORDS.size(); i++)
			if (CHORDS.get(i).getName().equals(name))
				return CHORDS.get(i).getType();
		return -1;
	}
	
	public static String getChordName(int type) {
		for (int i = 0; i < CHORDS.size(); i++)
			if (CHORDS.get(i).getType() == type)
				return CHORDS.get(i).getName();
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
		for (Pattern pattern : PATTERNS) {
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
	
}
