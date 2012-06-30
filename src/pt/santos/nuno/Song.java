package pt.santos.nuno;

import java.util.Iterator;

public class Song  {

	private int id;
	private String name;
	private String genre;
	private String measure;
	private String sProgression;
	public Progression progression;
	private String key;
	
	public Song(int id, String name, String genre, String measure, String key, String sProgression) {
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.measure = measure;
		this.key = key;
		this.sProgression = sProgression;
		this.progression = new Progression(sProgression);		
	}
	
	public String getKey() {
		return key;
	}
	
	@Override
	public String toString() {
		return "song id:'" + id + "' name:'" + name + "' genre:'" + genre + "' measure:'" + measure + "'\n" + sProgression;
	}
	
}
