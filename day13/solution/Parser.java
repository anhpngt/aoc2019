package solution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private Parser() {
    }

    public static String[] parseInputFile(String inputFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String line = reader.readLine();
            if (line == null) {
                throw new RuntimeException("Invalid content for the puzzle input file.");
            }

            return line.split(",");
        }
    }
}