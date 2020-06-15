package com.cs520p2;

public class MUL1Stage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;

    public MUL1Stage() {
        stalled = false;
    }

    public void execute() {
        if (stalled || inputInstruction == null) {
            if (inputInstruction == null)
                outputInstruction = null;
            return;
        }

        switch (inputInstruction.getOpCode()) {
            case ADD:
            case SUB:
            case ADDL:
            case SUBL:
            case LOAD:
            case STORE:
            case MOVC:
            case AND:
            case OR:
            case EXOR:
            case BZ:
            case BNZ:
            case JUMP:
            case HALT:
            case NOOP:
                break;

            case MUL:
                if (! inputInstruction.isLiteralPresent()) {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() * inputInstruction.getsReg2Val());
                }
                else {
                    inputInstruction.setIntermResult(inputInstruction.getsReg1Val() * inputInstruction.getLiteral());
                }

                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in MUL1 stage!");
                break;
        }

        // Let's give this instruction to output latch.
        outputInstruction = inputInstruction;

    }

    public String getCurInstr() {
        if (inputInstruction == null) {
            return "";
        }
        return "(I" + String.valueOf(inputInstruction.getSequenceNo()) + ")";
    }

    public String getCurInstrString() {
        if (inputInstruction == null) {
            return "Empty";
        }
        return inputInstruction.getInsString();
    }

    public String getStalledStr() {
        if (stalled && inputInstruction != null)
            return "Stalled";
        return "";
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }
}
