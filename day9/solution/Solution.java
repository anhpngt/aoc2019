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

        System.out.println("Part 1 solution: " + solvePart1(instructionArray, new long[] { 1 }));
        System.out.println("Part 2 solution: " + solvePart2(instructionArray, new long[] { 2 }));
    }

    private static long solvePart1(long[] instructionArray, long[] inputs) {
        Intcode intcode = new Intcode(instructionArray, inputs);
        return intcode.run();
    }

    private static long solvePart2(long[] instructionArray, long[] inputs) {
        Intcode intcode = new Intcode(instructionArray, inputs);
        return intcode.run();
    }
}