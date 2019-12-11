package solution;

import java.lang.RuntimeException;

public class Solution {
    public static void main(String args[]) throws Exception {
        if (args.length < 1) {
            throw new RuntimeException("Missing puzzle input argument.");
        }

        String inputFilePath = args[0];
        String[] inputArray = Parser.parseInputFile(inputFilePath);
        Intcode intcode = new Intcode(inputArray);
        intcode.run();
    }
}
