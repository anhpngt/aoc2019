package solution;

public class Operation {
    private int mOpCode;
    private int[] mParamModes;

    public Operation(int instruction) {
        mOpCode = instruction % 100;
        mParamModes = new int[] { (instruction / 100) % 10, (instruction / 1000) % 10, (instruction / 10000) % 10 };
    }

    public Operation(long instruction) {
        this((int) instruction);
    }

    public int opcode() {
        return mOpCode;
    }

    public int[] modes() {
        return mParamModes;
    }
}