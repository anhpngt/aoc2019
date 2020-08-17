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

        solve(instructionArray);
    }

    private static void solve(long[] instructionArray) {
        Painter painter = new Painter(instructionArray);
        painter.run();
        System.out.println(painter.getPaintedPanelCount());
    }
}
