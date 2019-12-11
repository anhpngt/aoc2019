package solution;

import java.util.Arrays;
import java.util.HashMap;

public class Intcode {
    public static final int OP_ADD = 1;
    public static final int OP_MULTIPLY = 2;
    public static final int OP_INPUT = 3;
    public static final int OP_OUTPUT = 4;
    public static final int OP_JUMPIFTRUE = 5;
    public static final int OP_JUMPIFFALSE = 6;
    public static final int OP_LESSTHAN = 7;
    public static final int OP_EQUALS = 8;
    public static final int OP_HALT = 99;

    public static final int MODE_POSITION = 0;
    public static final int MODE_IMMEDIATE = 1;

    private int[] mInstructionArray;
    private int mUserInput;
    private HashMap<Integer, Runnable> mFunc;

    private int mPosition;
    private Operation mOperation;

    public Intcode(int[] instructionArray, int userInput) {
        mInstructionArray = instructionArray;
        mUserInput = userInput;
        initialize();
    }

    public Intcode(String[] instructionStrArray, int userInput) {
        mInstructionArray = Arrays.asList(instructionStrArray).stream().mapToInt(Integer::parseInt).toArray();
        mUserInput = userInput;
        initialize();
    }

    public void run() {
        mOperation = determineCurrentOp();
        while (mOperation.opcode() != OP_HALT) {
            mFunc.get(mOperation.opcode()).run();

            mOperation = determineCurrentOp();
        }
    }

    private void initialize() {
        mPosition = 0;

        mFunc = new HashMap<>();
        mFunc.put(OP_ADD, () -> this.opAdd());
        mFunc.put(OP_MULTIPLY, () -> this.opMultiply());
        mFunc.put(OP_INPUT, () -> this.opInput());
        mFunc.put(OP_OUTPUT, () -> this.opOutput());
        mFunc.put(OP_JUMPIFTRUE, () -> this.opJumpIfTrue());
        mFunc.put(OP_JUMPIFFALSE, () -> this.opJumpIfFalse());
        mFunc.put(OP_LESSTHAN, () -> this.opLessThan());
        mFunc.put(OP_EQUALS, () -> this.opEquals());
        mFunc.put(OP_HALT, () -> {
        }); // does nothing
    }

    private Operation determineCurrentOp() {
        int instruction = mInstructionArray[mPosition];
        return new Operation(instruction);
    }

    private int[] getParameters(int paramNumber) {
        // Apply mode to all the parameters
        return getParameters(paramNumber, false);
    }

    private int[] getParameters(int paramNumber, boolean destinationRequired) {
        int[] paramArray = new int[paramNumber];
        int[] modes = mOperation.modes();

        for (int i = 0; i < paramNumber; i++) {
            int paramValue = mInstructionArray[mPosition + i + 1];

            if (modes[i] == MODE_IMMEDIATE || (destinationRequired && i == paramNumber - 1)) {
                paramArray[i] = paramValue; // immediate mode
            } else {
                paramArray[i] = mInstructionArray[paramValue]; // position mode
            }
        }

        return paramArray;
    }

    private void incrementInstructionPointer(int paramNumber) {
        mPosition += paramNumber + 1;
    }

    private void opAdd() {
        final int paramNumber = 3;
        int[] params = getParameters(paramNumber, true);
        mInstructionArray[params[2]] = params[0] + params[1];
        incrementInstructionPointer(paramNumber);
    }

    private void opMultiply() {
        final int paramNumber = 3;
        int[] params = getParameters(paramNumber, true);
        mInstructionArray[params[2]] = params[0] * params[1];
        incrementInstructionPointer(paramNumber);
    }

    private void opInput() {
        final int paramNumber = 1;
        int[] params = getParameters(paramNumber, true);
        mInstructionArray[params[0]] = mUserInput;
        incrementInstructionPointer(paramNumber);
    }

    private void opOutput() {
        final int paramNumber = 1;
        int[] params = getParameters(paramNumber);
        System.out.println(params[0]);
        incrementInstructionPointer(paramNumber);
    }

    private void opJumpIfTrue() {
        final int paramNumber = 2;
        int[] params = getParameters(paramNumber);
        if (params[0] != 0) {
            mPosition = params[1];
        } else {
            incrementInstructionPointer(paramNumber);
        }
    }

    private void opJumpIfFalse() {
        final int paramNumber = 2;
        int[] params = getParameters(paramNumber);
        if (params[0] == 0) {
            mPosition = params[1];
        } else {
            incrementInstructionPointer(paramNumber);
        }
    }

    private void opLessThan() {
        final int paramNumber = 3;
        int[] params = getParameters(paramNumber, true);
        mInstructionArray[params[2]] = params[0] < params[1] ? 1 : 0;
        incrementInstructionPointer(paramNumber);
    }

    private void opEquals() {
        final int paramNumber = 3;
        int[] params = getParameters(paramNumber, true);
        mInstructionArray[params[2]] = params[0] == params[1] ? 1 : 0;
        incrementInstructionPointer(paramNumber);
    }
}