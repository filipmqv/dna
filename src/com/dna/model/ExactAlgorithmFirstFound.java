package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.dna.util.DeepClone;
import com.dna.util.DnaComparator;

/**
 * Algorytm dok�adny, zatrzymuj�cy si� po znalezieniu pierwszego poprawnego rozwi�zania
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

	// wybierz pierwszy element, stw�rz dla niego list� pasuj�cych element�w
	// (je�li jest posortowane spektrum to wystarczy przeszuka� tylko cz��
	// listy)

	// do��czaj kolejne node'y, zapami�tuj d�ugo�� i sekwencj� dop�ki jest <=n
	// bior�c pod uwag� �e wszystkie wierzcho�ki musz� by� wykorzystane x>=min,
	// x<=max razy ewentualnie z b�edem pozytywnym

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
		// je�li pomy�nie dodano element to szukaj, a je�li nie to sprawd�
		// czy sekwencja si� nadaje i return
		if (dna.addElementToDna(node, lengthOfSolution, spectrum)) {
			// najpierw elementy wyd�uzaj�ce rozwi�zanie
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
			output += (double) (System.currentTimeMillis() - start) / 1000;
			SOLUTIONFOUND = true;
			return;
		}
		return;
	}
}
