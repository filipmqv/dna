package com.dna.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.dna.util.DnaComparator;
import com.dna.util.NodeUrgencyComparator;

/**
 * Algorytm tabu
 * @author filipmqv
 *
 */
public class ApproximateAlgorithm {

	private Spectrum spectrum;
	private final int lengthOfSolution;
	private int numberOfSolutionsSoFar = 0;
	//private Dna dna;
	private Node firstNode;
	private List<Dna> solutions = new ArrayList<Dna>();
	private List<Dna> uniqueSolutions = new ArrayList<Dna>();
	//private int maxLengthFound = 0;

	public ApproximateAlgorithm(Spectrum spectrum, int lengthOfSolution,
			Node firstNode) {
		this.spectrum = spectrum;
		this.lengthOfSolution = lengthOfSolution;
		this.firstNode = firstNode;
		//this.dna = new Dna(firstNode);
	}

	// 
	// wybierz pierwszy element, stwórz dla niego listę pasujących elementów
	// (jeśli jest posortowane spektrum to wystarczy przeszukać tylko część
	// listy)

	// dołączaj kolejne node'y, zapamiętuj długość i sekwencję dopóki jest <=n
	// biorąc pod uwagę że wszystkie wierzchołki muszą być wykorzystane x>=min,
	// x<=max razy ewentualnie z błedem pozytywnym

	public void generateSolution() {
		System.out.println("FIRST NODE: " + firstNode);

		tabuSearch(firstNode);

		Collections.sort(solutions, new DnaComparator());
		System.out.println("SOLUTION:");
		for (Dna u : solutions)
			System.out.println(u);
		
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
		System.out.println("number of solutions " + solutions.size());
		System.out.println("number of unique solutions " + uniqueSolutions.size());
		System.out.println("UNIQUE SOLUTIONS:");
		for (Dna u : uniqueSolutions)
			System.out.println(u);
	}
	
	/**
	 * 01: s ← s0
	 * 02: sBest ← s
	 * 03: tabuList ← []
	 * 04: while (not stoppingCondition())
	 * 05:     candidateList ← []
	 * 06:     bestCandidate ← null
	 * 07:     for (sCandidate in sNeighborhood)
	 * 08:         if ( (not tabuList.contains(sCandidate)) 
	 * 					and (fitness(sCandidate) > fitness(bestCandidate)) )
	 * 09:             bestCandidate ← sCandidate
	 * 10:         end
	 * 11:     end
	 * 12:     s ← bestCandidate
	 * 13:     if (fitness(bestCandidate) > fitness(sBest))
	 * 14:         sBest ← bestCandidate
	 * 15:     end
	 * 16:     tabuList.push(bestCandidate);
	 * 17:     if (tabuList.size > maxTabuSize)
	 * 18:         tabuList.removeFirst()
	 * 19:     end
	 * 20: end
	 * 21: return sBest
	 * 
	 * @param node First Node
	 */
	private void tabuSearch(Node node) {
		int maxTabuSize = 20000000;
		//Dna s = generateDnaUsingUrgency();
		Dna s = generateRandomDna();
		Dna sBest = s;
		List<Dna> tabuList = new ArrayList<Dna>();
		//04: TODO while (not stoppingCondition())
		boolean stop = false;
		int stopp=0;
		while (stopp<2) {//(!stop) { // TODO warunek kończący całe szukanie np przez ostatnie 
						// 10 faz nie było polepszenia sBest
			List<Dna> candidateList = new ArrayList<Dna>();
			Dna bestCandidate = null;
			// TODO wygenerować wariacje na temat "s" - sNeighborhood
			List<Dna> sNeighborhood = generateNeighborhood(s);
			for (Dna sCandidate : sNeighborhood) {
				if (!tabuList.contains(sCandidate) && (sCandidate != null) && 
						fitness(sCandidate) > fitness(bestCandidate)) {
					bestCandidate = sCandidate;
				}
			}
			s = bestCandidate;
			if ((bestCandidate != null) && (fitness(bestCandidate) > fitness(sBest))) {
				sBest = bestCandidate;
			}
			//if (bestCandidate != null) {
			tabuList.add(bestCandidate);
			//}
			if (tabuList.size() > maxTabuSize) {
				tabuList.remove(0);
			}
			// TODO zliczać ile razy nie zmienił się best, próg
			stopp++;
		}
	}

