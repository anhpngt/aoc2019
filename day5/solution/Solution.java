package solution;

import java.lang.RuntimeException;

public class Solution {
    public static void main(String args[]) throws Exception {
        if (args.length < 2) {
            throw new RuntimeException("Missing puzzle input file and/or input ID.");
        }

        String inputFilePath = args[0];
        int inputID = Integer.parseInt(args[1]);
        String[] instructionArray = Parser.parseInputFile(inputFilePath);
        Intcode intcode = new Intcode(instructionArray, inputID);
        intcode.run();
    }
}
