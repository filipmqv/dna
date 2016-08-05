package com.dna.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Dna implements Serializable {

	private static final long serialVersionUID = -5059702349357070987L;

	private String currentDna; // dna built so far
	private int currentPosition; // which nucleotide (index in solution) is now
									// beginning for searching
	private List<Node> elements; // from which elements is it built?
	private int numberOfPositiveErrors;
	private int numberOfCriticalErrors; //disqualifying the solution
	

	public Dna() {
		this.currentDna = "";// firstNode.getSequence();
		this.currentPosition = 0;
		this.elements = new ArrayList<Node>();
		this.numberOfPositiveErrors = 0;
		this.numberOfCriticalErrors = 0;
	}
	
	private boolean addLongerNode(Node node){
		try {
			elements.add(node);
			node.addVisit();
			StringBuilder b = new StringBuilder(currentDna);
			b.replace(currentPosition, currentDna.length(),
					node.getSequence());
			currentDna = b.toString();
			currentPosition++;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean addShorterNode(Node node) {
		elements.add(node);
		node.addVisit();
		currentPosition++;
		return true;
	}
	
	public boolean addElementToDna(Node node, int lengthOfSolution, Spectrum spectrum) {
		int lengthFromCurrentPositon = currentDna.length() - currentPosition;
		int lengthOfNode = node.getSequence().length();
		// dodaj do rozwi¹zania jeœli wyd³u¿y to rozwi¹zanie
		// oraz nie przekroczy dopuszczalnej d³ugoœci rozwi¹zania
		if (lengthFromCurrentPositon < lengthOfNode && 
				currentPosition + lengthOfNode <= lengthOfSolution) {
			if (!addLongerNode(node)) {
				//System.out.println("  1 error longer " + currentPosition + " " + currentDna +" "+ node);
				return false;
			}
		} else if (currentPosition + lengthOfNode <= lengthOfSolution) {
			addShorterNode(node);
			//System.out.println("  2 short " + currentPosition + " " + currentDna+" "+ node);
		} else {
			//System.out.println("  3 false " + currentPosition + " " + currentDna+" "+ node);
			return false;
		}
		//System.out.println("  4 true " + currentPosition + " " + currentDna+" "+ node);
		return true;
	}
	
	public void removeElementFromDna(int index) {
		Node toDelete = elements.get(index);
		toDelete.removeVisit();
		elements.remove(index);
		// rebuild currentDna
		currentPosition = 0;
		//String prev = currentDna;
		currentDna = "";
		for (Node n : elements) {
			try {
				StringBuilder b = new StringBuilder(currentDna);
				b.replace(currentPosition, currentDna.length(), n.getSequence());
				currentDna = b.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentPosition++;
		}
		//if (!prev.substring(0, 2).equals(currentDna.substring(0, 2))) {
			//System.out.println(index + " "+ currentPosition + "  " + toDelete + "        " + prev + " " + currentDna);
		//}
	}
	
	
	
	
	/*
	public List<Dna> tryInsertNodeAnywhere(Node node) {
		List<Dna> possibleDnas = new ArrayList<Dna>();
		int elementsSize = elements.size();
		for (int i=1; i<=elementsSize; i++) {
			elements.add(i, node);
			System.out.println(i + " try: elements: " + elements);
			String prevDna = currentDna;
			if (tryBuildCurrentDnaFromElements()) {
				node.addVisit();
				Dna deepClonedDna = (Dna) DeepClone.deepClone(this);
				possibleDnas.add(deepClonedDna);
				node.removeVisit();
				currentDna = prevDna;
			} 
			elements.remove(i);
			System.out.println();
		}
		System.out.println(possibleDnas);
		return possibleDnas;
	}*/
	
	/*private boolean tryBuildCurrentDnaFromElements() {
		String tempDna = elements.get(0).getSequence();
		int tempCurrentPosition = 0;
		for (Node n : elements) {
			int lengthFromCurrentPositon = tempDna.length() - tempCurrentPosition;
			int lengthOfPossibleNode = n.getSequence().length();
			int len = Math.min(lengthFromCurrentPositon, lengthOfPossibleNode);
			if (tempDna.substring(tempCurrentPosition).equals(
					n.getSequence().substring(0, len))) {
				System.out.println("## "+ n + " " + tempDna);
				try {
					StringBuilder b = new StringBuilder(tempDna);
					b.replace(tempCurrentPosition, tempDna.length(), n.getSequence());
					tempDna = b.toString();
					tempCurrentPosition++; 
				} catch (Exception e) {
					System.out.println("error builder "+tempDna);
					return false;
				}
			} else {
				System.out.println("error building for " + n + " tempdna: " +tempDna);
				return false;
			}	
		}
		currentDna = tempDna;
		return true;
	}*/
	
	
	/*public List<Dna> tryDeleteNodeFromAnywhere(int index) {
		List<Dna> possibleDnas = new ArrayList<Dna>();
		Node toDelete = elements.get(index);
		elements.remove(index);
		String prevDna = currentDna;
		if (tryBuildCurrentDnaFromElements()) {
			toDelete.removeVisit();
			Dna deepClonedDna = (Dna) DeepClone.deepClone(this);
			possibleDnas.add(deepClonedDna);
			toDelete.addVisit();
			currentDna = prevDna;
		} 
		elements.add(index, toDelete);
		
		System.out.println(possibleDnas);
		return possibleDnas;
	}*/

	public String getCurrentDna() {
		return currentDna;
	}

	public void setCurrentDna(String currentDna) {
		this.currentDna = currentDna;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	@Override
	public String toString() {
		return "Dna [pos" + numberOfPositiveErrors + " cri"
				+ numberOfCriticalErrors + " len" + currentDna.length()
				+ " currentDna=" + currentDna + ", elements=" + elements + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentDna == null) ? 0 : currentDna.hashCode());
		result = prime * result + currentPosition;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
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
		Dna other = (Dna) obj;
		if (currentDna == null) {
			if (other.currentDna != null)
				return false;
		} else if (!currentDna.equals(other.currentDna))
			return false;
		if (currentPosition != other.currentPosition)
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	public List<Node> getElements() {
		return elements;
	}
	
	public void addPositiveError() {
		numberOfPositiveErrors++;
	}
	
	public void resetPositiveErrors() {
		numberOfPositiveErrors=0;
	}
	
	public int getNumberOfPositiveErrors() {
		return numberOfPositiveErrors;
	}
	
	public void addCriticalError() {
		numberOfCriticalErrors++;
	}
	
	public void resetCriticalErrors() {
		numberOfCriticalErrors=0;
	}
	
	public int getNumberOfCriticalErrors() {
		return numberOfCriticalErrors;
	}

	
}
