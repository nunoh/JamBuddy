package gam;

import junit.framework.TestCase;

public class Test extends TestCase {
	
	Api app;
	
	public void setUp() {		
		app = new Api();
		app.loadXML();
	}

	public void testNote() {         
        
		// testing getMidiValue function
		assertEquals(Note.getMidiValue("C"), 60);
        assertEquals(Note.getMidiValue("C#"), 61);
        assertEquals(Note.getMidiValue("D"), 62);
        assertEquals(Note.getMidiValue("D#"), 63);
        assertEquals(Note.getMidiValue("E"), 64);
        assertEquals(Note.getMidiValue("E#"), 65);
        assertEquals(Note.getMidiValue("F"), 65);
        assertEquals(Note.getMidiValue("F#"), 66);
        assertEquals(Note.getMidiValue("G"), 67);
        assertEquals(Note.getMidiValue("G#"), 68);
        assertEquals(Note.getMidiValue("A"), 69);
        assertEquals(Note.getMidiValue("A#"), 70);
        assertEquals(Note.getMidiValue("B"), 71);
        assertEquals(Note.getMidiValue("B#"), 72);       
	
        // testing getLetter function
		assertEquals("C", Note.getLetter(60));
		assertEquals("D", Note.getLetter(62));
		assertEquals("E", Note.getLetter(64));
		assertEquals("F", Note.getLetter(65));
		assertEquals("G", Note.getLetter(67));
		assertEquals("A", Note.getLetter(69));
		assertEquals("B", Note.getLetter(71));
		
		// testing equals operator
		Note c = new Note(60, 123);
		Note d = new Note(62, 100);		
		assertEquals(c.equals(c), true);
		assertEquals(c.equals(d), false);
		
		// testing getPitchClass function
		assertEquals(c.getPitchClass(), 0);
		assertEquals(d.getPitchClass(), 2);
	}	
	
	public void testChord() {
		
		Chord Cmaj = new Chord(60, 1);
		Chord Dmaj = new Chord(62, 1);
		Chord Emaj = new Chord(64, 1);
						
		// testing equals operator
		assertEquals(Cmaj.equals(Cmaj), true);
		assertEquals(Cmaj.equals(Dmaj), false);
					
		// test getOctave function
		assertEquals(new Note(60).getOctave(), 5);
		assertEquals(new Note(59).getOctave(), 4);
		
		// test iterator interface implementation
		for (Note n : Cmaj)			
			assertEquals(n.getPitch() == 60 || n.getPitch() == 64 || n.getPitch() == 67, true);
		
		for (Note n : Dmaj)			
			 assertEquals(n.getPitch() == 62 || n.getPitch() == 66 || n.getPitch() == 69, true);
		
		for (Note n : Dmaj)
			 assertEquals(n.getPitch() == 60 || n.getPitch() == 64 || n.getPitch() == 67, false);
		
		// test contains function
		assertEquals(Cmaj.contains(new Note("C")), true);
		assertEquals(Cmaj.contains(new Note("D")), false);
		
		// test transpose function
		Cmaj.transpose(2);
		assertEquals(Cmaj, Dmaj);		
		assertEquals(Cmaj.equals(Emaj), false);
	}
	
	public void testMarkov() {
		
//		ChordNode cn = new ChordNode(1, 1);
//		ChordNode cn2 = new ChordNode(1, 1);
//		ChordNode cn3 = new ChordNode(1, 2);
//		assertEquals(cn.equals(cn2), true);
//		assertEquals(cn.equals(cn3), false);
		
		Markov markov = new Markov(1);
		String transition = "(0,1)->(0,2)";
		assertEquals("(0,1)", markov.getOriginState(transition));
		assertEquals("(0,2)", markov.getDestinationState(transition)); 		
	}
	
}
