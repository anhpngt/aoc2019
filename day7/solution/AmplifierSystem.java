package solution;

import java.util.Vector;

public class AmplifierSystem {
    public static final int INPUT_VALUE = 0;

    private int[] mInstructionArray;
    private int mAmplifierNumber;

    AmplifierSystem(int[] instructionArray, int amplifierNumber) {
        mInstructionArray = instructionArray;
        mAmplifierNumber = amplifierNumber;
    }

    public int run(int[] phaseSettings) {
        if (phaseSettings.length != mAmplifierNumber) {
            throw new RuntimeException("Invalid length of phase settings.");
        }

        int signal = INPUT_VALUE;
        for (int i = 0; i < mAmplifierNumber; i++) {
            int[] amplifierInputs = { phaseSettings[i], signal };
            Intcode amplifier = new Intcode(mInstructionArray, amplifierInputs);
            signal = amplifier.run();
        }

        return signal;
    }

    public int runWithFeedback(int[] phaseSettings) {
        if (phaseSettings.length != mAmplifierNumber) {
            throw new RuntimeException("Invalid length of phase settings.");
        }

        // Initialize the amplifiers for the first time
        Vector<Intcode> amplifiers = new Vector<>();
        for (int i = 0; i < mAmplifierNumber; i++) {
            // Provide phase setting at initialization
            amplifiers.add(new Intcode(mInstructionArray.clone(), new int[] { phaseSettings[i] }));
        }

        int signal = INPUT_VALUE;
        int amplifierIdx = 0;
        while (true) {
            Intcode amplifier = amplifiers.get(amplifierIdx);
            amplifier.provideInput(new int[] { signal });
            signal = amplifier.runUntilOutput();
            if (amplifierIdx == mAmplifierNumber - 1 && amplifier.getCurrentOpcode() == Intcode.OP_HALT) {
                return signal;
            }

            amplifierIdx = (amplifierIdx + 1) % mAmplifierNumber;
        }
    }
}