package solution;

import java.util.Arrays;
import java.util.Vector;

public class Solution {
    public static void main(String args[]) throws Exception {
        if (args.length < 1) {
            throw new RuntimeException("Missing puzzle input file.");
        }

        String inputFilePath = args[0];
        String[] strInstructionArray = Parser.parseInputFile(inputFilePath);
        int[] instructionArray = Arrays.asList(strInstructionArray).stream().mapToInt(Integer::parseInt).toArray();

        int maxThrusterSignal = 0;
        for (int[] phaseSetting : generatePermutation(new int[] { 0, 1, 2, 3, 4 })) {
            AmplifierSystem amps = new AmplifierSystem(instructionArray, 5);
            int thrusterSignal = amps.run(phaseSetting);
            if(thrusterSignal > maxThrusterSignal) {
                maxThrusterSignal = thrusterSignal;
            }
        }
        
        System.out.println(maxThrusterSignal);
    }

    private static Vector<int[]> generatePermutation(int[] arr) {
        Vector<int[]> result = new Vector<>();
        permutations(arr, 0, arr.length, result);
        return result;
    }

    // Reference:
    // https://stackoverflow.com/questions/36373719/java-permutations-of-an-array
    private static void permutations(int[] arr, int loc, int len, Vector<int[]> result) {
        if (loc == len) {
            result.add(arr.clone());
            return;
        }

        permutations(arr, loc + 1, len, result);
        for (int i = loc + 1; i < len; i++) {
            swap(arr, loc, i);
            permutations(arr, loc + 1, len, result);
            swap(arr, loc, i);
        }
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
