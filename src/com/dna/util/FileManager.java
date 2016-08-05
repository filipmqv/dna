package com.dna.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileManager {

	public FileManager() {
		
	}
	
	/*public static void main(String[] args) throws IOException {
		FileReader text = new FileReader();

		// treat as a small file
		List<String> lines = text.readSmallTextFile(FILE_NAME);
		log(lines);
		lines.add("This is a line added in code.");
		text.writeSmallTextFile(lines, FILE_NAME);

		// treat as a large file - use some buffering
		text.readLargerTextFile(FILE_NAME);
		lines = Arrays.asList("Down to the Waterline", "Water of Love");
		text.writeLargerTextFile(OUTPUT_FILE_NAME, lines);
	}*/

	final static Charset ENCODING = StandardCharsets.UTF_8;


	/**
	 * Note: the javadoc of Files.readAllLines says it's intended for small
	 * files. But its implementation uses buffering, so it's likely good even
	 * for fairly large files.
	 */
	public List<String> readTextFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		return Files.readAllLines(path, ENCODING);
	}

	public void writeTextFile(List<String> aLines, String aFileName)
			throws IOException {
		Path path = Paths.get(aFileName);
		Files.write(path, aLines, ENCODING);
	}

	// For larger files
/*
	static void readLargerTextFile(String aFileName) throws IOException {
		Path path = Paths.get(aFileName);
		try (Scanner scanner = new Scanner(path, ENCODING.name())) {
			while (scanner.hasNextLine()) {
				// process each line in some way
				log(scanner.nextLine());
			}
		}
	}

	void readLargerTextFileAlternate(String aFileName) throws IOException {
		Path path = Paths.get(aFileName);
		try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				log(line);
			}
		}
	}

	void writeLargerTextFile(String aFileName, List<String> aLines)
			throws IOException {
		Path path = Paths.get(aFileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
			for (String line : aLines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	private static void log(Object aMsg) {
		System.out.println(String.valueOf(aMsg));
	}
*/
}