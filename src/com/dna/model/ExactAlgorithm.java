package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dna.util.DeepClone;
import com.dna.util.DnaComparator;

/**
 * Algorytm dok³adny
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

	// wybierz pierwszy element, stwórz dla niego listê pasuj¹cych elementów
	// (jeœli jest posortowane spektrum to wystarczy przeszukaæ tylko czêœæ
	// listy)

	// do³¹czaj kolejne node'y, zapamiêtuj d³ugoœæ i sekwencjê dopóki jest <=n
	// bior¹c pod uwagê ¿e wszystkie wierzcho³ki musz¹ byæ wykorzystane x>=min,
	// x<=max razy ewentualnie z b³edem pozytywnym

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

		// wydob¹dŸ unikalne sekwencje o najmniejszej liczbie b³êdów pozytywnych
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
		// jeœli pomyœnie dodano element to szukaj, a jeœli nie to sprawdŸ
		// czy sekwencja siê nadaje i return
		if (dna.addElementToDna(node, lengthOfSolution, spectrum)) {
			// najpierw elementy wyd³uzaj¹ce rozwi¹zanie
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
			// teraz trzeba cofn¹æ wprowadzone zmiany - visited, visited w
			// dzieciach
			// solution przyci¹æ, usun¹æ z listy w dna, przesun¹æ
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