	private Dna generateRandomDna() {
		Dna dna = new Dna();
		dna.addElementToDna(firstNode, Integer.MAX_VALUE, spectrum);
		System.out.println("FIRST DNA: " + dna);
		boolean added = true;
		int counter = 0;
		// TODO counter usunąć
		while (added && counter<6) {
			List<Node> matchingElements = spectrum.getMatchingElements(dna);
			if (matchingElements.size() == 0 || counter==5) {
				System.out.println("MATCHONG ELEMENTS: " + matchingElements);
				added = false;
			} else {
				Random rnd = new Random();
				int index = rnd.nextInt(matchingElements.size());
				System.out.println(index + " " + matchingElements.size());
				// TODO usunąć:
				index = 0;
				dna.addElementToDna(matchingElements.get(index), Integer.MAX_VALUE, spectrum);
			}
			counter++;
		}
		System.out.println("RANDOM DNA: "+dna);
		return dna;
	}
	
	private Dna generateDnaUsingUrgency() {
		Dna dna = new Dna();
		dna.addElementToDna(firstNode, Integer.MAX_VALUE, spectrum);
		System.out.println("FIRST DNA: " + dna);
		boolean added = true;
		while (added) {
			List<Node> matchingElements = spectrum.getMatchingElements(dna);
			if (matchingElements.size() == 0) {
				added = false;
			} else {
				Collections.sort(matchingElements, new NodeUrgencyComparator());
				for (Node n : matchingElements) {
					System.out.println(n.getUrgency());
				}
				dna.addElementToDna(matchingElements.get(0), Integer.MAX_VALUE, spectrum);
			}
		}
		System.out.println("URGENCY DNA: "+dna);
		return dna;
	}
	
	/**
	 * Generate DNA's similar to given - 1 (insertion, deletion) or 2 (exchange - ) differences
	 * @param s given DNA
	 * @return list of DNA's in neighborhood
	 */
	private List<Dna> generateNeighborhood(Dna s) {
		List<Dna> sNeighborhood = new ArrayList<Dna>();
		// try insertion
		for (Node n : spectrum.getElements()) {
			List<Dna> possibleDnas = s.tryInsertNodeAnywhere(n);
			sNeighborhood.addAll(possibleDnas);
		}
		//Node n = spectrum.getElements().get(1);
		//System.out.println("GENERATE NEIGH: " + n);
		//List<Dna> possibleDnas = s.tryInsertNodeAnywhere(n);
		//sNeighborhood.addAll(possibleDnas);
		System.out.println("insertion done");
		// try deletion
		/*for (int i=0; i<s.getElements().size(); i++) {
			List<Dna> possibleDnas = s.tryDeleteNodeFromAnywhere(i);
			sNeighborhood.addAll(possibleDnas);
			// try insertion after deleting element
			//for (Node nn : spectrum.getElements()) {
			//	List<Dna> possibleDnas2 = s.tryInsertNodeAnywhere(n);
			//	sNeighborhood.addAll(possibleDnas);
			//}
		}
		System.out.println("Deletion done");*/
		System.out.println("####### NEIGH: " + sNeighborhood);
		return sNeighborhood;
	}
	
	private int fitness(Dna dna) {
		if (dna == null) {
			return Integer.MAX_VALUE;
		}
		// factors of importance of measure
		int lengthFactor = 1;
		int positiveErrorsFactor = 1;
		int criticalErrorsFactor = 1;
		int fitness = 0;
		spectrum.measureCorrectness(dna);
		fitness += lengthFactor * Math.abs(dna.getCurrentDna().length() - lengthOfSolution);
		fitness += positiveErrorsFactor * dna.getNumberOfPositiveErrors();
		fitness += criticalErrorsFactor * dna.getNumberOfCriticalErrors();
		return fitness;
	}
	
}
