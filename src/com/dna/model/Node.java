package com.dna.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
	
	private static final long serialVersionUID = -2497864831797577470L;
	
	private int minWithPositiveError; // min liczba wyst¹pieñ po uwzglêdnieniu
										// b³êdu
	private int min;// min, max liczba wyst¹pieñ 0 1 {2,3} lub wiele
	private int max;
	private int occurredSoFar; // ile razy podczas tworzenia spektrum dotychczas
								// taka sekwencja wyst¹pi³a
	private String sequence; // string sekwencja
	private int visited; // ile razy ten wierzcho³ek zosta³ dotychczas
							// odwiedzony
	private List<Node> included; // inne Node'y (krótsze) które zawieraj¹ siê w
									// tej sekwencji
	private int temperature;
	private int urgency; // how many times this Node caused critical error

	public Node() {
		this.minWithPositiveError = 0;
		this.min = 0;
		this.max = 0;
		this.occurredSoFar = 0;
		this.sequence = null;
		this.visited = 0;
		this.included = new ArrayList<Node>();
		this.temperature = 0;
		this.urgency = 0;
	}

	public Node(String seq) {
		this.minWithPositiveError = 0;
		this.min = 1;
		this.max = 1;
		this.occurredSoFar = 1;
		this.sequence = seq;
		this.visited = 0;
		this.included = new ArrayList<Node>();
		this.temperature = 0;
		for (int i = 0; i < seq.length(); i++) {
			char nucleotide = seq.charAt(i);
			if (nucleotide == 'A' || nucleotide == 'T')
				temperature += 2;
			else if (nucleotide == 'C' || nucleotide == 'G')
				temperature += 4;
		}
		this.urgency = 0;
	}

	public void addOccurrence() {
		if (occurredSoFar == 1) {
			minWithPositiveError = 1;
			min = 2;
			max = 3;
		} else if (occurredSoFar == 3) {
			minWithPositiveError = 2;
			min = 4;
			max = 99999999;
		}
		occurredSoFar++;
	}
	
	public void addVisit() {
		visited++;
		// dla wszystkich node'ów krótszych, podobnych to samo
		for (Node n : included) {
			n.addVisit();
		}
	}
	
	public void removeVisit() {
		visited--;
		for (Node n : included) {
			n.removeVisit();
		}
	}
	
	public void resetVisits() {
		visited = 0;
	}

	public int getMinWithPositiveError() {
		return minWithPositiveError;
	}

	public void setMinWithPositiveError(int minWithPositiveError) {
		this.minWithPositiveError = minWithPositiveError;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getOccurredSoFar() {
		return occurredSoFar;
	}

	public void setOccurredSoFar(int occurredSoFar) {
		this.occurredSoFar = occurredSoFar;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getVisited() {
		return visited;
	}

	public void setVisited(int visited) {
		this.visited = visited;
	}

	public List<Node> getIncluded() {
		return included;
	}

	public void setIncluded(List<Node> included) {
		this.included = included;
	}

	@Override
	public String toString() {
		return "Node [" + minWithPositiveError + "/" + min + "/" + max + "/ vi" + visited + ", "
				+ sequence + " " + temperature + " " + sequence.length() + " ur" + urgency + 
				 "\tincluded:" + included + " oc:" + occurredSoFar + "]";
	}

	public int compareTo(Node n) {
		return sequence.compareTo(n.getSequence());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}
	
	public void addUrgency() {
		if (urgency < Integer.MAX_VALUE)
			urgency++;
		else 
			System.out.println("MAX URGENCY VALUE" + this.toString());
	}
	
	public void resetUrgency() {
		urgency=0;
	}
	
	public int getUrgency() {
		return urgency;
	}

}
