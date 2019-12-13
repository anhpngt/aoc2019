package solution;

import java.util.HashMap;
import java.util.LinkedList;

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
    private LinkedList<Integer> mInputs;
    private int mOutput;
    private HashMap<Integer, Runnable> mFunc;

    private int mPosition;
    private Operation mOperation;

    public Intcode(int[] instructionArray, int[] inputs) {
        mInstructionArray = instructionArray;
        provideInput(inputs);
        initializeStates();
    }

    public int getCurrentOpcode() {
        return mOperation.opcode();
    }

    public void provideInput(int[] inputs) {
        if (mInputs == null) {
            mInputs = new LinkedList<>();
        }

        for (int e : inputs) {
            mInputs.add(e);
        }
    }

    public int run() {
        mOperation = determineNextOperation();
        while (mOperation.opcode() != OP_HALT) {
            mFunc.get(mOperation.opcode()).run();

            mOperation = determineNextOperation();
        }

        return mOutput;
    }

    public int runUntilOutput() {
        mOperation = determineNextOperation();
        int opcode = mOperation.opcode();
        while (opcode != OP_HALT) {
            mFunc.get(opcode).run();
            if (opcode == OP_OUTPUT) {
                return mOutput;
            }

            mOperation = determineNextOperation();
            opcode = mOperation.opcode();
        }

        return mOutput;
    }

    private void initializeStates() {
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

    private Operation determineNextOperation() {
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
        mInstructionArray[params[0]] = mInputs.poll();
        incrementInstructionPointer(paramNumber);
    }

    private void opOutput() {
        final int paramNumber = 1;
        int[] params = getParameters(paramNumber);
        mOutput = params[0];
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