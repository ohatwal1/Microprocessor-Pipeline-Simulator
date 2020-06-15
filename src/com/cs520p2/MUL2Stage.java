package com.cs520p2;

public class MUL2Stage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;

    public boolean stalled;

    public MUL2Stage() {
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
                UnifiedRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                UnifiedRegisterFile.SetZFlag(inputInstruction.getdRegAddr(), (inputInstruction.getIntermResult() == 0));
                UnifiedRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
            	break;

            default:
                System.out.println("Error! Unknown instruction opcode found in MUL2 stage!");
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