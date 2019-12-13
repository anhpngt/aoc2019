package solution;

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
}