package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dna.util.DeepClone;
import com.dna.util.DnaComparator;

/**
 * Algorytm dok³adny, zatrzymuj¹cy siê po znalezieniu pierwszego poprawnego rozwi¹zania
 * czyli o mierze Levenshteina = 0;
 * 
 * @author filipmqv
 *
 */
public class ExactAlgorithmFirstFound {

	private boolean SOLUTIONFOUND = false;
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
	public String output = "";

	public ExactAlgorithmFirstFound(Spectrum spectrum, int lengthOfSolution,
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
		//System.out.println("EXACT_FIRST");
		//System.out.println("FIRST NODE: " + firstNode);
		//output += "EXACT_FIRST";
		
		//System.out.println("START");
		long start = System.currentTimeMillis();
		checkNode(firstNode, start);
		long stop = System.currentTimeMillis();
		if (!SOLUTIONFOUND) {
			output += " --- ";
		}
		//System.out.println("STOP");
		long diff = stop - start;
		
		System.out.println("TIME: " + (double)diff/1000 + " s");
		if ((double)diff/1000 < 60) {
			output += "\t" + (double)diff/1000;
		} else output += "\t ---- ";
		Collections.sort(solutions, new DnaComparator());
		//System.out.println("SOLUTION:");
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
		//System.out.println("number of solutions " + solutions.size());
		//System.out.println("number of unique solutions "
		//		+ uniqueSolutions.size());
		//System.out.println("UNIQUE SOLUTIONS:");
		for (Dna u : uniqueSolutions) {
			int q = StringUtils.getLevenshteinDistance(inputString,
					u.getCurrentDna());
			//System.out.println("levenst" + q + "\t" + u);
			output += "\t" + q + "\t" + u.getCurrentDna();
		}
		output += "\t ";
	}

	private void checkNode(Node node, long start) {
		if (!SOLUTIONFOUND && System.currentTimeMillis() - start > 20000) {//60s*1000
			System.out.println("Out of time");
			output += "Out of time";
			SOLUTIONFOUND = true;
			return;
		}
		// jeœli pomyœnie dodano element to szukaj, a jeœli nie to sprawdŸ
		// czy sekwencja siê nadaje i return
		if (dna.addElementToDna(node, lengthOfSolution, spectrum)) {
			// najpierw elementy wyd³uzaj¹ce rozwi¹zanie
			List<Node> matchingElementsLonger = spectrum
					.getMatchingElementsLonger(dna);
			for (Node n : matchingElementsLonger) {
				checkNode(n, start);
				if (SOLUTIONFOUND) {
					return;
				}
			}

			if (spectrum.isSolutionValid(dna, lengthOfSolution)) {
				Dna deepClonedDna = (Dna) DeepClone.deepClone(dna);
				solutions.add(deepClonedDna);
				numberOfSolutionsSoFar++;
				//System.out.println("### Found1:  " + deepClonedDna.toString());
				output += (double) (System.currentTimeMillis() - start) / 1000;
				SOLUTIONFOUND = true;
				return;
			}

			List<Node> matchingElementsShorter = spectrum
					.getMatchingElementsShorter(dna);
			for (Node n : matchingElementsShorter) {
				checkNode(n, start);
				if (SOLUTIONFOUND) {
					return;
				}
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
			output += (double) (System.currentTimeMillis() - start) / 1000;
			SOLUTIONFOUND = true;
			return;
		}
		return;
	}
}
