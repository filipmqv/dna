package com.dna.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.dna.util.NodeComparator;

public class Spectrum implements Serializable {

	private static final long serialVersionUID = 399202046386348490L;

	private List<Node> elements; // list of elements in spectrum
	public String output = "";

	public Spectrum() {
		this.elements = new ArrayList<Node>();
	}

	public Spectrum(String input, int temperature1, int temperature2,
			int percentOfNewNodesErrors, int percentOfOccurranceErrors) {
		this.elements = new ArrayList<Node>();
		if ((temperature1 + 2 == temperature2) && temperature1 % 2 == 0) {
			cutInputString(input, (temperature1 + temperature2) / 2);
			addNewNodesErrors(percentOfNewNodesErrors);
			addOccurranceErrors(percentOfOccurranceErrors);
			// find elements containing other elements
			for (Node n : elements) {
				findIncludedElementsFor(n);
			}
		} else
			System.err.println("Given temperatures are not adjacent even.");
	}

	private void findIncludedElementsFor(Node node) {
		String currentSequence = node.getSequence();

		for (Node possibleNode : elements) {
			int lengthOfSequence = currentSequence.length();
			int lengthOfPossibleNode = possibleNode.getSequence().length();
			if (lengthOfPossibleNode < lengthOfSequence
					&& currentSequence.substring(0, lengthOfPossibleNode)
							.equals(possibleNode.getSequence())) {
				node.getIncluded().add(possibleNode);
			}
		}
	}

	private void cutInputString(String input, int maxTemperature) {
		int currFirstNucleotideIndex = 0;
		String currStrand = "";
		boolean next = true;
		while (next) { // not going out of INPUT index range
			try {
				currStrand = ""; // aktualna niæ elementu ze spektrum
				int currTemperature = 0;
				int tempNucleotideIndex = currFirstNucleotideIndex;
				do {
					char nucleotide = input.charAt(tempNucleotideIndex);
					if (nucleotide == 'A' || nucleotide == 'T')
						currTemperature += 2;
					else if (nucleotide == 'C' || nucleotide == 'G')
						currTemperature += 4;
					else {
						System.err.println("Wrong input string");
						throw new Exception();
					}
					currStrand += nucleotide;
					tempNucleotideIndex++;
					if (isTemperatureEqualTarget(currStrand, maxTemperature - 1)
							|| isTemperatureEqualTarget(currStrand,
									maxTemperature + 1)) {
						addNewElementToSpectrum(currStrand);
					}
				} while (currTemperature <= maxTemperature + 1);
				currFirstNucleotideIndex++;
			} catch (Exception e) { // index out of range; wrong letter
				next = false;
			}
		}
	}

	private boolean isTemperatureEqualTarget(String strand,
			int targetTemperature) {
		int temperature = 0;
		for (int i = 0; i < strand.length(); i++) {
			char nucleotide = strand.charAt(i);
			if (nucleotide == 'A' || nucleotide == 'T')
				temperature += 2;
			else if (nucleotide == 'C' || nucleotide == 'G')
				temperature += 4;
		}
		if (temperature == targetTemperature)
			return true;
		return false;
	}

	/**
	 * If Node with this strand (sequence) exists, add occurance, otherwise
	 * create new node
	 * 
	 * @param strand
	 *            sequence
	 */
	private void addNewElementToSpectrum(String strand) {
		Node existingNode = getNodeBySequence(strand);
		if (existingNode == null) {
			elements.add(new Node(strand));
		} else {
			existingNode.addOccurrence();
		}
	}

	public Node getNodeBySequence(String strand) {
		for (Node n : elements) {
			if (n.getSequence().equals(strand))
				return n;
		}
		return null;
	}

	public List<Node> getElements() {
		return elements;
	}

