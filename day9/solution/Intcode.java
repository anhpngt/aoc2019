package solution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public class Intcode {
    public static final int OP_ADD = 1;
    public static final int OP_MULTIPLY = 2;
    public static final int OP_INPUT = 3;
    public static final int OP_OUTPUT = 4;
    public static final int OP_JUMPIFTRUE = 5;
    public static final int OP_JUMPIFFALSE = 6;
    public static final int OP_LESSTHAN = 7;
    public static final int OP_EQUALS = 8;
    public static final int OP_RELATIVEBASEOFFSET = 9;
    public static final int OP_HALT = 99;

    public static final int MODE_POSITION = 0;
    public static final int MODE_IMMEDIATE = 1;
    public static final int MODE_RELATIVE = 2;

    private Vector<Long> mInstructionArray;
    private LinkedList<Long> mInputs;
    private long mOutput;
    private HashMap<Integer, Runnable> mFunc;

    private int mPosition;
    private int mRelativeBase;
    private Operation mOperation;

    public Intcode(long[] instructionArray, long[] inputs) {
        mInstructionArray = new Vector<>();
        for (long e : instructionArray) {
            mInstructionArray.add(e);
        }
        provideInput(inputs);
        initializeStates();
    }

    public int getCurrentOpcode() {
        return mOperation.opcode();
    }

    public void provideInput(long[] inputs) {
        if (mInputs == null) {
            mInputs = new LinkedList<>();
        }

        for (long e : inputs) {
            mInputs.add(e);
        }
    }

    public long run() {
        mOperation = determineNextOperation();
        while (mOperation.opcode() != OP_HALT) {
            mFunc.get(mOperation.opcode()).run();

            mOperation = determineNextOperation();
        }

        return mOutput;
    }

    public long runUntilOutput() {
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
        mRelativeBase = 0;

        mFunc = new HashMap<>();
        mFunc.put(OP_ADD, () -> this.opAdd());
        mFunc.put(OP_MULTIPLY, () -> this.opMultiply());
        mFunc.put(OP_INPUT, () -> this.opInput());
        mFunc.put(OP_OUTPUT, () -> this.opOutput());
        mFunc.put(OP_JUMPIFTRUE, () -> this.opJumpIfTrue());
        mFunc.put(OP_JUMPIFFALSE, () -> this.opJumpIfFalse());
        mFunc.put(OP_LESSTHAN, () -> this.opLessThan());
        mFunc.put(OP_EQUALS, () -> this.opEquals());
        mFunc.put(OP_RELATIVEBASEOFFSET, () -> this.opRelativeBaseOffset());
        mFunc.put(OP_HALT, () -> {
        }); // does nothing
    }

    private long getInstructionAtIndex(int index) {
        // Extend the memory of the program til the `index`
        while (index >= mInstructionArray.size()) {
            mInstructionArray.add(0L);
        }

        return mInstructionArray.get(index).longValue();
    }

    private long getInstructionAtIndex(long index) {
        return getInstructionAtIndex((int) index);
    }

    private void setInstructionAtIndex(long value, int index) {
        // Extend the memory of the program til the `index`
        while (index >= mInstructionArray.size()) {
            mInstructionArray.add(0L);
        }

        mInstructionArray.setElementAt(value, index);
    }

    private void setInstructionAtIndex(long value, long index) {
        setInstructionAtIndex(value, (int) index);
    }

    private Operation determineNextOperation() {
        long instruction = getInstructionAtIndex(mPosition);
        return new Operation(instruction);
    }

    private long[] getParameters(int paramNumber) {
        // Apply mode to all the parameters
        return getParameters(paramNumber, false);
    }

    private long[] getParameters(int paramNumber, boolean destinationRequired) {
        long[] paramArray = new long[paramNumber];
        int[] modes = mOperation.modes();

        for (int i = 0; i < paramNumber; i++) {
            long paramValue = getInstructionAtIndex(mPosition + i + 1);

            if (destinationRequired && i == paramNumber - 1) {
                if (modes[i] == MODE_RELATIVE) {
                    paramArray[i] = paramValue + mRelativeBase;
                } else {
                    paramArray[i] = paramValue;
                }
            } else if (modes[i] == MODE_IMMEDIATE) {
                paramArray[i] = paramValue; // immediate mode
            } else if (modes[i] == MODE_POSITION) {
                paramArray[i] = getInstructionAtIndex(paramValue); // position mode
            } else if (modes[i] == MODE_RELATIVE) {
                paramArray[i] = getInstructionAtIndex(paramValue + mRelativeBase);
            }
        }

        return paramArray;
    }

    private void incrementInstructionPointer(int paramNumber) {
        mPosition += paramNumber + 1;
    }

    private void opAdd() {
        final int paramNumber = 3;
        long[] params = getParameters(paramNumber, true);
        setInstructionAtIndex(params[0] + params[1], params[2]);
        incrementInstructionPointer(paramNumber);
    }

    private void opMultiply() {
        final int paramNumber = 3;
        long[] params = getParameters(paramNumber, true);
        setInstructionAtIndex(params[0] * params[1], params[2]);
        incrementInstructionPointer(paramNumber);
    }

    private void opInput() {
        final int paramNumber = 1;
        long[] params = getParameters(paramNumber, true);
        setInstructionAtIndex(mInputs.poll(), params[0]);
        incrementInstructionPointer(paramNumber);
    }

    private void opOutput() {
        final int paramNumber = 1;
        long[] params = getParameters(paramNumber);
        mOutput = params[0];
        System.out.println(mOutput);
        incrementInstructionPointer(paramNumber);
    }

    private void opJumpIfTrue() {
        final int paramNumber = 2;
        long[] params = getParameters(paramNumber);
        if (params[0] != 0L) {
            mPosition = (int) params[1];
        } else {
            incrementInstructionPointer(paramNumber);
        }
    }

    private void opJumpIfFalse() {
        final int paramNumber = 2;
        long[] params = getParameters(paramNumber);
        if (params[0] == 0L) {
            mPosition = (int) params[1];
        } else {
            incrementInstructionPointer(paramNumber);
        }
    }

    private void opLessThan() {
        final int paramNumber = 3;
        long[] params = getParameters(paramNumber, true);
        setInstructionAtIndex(params[0] < params[1] ? 1 : 0, params[2]);
        incrementInstructionPointer(paramNumber);
    }

    private void opEquals() {
        final int paramNumber = 3;
        long[] params = getParameters(paramNumber, true);
        setInstructionAtIndex(params[0] == params[1] ? 1 : 0, params[2]);
        incrementInstructionPointer(paramNumber);
    }

    private void opRelativeBaseOffset() {
        final int paramNumber = 1;
        long[] params = getParameters(paramNumber);
        mRelativeBase += (int) params[0];
        incrementInstructionPointer(paramNumber);
    }
}