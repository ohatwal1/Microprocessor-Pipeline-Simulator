package com.cs520p2;

public class MEM3Stage {
    public InstructionInfo outputInstruction;
    public InstructionInfo inputInstruction;
    public boolean stalled;
    private int clockCycleNo;

    public MEM3Stage() {
        stalled = false;
    }

    public void execute() {

        if (!stalled && inputInstruction == null) {
            // Not currently processing any instruction and no new instruction
            outputInstruction = null;
            return;
        }

        else if (!stalled && inputInstruction != null) {
            // Got new instruction
            stalled = true;
            clockCycleNo = 2;
            outputInstruction = null;
            return;
        }

        else if (stalled && clockCycleNo > 1) {
            // Currently processing an instruction which is not in its last clock cycle
            clockCycleNo--;
            outputInstruction = null;
            return;
        }

        // Processing an instruction which is in last clock cycle.

        switch (inputInstruction.getOpCode()) {

            case LOAD:
                inputInstruction.setIntermResult(DataMemory.readFromMemory(inputInstruction.getIntermResult()));

                UnifiedRegisterFile.WriteToRegister(inputInstruction.getdRegAddr(), inputInstruction.getIntermResult());
                UnifiedRegisterFile.SetRegisterStatus(inputInstruction.getdRegAddr(), true);

                ROB.setResult(inputInstruction.getDispatchedClockCycle(), inputInstruction.getIntermResult());
                ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case STORE:
                DataMemory.writeToMemory(inputInstruction.getsReg1Val(), inputInstruction.getIntermResult());

                //ROB.setStatus(inputInstruction.getDispatchedClockCycle(), true);
                break;

            case ADD:
            case SUB:
            case ADDL:
            case SUBL:
            case MUL:
            case MOVC:
            case AND:
            case OR:
            case EXOR:
            case BZ:
            case BNZ:
            case JUMP:
            case HALT:
            case NOOP:
            case JAL:
                break;

            default:
                System.out.println("Error! Unknown instruction opcode found in MEM stage!");
                break;
        }

        // Let's give this instruction to output latch.
        outputInstruction = inputInstruction;
        stalled = false;
    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
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
        if (stalled)
            return "Stalled";
        return "";
    }
}
