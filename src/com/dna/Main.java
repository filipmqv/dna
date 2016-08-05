package com.dna;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.dna.model.ApproximateAlgorithmFirstFound;
import com.dna.model.ExactAlgorithmFirstFound;
import com.dna.model.Node;
import com.dna.model.Spectrum;
import com.dna.util.FileManager;

public class Main {

	// String typeOfAlgorithm = "a";// a - approximate przyblizony
	// String typeOfAlgorithm = "e";// e - exact dokladny
	// String typeOfAlgorithm = "af";// af - approximate przyblizony first
	// found
	// String typeOfAlgorithm = "ef";// ef - exact dokladny first found

	final static String typeOfAlgorithm = "af";
	final static String typeOfTest = "dlugosc";
	// final static String typeOfTest = "temperatura";
	//final static String typeOfTest = "bledyNowe";
	//final static String typeOfTest = "bledyPowt";
	final static int percentOfNewNodesErrors = 0;
	final static int percentOfOccurranceErrors = 0;
	final static int temp1 = 30;
	final static int temp2 = temp1 + 2;

	final static String INPUT_FILE_NAME = "TESTY_INPUT_" + typeOfTest + ".txt";
	final static String OUTPUT_FILE_NAME = "WYNIKI_" + typeOfAlgorithm + "_"
			+ typeOfTest + ".txt";
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static void main(String[] args) {

		List<String> outputStrings = new ArrayList<String>();
		FileManager fm = new FileManager();

		List<String> inputStrings = new ArrayList<String>();
		try {
			inputStrings = fm.readTextFile(INPUT_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}

		outputStrings.add("temp1:\t" + temp1 + "\ttemp2:\t" + temp2
				+ "\ttype:\t" + typeOfAlgorithm);
		outputStrings
				.add("lp\t length\t DNA\t time_first_found[s]\t time[s]\t Levenstein's\t");

		Node firstNode; // which oligo is the first one (first from INPUT)

		// System.out.println("INPUT: " + inputString);
		// System.out.println("temp1: " + temp1 + "\ntemp2: " + temp2
		// + "\nLENGTH OF SOLUTION: " + lengthOfSolution);

		if (typeOfTest.equals("dlugosc")) {
			for (int i = 0; i < inputStrings.size(); i++) {
				System.out.print(i + 1 + " ");
				Spectrum spectrum = new Spectrum(inputStrings.get(i), temp1,
						temp2, percentOfNewNodesErrors,
						percentOfOccurranceErrors);

				String sequence0 = spectrum.getElements().get(0).getSequence();
				String sequence1 = spectrum.getElements().get(1).getSequence();
				if (sequence1.length() > sequence0.length() &&
						sequence1.substring(0, sequence0.length()).equals(sequence0)) {
					firstNode = spectrum.getElements().get(1);
				} else {
					firstNode = spectrum.getElements().get(0);
				}
				
				spectrum.sortElements();

				if (typeOfAlgorithm.equals("af"))
					System.out.println(spectrum);

				if (typeOfAlgorithm.equals("af")) {
					ApproximateAlgorithmFirstFound solver = new ApproximateAlgorithmFirstFound(
							spectrum, inputStrings.get(i).length(), firstNode,
							inputStrings.get(i));
					solver.generateSolution();
					outputStrings.add(i + 1 + "\t"
							+ inputStrings.get(i).length() + "\t"
							+ inputStrings.get(i) + "\t" + solver.output);
				} else if (typeOfAlgorithm.equals("ef")) {
					ExactAlgorithmFirstFound solver = new ExactAlgorithmFirstFound(
							spectrum, inputStrings.get(i).length(), firstNode,
							inputStrings.get(i));
					solver.generateSolution();
					outputStrings.add(i + 1 + "\t"
							+ inputStrings.get(i).length() + "\t"
							+ inputStrings.get(i) + "\t" + solver.output);
				}
			}
		}

		if (typeOfTest.equals("temperatura")) {
			int temps[] = { 41, 37, 33, 29, 25, 21, 17, 13, 9, 5 }; // dla
																	// inputów
																	// d³ugosci
																	// 50
			for (int i = 0; i < inputStrings.size(); i++) {
				for (int j = 0; j < temps.length; j++) {
					System.out.print(i * 10 + j + 1 + " ");
					Spectrum spectrum = new Spectrum(inputStrings.get(i),
							temps[j] - 1, temps[j] + 1,
							percentOfNewNodesErrors, percentOfOccurranceErrors);

					String sequence0 = spectrum.getElements().get(0).getSequence();
					String sequence1 = spectrum.getElements().get(1).getSequence();
					if (sequence1.length() > sequence0.length() &&
							sequence1.substring(0, sequence0.length()).equals(sequence0)) {
						firstNode = spectrum.getElements().get(1);
					} else {
						firstNode = spectrum.getElements().get(0);
					}

					spectrum.sortElements();

					if (typeOfAlgorithm.equals("af"))
						System.out.println(spectrum);

					if (typeOfAlgorithm.equals("af")) {
						ApproximateAlgorithmFirstFound solver = new ApproximateAlgorithmFirstFound(
								spectrum, inputStrings.get(i).length(),
								firstNode, inputStrings.get(i));
						solver.generateSolution();
						outputStrings.add(i * 10 + j + 1 + "\t"
								+ inputStrings.get(i).length() + "\t"
								+ inputStrings.get(i) + "\t" + solver.output);
					} else if (typeOfAlgorithm.equals("ef")) {
						ExactAlgorithmFirstFound solver = new ExactAlgorithmFirstFound(
								spectrum, inputStrings.get(i).length(),
								firstNode, inputStrings.get(i));
						solver.generateSolution();
						outputStrings.add(i * 10 + j + 1 + "\t"
								+ inputStrings.get(i).length() + "\t"
								+ inputStrings.get(i) + "\t" + solver.output);
					}
				}
			}
		}

		if (typeOfTest.equals("bledyNowe")
				|| typeOfTest.equals("bledyPowt")) {
			int errors[] = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45 }; 
			for (int i = 0; i < inputStrings.size(); i++) {
				for (int j = 0; j < errors.length; j++) {
					System.out.print(i * 10 + j + 1 + " ");
					Spectrum spectrum = null;
					if (typeOfTest.equals("bledyNowe")) {
						spectrum = new Spectrum(inputStrings.get(i), temp1,
								temp2, errors[j], percentOfOccurranceErrors);
					} else {
						spectrum = new Spectrum(inputStrings.get(i), temp1,
								temp2, percentOfNewNodesErrors, errors[j]);
					}

					String sequence0 = spectrum.getElements().get(0).getSequence();
					String sequence1 = spectrum.getElements().get(1).getSequence();
					if (sequence1.length() > sequence0.length() &&
							sequence1.substring(0, sequence0.length()).equals(sequence0)) {
						firstNode = spectrum.getElements().get(1);
					} else {
						firstNode = spectrum.getElements().get(0);
					}

					spectrum.sortElements();

					if (typeOfAlgorithm.equals("af"))
						System.out.println(spectrum);

					if (typeOfAlgorithm.equals("af")) {
						ApproximateAlgorithmFirstFound solver = new ApproximateAlgorithmFirstFound(
								spectrum, inputStrings.get(i).length(),
								firstNode, inputStrings.get(i));
						solver.generateSolution();
						outputStrings.add(i * 10 + j + 1 + "\t"
								+ inputStrings.get(i).length() + "\t"
								+ inputStrings.get(i) + "\t" + solver.output);
					} else if (typeOfAlgorithm.equals("ef")) {
						ExactAlgorithmFirstFound solver = new ExactAlgorithmFirstFound(
								spectrum, inputStrings.get(i).length(),
								firstNode, inputStrings.get(i));
						solver.generateSolution();
						outputStrings.add(i * 10 + j + 1 + "\t"
								+ inputStrings.get(i).length() + "\t"
								+ inputStrings.get(i) + "\t" + solver.output);
					}
				}
			}
		}

		try {
			fm.writeTextFile(outputStrings, OUTPUT_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
