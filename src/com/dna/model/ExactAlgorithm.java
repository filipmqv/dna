package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dna.util.DeepClone;
import com.dna.util.DnaComparator;

/**
 * Algorytm dok�adny
 * 
 * @author filipmqv
 *
 */
public class ExactAlgorithm {

	private Spectrum spectrum;
	private final int lengthOfSolution;
	private int numberOfSolutionsSoFar = 0;
	private Dna dna;
	private Node firstNode;
	private List<Dna> solutions = new ArrayList<Dna>();
	private List<Dna> uniqueSolutions = new ArrayList<Dna>();
	// private int maxLengthFound = 0;
	private long in = 0, in2 = 0;
	private String inputString;

	public ExactAlgorithm(Spectrum spectrum, int lengthOfSolution,
			Node firstNode, String inputString) {
		this.spectrum = spectrum;
		this.lengthOfSolution = lengthOfSolution;
		this.firstNode = firstNode;
		this.inputString = inputString;
		this.dna = new Dna();
	}

	// wybierz pierwszy element, stw�rz dla niego list� pasuj�cych element�w
	// (je�li jest posortowane spektrum to wystarczy przeszuka� tylko cz��
	// listy)

	// do��czaj kolejne node'y, zapami�tuj d�ugo�� i sekwencj� dop�ki jest <=n
	// bior�c pod uwag� �e wszystkie wierzcho�ki musz� by� wykorzystane x>=min,
	// x<=max razy ewentualnie z b�edem pozytywnym

	public void generateSolution() {
		System.out.println("EXACT");
		System.out.println("FIRST NODE: " + firstNode);

		System.out.println("START");
		long start = System.currentTimeMillis();
		checkNode(firstNode);
		long stop = System.currentTimeMillis();
		System.out.println("STOP");
		long diff = stop - start;
		
		System.out.println("TIME: " + (double)diff/1000 + " s");
		Collections.sort(solutions, new DnaComparator());
		System.out.println("SOLUTION:");
		/*
		 * for (Dna u : solutions) System.out.println(u);
		 */

		// wydob�d� unikalne sekwencje o najmniejszej liczbie b��d�w pozytywnych
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
		System.out.println("number of solutions " + solutions.size());
		System.out.println("number of unique solutions "
				+ uniqueSolutions.size());
		System.out.println("UNIQUE SOLUTIONS:");
		for (Dna u : uniqueSolutions) {
			int q = StringUtils.getLevenshteinDistance(inputString,
					u.getCurrentDna());
			System.out.println("levenst" + q + "\t" + u);
		}
	}

	private void checkNode(Node node) {
		in++;
		if (in % 100000 == 0) {
			in = 1;
			in2++;
			System.out.println(in2 + " " + dna.getCurrentDna().length()
					+ " " + dna.getCurrentPosition() + " "
					+ numberOfSolutionsSoFar + " \t" + dna);
		}
		// je�li pomy�nie dodano element to szukaj, a je�li nie to sprawd�
		// czy sekwencja si� nadaje i return
		if (dna.addElementToDna(node, lengthOfSolution, spectrum)) {
			// najpierw elementy wyd�uzaj�ce rozwi�zanie
			List<Node> matchingElementsLonger = spectrum
					.getMatchingElementsLonger(dna);
			for (Node n : matchingElementsLonger) {
				checkNode(n);
			}

			if (spectrum.isSolutionValid(dna, lengthOfSolution)) {
				Dna deepClonedDna = (Dna) DeepClone.deepClone(dna);
				solutions.add(deepClonedDna);
				numberOfSolutionsSoFar++;
				//System.out.println("### Found1:  " + deepClonedDna.toString());
			}

			List<Node> matchingElementsShorter = spectrum
					.getMatchingElementsShorter(dna);
			for (Node n : matchingElementsShorter) {
				checkNode(n);
			}
			// teraz trzeba cofn�� wprowadzone zmiany - visited, visited w
			// dzieciach
			// solution przyci��, usun�� z listy w dna, przesun��
			// currentPosition '-1'
			dna.removeElementFromDna(dna.getElements().size() - 1);
		} else if (spectrum.isSolutionValid(dna, lengthOfSolution)) {
			Dna deepClonedDna = (Dna) DeepClone.deepClone(dna);
			solutions.add(deepClonedDna);
			numberOfSolutionsSoFar++;
			//System.out.println("### Found2:  " + deepClonedDna.toString());
		}
		return;
	}
}
