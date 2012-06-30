package pt.santos.nuno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

public class Markov {
	
	private static final String stateDiviser = "->"; 
		
	private String inPrev;
	private String outPrev;
	private int order;
	private int elements;	
	private Random random;
	
	HashMap<String, Integer> map;
//	HashMap<String, Float> probs;
		
	public Markov(int order) {
		
		if (order > 3) {
			System.err.println("Markov class only supports up to order 3");
			return;
		}
		
		this.order = order;
		map = new HashMap<String, Integer>();
//		probs = new HashMap<String, Float>();
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
	
//	public void calculateProbs() {
//		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry<String, Integer> entry = (Entry<String, Integer>) it.next();
//			String key = entry.getKey(); 
//			int value = entry.getValue();
//			Float prob = (float)value / elements;
//			probs.put(key, prob);
//		}
//	}
	
	public String getNext() {
		
		String ret;
	
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
	
}
