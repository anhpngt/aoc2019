package solution;

import java.util.Arrays;

public class Intcode {
    public static final int OP_ADD = 1;
    public static final int OP_MULTIPLY = 2;
    public static final int OP_INPUT = 3;
    public static final int OP_OUTPUT = 4;
    public static final int OP_HALT = 99;

    public static final int[] INSTRUCTION_OFFSET = { 0 /* offset */, 4, 4, 2, 2 };

    public static final int MODE_POSITION = 0;
    public static final int MODE_IMMEDIATE = 1;

    public static final int USER_INPUT = 1;

    private int[] mInstructionArray;

    public Intcode(int[] instructionArray) {
        mInstructionArray = instructionArray;
    }

    public Intcode(String[] instructionStrArray) {
        mInstructionArray = Arrays.asList(instructionStrArray).stream().mapToInt(Integer::parseInt).toArray();
    }

    public void run() {
        int position = 0;
        while (true) {
            Operation op = determineCurrentOp(position);
            int opCode = op.opcode();
            switch (opCode) {
            case OP_ADD:
                opAdd(position, op.modes());
                break;
            case OP_MULTIPLY:
                opMultiply(position, op.modes());
                break;
            case OP_INPUT:
                opInput(position);
                break;
            case OP_OUTPUT:
                opOutput(position, op.modes());
                break;

            // Program exits if opcode is 99 (halt) or unknown values (error)
            case OP_HALT:
                System.out.println("Program exits normally.");
                return;
            default:
                throw new RuntimeException("Invalid operation" + String.valueOf(op.opcode()));
            }

            position += INSTRUCTION_OFFSET[opCode];
        }
    }

    public Operation determineCurrentOp(int position) {
        int instruction = mInstructionArray[position];
        return new Operation(instruction);
    }

    public void opAdd(int position, int[] modes) {
        int param1 = mInstructionArray[position + 1];
        int param2 = mInstructionArray[position + 2];
        int destination = mInstructionArray[position + 3];

        // Actual operand values depend on mode
        int operand1 = modes[0] == MODE_POSITION ? mInstructionArray[param1] : param1;
        int operand2 = modes[1] == MODE_POSITION ? mInstructionArray[param2] : param2;

        mInstructionArray[destination] = operand1 + operand2;
    }

    public void opMultiply(int position, int[] modes) {
        int param1 = mInstructionArray[position + 1];
        int param2 = mInstructionArray[position + 2];
        int destination = mInstructionArray[position + 3];

        // Actual operand values depend on mode
        int operand1 = modes[0] == MODE_POSITION ? mInstructionArray[param1] : param1;
        int operand2 = modes[1] == MODE_POSITION ? mInstructionArray[param2] : param2;

        mInstructionArray[destination] = operand1 * operand2;
    }

    public void opInput(int position) {
        // Write the provided input to the specified position
        int destination = mInstructionArray[position + 1];

        // Provide 1 according to the problem
        mInstructionArray[destination] = USER_INPUT;
    }

    public void opOutput(int position, int[] modes) {
        int parameter = mInstructionArray[position + 1];
        int result = modes[0] == 0 ? mInstructionArray[parameter] : parameter;

        // Print an output to the terminal
        System.out.println(result);
    }
}