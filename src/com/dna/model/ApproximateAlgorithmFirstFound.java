package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.dna.util.DeepClone;
import com.dna.util.NodeUrgencyComparator;

/**
 * Algorytm przybliżony, zatrzymujący się po znalezieniu pierwszego poprawnego
 * rozwiązania czyli o mierze Levenshteina = 0;
 * 
 * @author filipmqv
 *
 */
public class ApproximateAlgorithmFirstFound {

	private boolean SOLUTIONFOUND = false;
	private Spectrum spectrum;
	private final int lengthOfSolution;
	private Node firstNode;
	private List<Dna> solutions = new ArrayList<Dna>();
	private List<Dna> uniqueSolutions = new ArrayList<Dna>();
	// private int maxLengthFound = 0;
	private Random generator = new Random();
	private String inputString;
	public String output = "";

	public ApproximateAlgorithmFirstFound(Spectrum spectrum,
			int lengthOfSolution, Node firstNode, String inputString) {
		this.spectrum = spectrum;
		this.lengthOfSolution = lengthOfSolution;
		this.firstNode = firstNode;
		this.inputString = inputString;
		// this.dna = new Dna(firstNode);
	}

	public void generateSolution() {
		//System.out.println("APPROXIMATE_FIRST");
		//System.out.println("FIRST NODE: " + firstNode);
		//output += "APPROXIMATE_FIRST";

		//System.out.println("START");
		long start = System.currentTimeMillis();
		search(start);
		long stop = System.currentTimeMillis();
		if (!SOLUTIONFOUND) {
			output += " --- ";
		}
		//System.out.println("STOP");
		long diff = stop - start;

		System.out.println("TIME: " + (double) diff / 1000 + " s");
		output += "\t" + (double)diff/1000;
		
		//System.out.println("SOLUTION:");
		// for (Dna u : solutions)
		// System.out.println(u);

		// wydobądź unikalne sekwencje o najmniejszej liczbie błędów pozytywnych
		for (Dna d : solutions) {
			boolean contains = false;
			for (Dna us : uniqueSolutions) {
				if (d.getCurrentDna().equals(us.getCurrentDna()))
					contains = true;
			}
			if (!contains) {
				uniqueSolutions.add(d);
			}
		}
		//System.out.println("number of solutions " + solutions.size());
		//System.out.println("number of unique solutions "
		//		+ uniqueSolutions.size());
		//System.out.println("UNIQUE SOLUTIONS:");
		for (Dna u : uniqueSolutions) {
			int q = StringUtils.getLevenshteinDistance(inputString,
					u.getCurrentDna());
			//System.out.println("Levenshtein: " + q + "\t" + u);
			output += "\t" + q + "\t" + u.getCurrentDna();
		}
		output += "\t ";
	}

	private void search(long start) {
		// Dna bestDna = null;
		int bestIncorrectness = Integer.MAX_VALUE;
		boolean stopCondition = false;
		int bestNotChanged = 0;
		int randomGenerations = 0;
		boolean generateRandom = true;
		int i = 0;
		while (!stopCondition && !SOLUTIONFOUND) {
			for (Node n : spectrum.getElements()) {
				n.resetVisits();
			}
			Dna dna;
			if (generateRandom) {
				dna = generateRandomDna();
				generateRandom = false;
			} else {
				dna = generateDnaRespectingUrgency();
			}
			// System.out.println("dna" + dna + "         incorr" +
			// incorrectness(dna));
			// System.out.println("best" + bestDna + "         incorr" +
			// incorrectness(bestDna));
			int currentDnaIncorrectness = incorrectness(dna);
			if (currentDnaIncorrectness < bestIncorrectness) {
				Dna deepClonedDna = (Dna) DeepClone.deepClone(dna);
				solutions.add(deepClonedDna);
				if (deepClonedDna.getCurrentDna().length() == lengthOfSolution
						&& deepClonedDna.getNumberOfCriticalErrors() == 0) {
					output += (double) (System.currentTimeMillis() - start) / 1000;
					SOLUTIONFOUND = true;
				}
				bestIncorrectness = currentDnaIncorrectness;
				bestNotChanged = 0;
				// System.out.println("added " + deepClonedDna.toString());
			} /*
			 * else if (currentDnaIncorrectness == bestIncorrectness) { Dna
			 * deepClonedDna = (Dna) DeepClone.deepClone(dna);
			 * solutions.add(deepClonedDna); bestIncorrectness =
			 * currentDnaIncorrectness; System.out.println("added2 " +
			 * deepClonedDna.toString()); }
			 */else {
				bestNotChanged++;
			}
			if (bestNotChanged > 50) {
				// usunąć info o urgency, random dna
				randomGenerations++;
				if (randomGenerations < 10) {
					generateRandom = true;
					for (Node n : spectrum.getElements()) {
						n.resetUrgency();
					}
					bestNotChanged = 0;
				} else {// jeśli ileś razy wygenerowano losowe dna:
					stopCondition = true;
				}
			}
			i++;
			// System.out.println(i + " " + currentDnaIncorrectness);
		}

		// System.out.println("best Incorrectness: " + bestIncorrectness);
		// System.out.println("best: " + bestDna);
		// System.out.println("getNumberOfCriticalErrors" +
		// bestDna.getNumberOfCriticalErrors());
		// System.out.println("getNumberOfPositiveErrors" +
		// bestDna.getNumberOfPositiveErrors());
	}

	private Dna generateRandomDna() {
		Dna dna = new Dna();
		dna.addElementToDna(firstNode, Integer.MAX_VALUE, spectrum);
		boolean added = true;
		while (added && dna.getCurrentDna().length()<=lengthOfSolution) {
			List<Node> matchingElements = spectrum.getMatchingElements(dna);
			if (matchingElements.size() == 0) {
				added = false;
			} else {
				int idx = generator.nextInt(matchingElements.size());
				dna.addElementToDna(matchingElements.get(idx),
						Integer.MAX_VALUE, spectrum);
			}
		}
		// System.out.println(dna);
		return dna;
	}

	private Dna generateDnaRespectingUrgency() {
		Dna dna = new Dna();
		dna.addElementToDna(firstNode, Integer.MAX_VALUE, spectrum);
		boolean added = true;
		while (added && dna.getCurrentDna().length()<=lengthOfSolution) {
			List<Node> matchingElements = spectrum.getMatchingElements(dna);
			if (matchingElements.size() == 0) {
				added = false;
			} else {
				Collections.sort(matchingElements, new NodeUrgencyComparator());
				dna.addElementToDna(matchingElements.get(0), Integer.MAX_VALUE,
						spectrum);
			}
		}
		return dna;
	}

	private int incorrectness(Dna dna) {
		if (dna == null) {
			return Integer.MAX_VALUE;
		}
		// TODO dodać tyle punktów ile razy brakuje wzięcia elementu
		// factors of importance of measure
		int lengthFactor = 99;// 8;
		int positiveErrorsFactor = 1;// 1;
		int criticalErrorsFactor = 7;// 4;
		int incorrectness = 0;
		spectrum.measureCorrectness(dna);
		incorrectness += lengthFactor
				* Math.abs(dna.getCurrentDna().length() - lengthOfSolution);
		incorrectness += positiveErrorsFactor * dna.getNumberOfPositiveErrors();
		incorrectness += criticalErrorsFactor * dna.getNumberOfCriticalErrors();
		return incorrectness;
	}

}
