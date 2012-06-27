package pt.hespanhol.nuno;

public class Song {

	private int id;
	private String name;
	private String genre;
	private String measure;
	private String sProgression;
	public Progression progression;
	
	public Song(int id, String name, String genre, String measure, String sProgression) {
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.measure = measure;
		this.sProgression = sProgression;
		this.progression = new Progression(sProgression);		
	}
	
	@Override
	public String toString() {
		return "song id:'" + id + "' name:'" + name + "' genre:'" + genre + "' measure:'" + measure + "'\n" + sProgression;
	}
	
}
