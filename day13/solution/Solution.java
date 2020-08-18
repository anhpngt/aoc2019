package solution;

import java.util.Arrays;

public class Solution {
    public static void main(String args[]) throws Exception {
        if (args.length < 1) {
            throw new RuntimeException("Missing puzzle input file.");
        }

        String inputFilePath = args[0];
        String[] strInstructionArray = Parser.parseInputFile(inputFilePath);
        long[] instructionArray = Arrays.asList(strInstructionArray).stream().mapToLong(Long::parseLong).toArray();

        solvePart1(instructionArray);
    }

    private static void solvePart1(long[] instructionArray) {
        Screen screen = new Screen(instructionArray);
        screen.startUp();
        System.out.println("Part 1: " + screen.countTileNumber(2));
    }
}
