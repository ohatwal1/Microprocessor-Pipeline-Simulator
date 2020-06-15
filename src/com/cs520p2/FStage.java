package com.cs520p2;

public class FStage {

    public InstructionInfo outputInstruction;
    public boolean stalled;
    public boolean exStalled;
    public boolean mulStalled;
    private int nextInstAddress;

    public FStage() {
        nextInstAddress = 0;
        stalled = false;
        exStalled = false;
        mulStalled = false;
    }

    public void setNextInstAddress(int newNextInstAddress) {
        int temp = (newNextInstAddress - ProjectConsts.codeBaseAddress) / ProjectConsts.codeInstructionLength;
        nextInstAddress = temp;
    }



    public void execute() {
        if (stalled || exStalled || mulStalled) {
            //System.out.println("FStage is stalled. returning...");
            return;
        }
        else if (Pipeline.IsBranching() || Pipeline.isHalted()) {
            outputInstruction = null;
            return;
        }

        //Fetch new instruction
        CodeLine cl = CodeMemory.getInstruction(nextInstAddress);

        if (cl == null) {
            // Instructions finished.
            outputInstruction = null;
        }
        else {

            InstructionInfo ii = new InstructionInfo(cl.getInsString(), cl.getAddress(), nextInstAddress);

            nextInstAddress++;

            outputInstruction = ii;
        }

    }

    public boolean isStalled() {
        return stalled;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public String getCurInstr() {
        if (outputInstruction == null) {
            return "";
        }
        return "(I" + String.valueOf(outputInstruction.getSequenceNo()) + ")";
    }

    public String getCurInstrString() {
        if (outputInstruction == null) {
            return "Empty";
        }
        return outputInstruction.getInsString();
    }

    public boolean isExStalled() {
        return exStalled;
    }

    public void setExStalled(boolean exStalled) {
        this.exStalled = exStalled;
    }

    public boolean isMulStalled() { return mulStalled; }

    public void setMulStalled(boolean mulStalled) { this.mulStalled = mulStalled; }

    public String getStalledStr() {
        if ((stalled || exStalled || mulStalled) && outputInstruction != null)
            return "Stalled";
        return "";
    }



}
