package gam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class Markov {
	
	private static final String stateDiviser = "->"; 
		
	private String inPrev;
	private String outPrev;
	@SuppressWarnings("unused")
	private int order;
	@SuppressWarnings("unused")
	private int elements;	
	private Random random;
	
	HashMap<String, Integer> map;
		
	public Markov(int order) {
		
		if (order > 3) {
			System.err.println("Markov class only supports up to order 3");
			return;
		}
		
		this.order = order;
		map = new HashMap<String, Integer>();
		elements = 0;
		random = new Random();		
	}
	
	public void add(String str) {
		
		// first element
		if (inPrev == null) {
			inPrev = str;
			return;
		}
		
		elements++;	
		
		String sPair = inPrev + stateDiviser + str;
					
		Object ret = map.get(sPair);
		if (ret == null) {
			map.put(sPair, 1);
		}
		else {
			Integer i = (Integer) ret;
			map.remove(sPair);
			map.put(sPair, i+1);
		}
		
		inPrev = str;		
	}
	
	public String getNext() {
		
		String ret;
	
		// se é o primeiro, retorna um random de todos os estados
		if (outPrev == null) {
			int r = random.nextInt(map.size());			
			Object[] keys = map.keySet().toArray();
			String pair = (String) keys[r];
			ret = getOriginState(pair);
		}
 
		else {
			ret = getSucessor();
		}
		
		outPrev = ret;
		
		return ret;
	}
				
	public String getSucessor() {
		
		String ret = null;
		
		ArrayList<String> successors = new ArrayList<String>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		int weight = 0;
		
		// first pass to get the successors states
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = (Entry<String, Integer>) it.next();
			String key = entry.getKey();
			int value = entry.getValue();
			
			if (getOriginState(key).contains(outPrev)) {
				successors.add(getDestinationState(key));
				counts.add(value);
				weight += value;
			}
		}
		
		// pass on the successor state to get a weighted random generated one
		int r = random.nextInt(weight);		
		int pCount = 0;
		for (int i = 0; i < counts.size(); i++) {
			pCount += counts.get(i);
			if (pCount >= r) {
				ret = successors.get(i);
				break;
			}
		}
		
		return ret;		
	}
	
	public String getOriginState(String transition) {
		return transition.substring(0, transition.indexOf(Markov.stateDiviser));
	}
	
	public String getDestinationState(String transition) {
		return transition.substring(transition.indexOf(Markov.stateDiviser)+Markov.stateDiviser.length());		
	}

	public void setPrevious(String string) {
		
	}

	public String getFirst(String genre, String major) {

		String firstState = "(0,1)"; 
		if (genre.equals("Jazz") && major.equals("Major")) {
			Random random = new Random();
			int n = random.nextInt(3);
			if (n == 0) firstState = "(0,1)";
			else if (n == 1) firstState = "(0,5)";
			else firstState = "(2,6)";
		}
		
		else if (genre.equals("Jazz") && major.equals("Minor")) {
			Random random = new Random();
			int n = random.nextInt(2);
			if (n == 0) firstState = "(0,2)";
			else firstState = "(0,6)";
		}
		
		else if (genre.equals("Pimba")) {
			firstState = "(0,1)";
		}
		
		else if (genre.equals("Blues")) {
			// dont know a rule for this
		}
		
		else if (genre.equals("Bossa Nova") && major.equals("Major")){ // Bossa Nova
			firstState = "(0,5)";
		}
		
		else { // Bossa Nova Minor
			firstState = "(2,6)"; 
		}
		
		outPrev = firstState;
//		System.out.println("returning " + outPrev);
		return outPrev;
	}
	
}
