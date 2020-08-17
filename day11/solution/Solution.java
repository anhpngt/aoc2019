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
        Painter painter = new Painter(instructionArray);
        painter.run();
        System.out.println("Part 1: " + painter.getPaintedPanelCount());
    }
}