	/**
	 * get elements from spectrum that match current searching position and
	 * didn't exceed max no. of occurrence (ANY LENGTH)
	 * 
	 * @return matching elements
	 */
	public List<Node> getMatchingElements(Dna dna) {
		List<Node> matchingElements = new ArrayList<Node>();
		int currentPosition = dna.getCurrentPosition();
		String currentDna = dna.getCurrentDna();

		for (Node possibleNode : elements) {
			int lengthFromCurrentPositon = currentDna.length()
					- currentPosition;
			int lengthOfPossibleNode = possibleNode.getSequence().length();
			int len = Math.min(lengthFromCurrentPositon, lengthOfPossibleNode);
			if (currentDna.substring(currentPosition).equals(
					possibleNode.getSequence().substring(0, len))
					&& possibleNode.getVisited() < possibleNode.getMax()) {
				matchingElements.add(possibleNode);
			}
		}
		return matchingElements;
	}

	/**
	 * get elements from spectrum that match current searching position and
	 * didn't exceed max no. of occurrence (MAKING CURRENT DNA LONGER)
	 * 
	 * @return matching elements
	 */
	public List<Node> getMatchingElementsLonger(Dna dna) {
		List<Node> matchingElements = new ArrayList<Node>();
		int currentPosition = dna.getCurrentPosition();
		String currentDna = dna.getCurrentDna();

		for (Node possibleNode : elements) {
			int lengthFromCurrentPositon = currentDna.length()
					- currentPosition;
			int lengthOfPossibleNode = possibleNode.getSequence().length();
			// int len = Math.min(lengthFromCurrentPositon,
			// lengthOfPossibleNode);
			if (lengthOfPossibleNode > lengthFromCurrentPositon
					&& currentDna.substring(currentPosition).equals(
							possibleNode.getSequence().substring(0,
									lengthFromCurrentPositon))
					&& possibleNode.getVisited() < possibleNode.getMax()) {
				matchingElements.add(possibleNode);
			}
		}
		return matchingElements;
	}

	/**
	 * get elements from spectrum that match current searching position and
	 * didn't exceed max no. of occurrence (KEEPING CURRENT DNA'S SAME LENGTH)
	 * 
	 * @return matching elements
	 */
	public List<Node> getMatchingElementsShorter(Dna dna) {
		List<Node> matchingElements = new ArrayList<Node>();
		int currentPosition = dna.getCurrentPosition();
		String currentDna = dna.getCurrentDna();

		for (Node possibleNode : elements) {
			int lengthFromCurrentPositon = currentDna.length()
					- currentPosition;
			int lengthOfPossibleNode = possibleNode.getSequence().length();
			// int len = Math.min(lengthFromCurrentPositon,
			// lengthOfPossibleNode);
			if (lengthOfPossibleNode <= lengthFromCurrentPositon
					&& currentDna.substring(currentPosition).equals(
							possibleNode.getSequence().substring(0,
									lengthOfPossibleNode))
					&& possibleNode.getVisited() < possibleNode.getMax()) {
				matchingElements.add(possibleNode);
			}
		}
		return matchingElements;
	}

