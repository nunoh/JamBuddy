package pt.santos.nuno;

import java.util.Iterator;

public class Song  {

	private String name;
	private String genre;
	private String measure;
	private String sProgression;
	public Progression progression;
	private String key;
	
	public Song(String name, String genre, String measure, String key, String[] progression) {
		this.name = name;
		this.genre = genre;
		this.measure = measure;
		this.key = key;
		this.progression = new Progression(progression);		
	}
	
	public String getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return "song name:'" + name + "' genre:'" + genre + "' measure:'" + measure + "'\n" + sProgression;
	}
	
}
