package pt.hespanhol.nuno;

import java.util.HashMap;


public class MarkovModel {
	
	class ChordNode {
	
		int function;
		int type;
	
		ChordNode(int function, int type) {
			this.function = function;
			this.type = type;			 
		}
		
		@Override
		public String toString() {
			return "(" + function + "," + type + ")";
		}	
	}
	
	class ChordNodePair {
		
		ChordNode previous;
		ChordNode current;
		
		ChordNodePair(ChordNode previous, ChordNode current) {
			this.previous = previous;
			this.current = current;
		}
	}
	
	////////////////////////////////////////////////////////////////////
			
	private HashMap<ChordNodePair, Integer> counts;
	private HashMap<ChordNodePair, Float> probs;
	
	MarkovModel() {
		counts = new HashMap<ChordNodePair, Integer>();
		probs = new HashMap<ChordNodePair, Float>();
	}
	
	void add(ChordNode previous, ChordNode current) {			
		ChordNodePair pair = new ChordNodePair(previous, current);
		counts.get(pair);		
	}

	void addFirst(ChordNode first) {
		
	}
	
	void addLast(ChordNode last) {
		
	}
}
		