	/**
	 * Validate DNA
	 * 
	 * @param dna
	 * @param lengthOfSolution
	 * @return
	 */
	public boolean isSolutionValid(Dna dna, int lengthOfSolution) {
		dna.resetPositiveErrors();
		if (dna.getCurrentDna().length() != lengthOfSolution) {
			return false;
		}
		// czy wykorzystano wszystkie elementów ze spektrum
		// (visited) zgodnie z min i max, ewentualnie z b³êdem
		for (Node n : elements) {
			if (dna.getElements().contains(n)) {
				// czy zosta³o wykorzystane odpowiedni¹ liczbê razy?
				if (n.getVisited() < n.getMinWithPositiveError()
						|| n.getVisited() > n.getMax()) {
					return false;
				}
				if (n.getVisited() < n.getMin()) {
					dna.addPositiveError();
				}
			} else {
				// ostatnia deska ratunku - jeœli node ma
				// minWithPositiveError==0 to dodaj b³¹d i sprawdzaj dalej
				if (n.getMinWithPositiveError() == 0) {
					dna.addPositiveError();
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Measure how far given DNA is from correct DNA - critical and posotive
	 * errors
	 * 
	 * @param dna
	 * @param lengthOfSolution
	 */
	public void measureCorrectness(Dna dna) {
		dna.resetPositiveErrors();
		dna.resetCriticalErrors();

		for (Node n : elements) {
			if (dna.getElements().contains(n)) {
				// czy zosta³o wykorzystane odpowiedni¹ liczbê razy?
				if (n.getVisited() < n.getMinWithPositiveError()
						|| n.getVisited() > n.getMax()) {
					dna.addCriticalError();
					n.addUrgency();
					n.addUrgency();
					// System.out.println("added urg1");
				} else if (n.getVisited() < n.getMin()) {
					dna.addPositiveError();
					n.addUrgency();
					// System.out.println("added positive1");
				}
			} else {
				if (n.getMinWithPositiveError() == 0) {
					dna.addPositiveError();
					n.addUrgency();
					// System.out.println("added positive2");
				} else {
					dna.addCriticalError();
					n.addUrgency();
					n.addUrgency();
					// System.out.println("added urg2");
				}
			}
		}
	}

	@Override
	public String toString() {
		String out = "\nSPECTRUM\n";
		for (Node n : elements) {
			out += n.toString() + "\n";
		}
		out += "num of elements: " + elements.size();
		return out + "\nEND OF SPECTRUM \n";
	}

	public void sortElements() {
		Collections.sort(elements, new NodeComparator());
		Collections.reverse(elements);
	}

	private void addNewNodesErrors(int percent) {
		int sizeOfSpectrum = elements.size();
		int numOfElementsToAdd = (int) (sizeOfSpectrum * percent / 100);
		System.out.println("ADDED " + numOfElementsToAdd + " (" + percent
				+ "%) OF NEW NODES ERRORS");
		Random rnd = new Random();
		for (int i = 0; i < numOfElementsToAdd; i++) {
			boolean repeat = true;
			while (repeat) {
				int idx = rnd.nextInt(sizeOfSpectrum);
				Node n = elements.get(idx);
				String strandToChange = n.getSequence();
				idx = rnd.nextInt(strandToChange.length());
				char nuclToChange = strandToChange.charAt(idx);
				String nuclToSet = null;
				switch (nuclToChange) {
				case 'A':
					nuclToSet = "T";
					break;
				case 'C':
					nuclToSet = "G";
					break;
				case 'G':
					nuclToSet = "C";
					break;
				case 'T':
					nuclToSet = "A";
					break;
				default:
					break;
				}
				StringBuilder b = new StringBuilder(strandToChange);
				b.replace(idx, idx + 1, nuclToSet);
				strandToChange = b.toString();

				repeat = false;
				for (Node nn : elements) {
					if (nn.getSequence().equals(strandToChange)) {
						repeat = true;
						//System.out.println("istnieje " + strandToChange);
						break;
					}
				}
				if (!repeat) {
					addNewElementToSpectrum(strandToChange);
					//System.out.println("dodano " + strandToChange);
				}
			}
		}
	}

	private void addOccurranceErrors(int percent) {
		int sizeOfSpectrum = elements.size();
		int numOfElementsToAdd = (int) (sizeOfSpectrum * percent / 100);
		System.out.println("ADDED " + numOfElementsToAdd + " (" + percent
				+ "%) OF OCCURRANCE ERRORS");
		Random rnd = new Random();
		List<String> addedSequences = new ArrayList<String>();
		for (int i = 0; i < numOfElementsToAdd; i++) {
			boolean alreadyUsed = true;
			while (alreadyUsed) {
				alreadyUsed = false;
				int idx = rnd.nextInt(sizeOfSpectrum);
				Node n = elements.get(idx);
				for (String s : addedSequences) {
					if (n.getSequence().equals(s)) {
						alreadyUsed = true;
						break;
					}
				}
				if (!alreadyUsed) {
					if (n.getOccurredSoFar() == 2) {
						n.addOccurrence();
					}
					n.addOccurrence();
				}
			}
		}
	}

}
