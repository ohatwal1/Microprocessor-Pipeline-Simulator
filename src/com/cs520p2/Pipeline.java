package com.cs520p2;

public class Pipeline {

    private static boolean halted = false;
    private static boolean branch = false;
    private static int instructionsCompleted;
    private static int targetAddress;
    private static int branchClockCycle;
    private static int branchInstrCFID;
    private static FStage fs;
    private static DRFStage drfs;
    private static EXStage exs;
    private static MUL1Stage mul1s;
    private static MUL2Stage mul2s;
    private static MEM3Stage mem3s;
    private static InstructionInfo loadForwarded;

    public static void Setup() {
        fs = new FStage();
        drfs = new DRFStage();
        exs = new EXStage();
        mul1s = new MUL1Stage();
        mul2s = new MUL2Stage();
        mem3s = new MEM3Stage();
        halted = false;
        branch = false;
        loadForwarded = null;
    }

    public static void TakeBranch(int newTargetAddress, int dispatchedBranchClockCycle, int branchCFID, int branchPC) {
        halted = false;
        branch = true;
        targetAddress = newTargetAddress;
        branchClockCycle = dispatchedBranchClockCycle;
        branchInstrCFID = branchCFID;
    }

    public static boolean IsBranching() {
        return branch;
    }

    public static boolean isHalted() {
        return halted;
    }

    public static void setHalted(boolean halted) {
        Pipeline.halted = halted;
    }

    public static void Simulate(int clockCycles) {

        for (int i = 1; i <= clockCycles; i++) {


            ROB.commit(1, mem3s);

            mem3s.execute();

            mul2s.execute();

            mul1s.execute();

            exs.execute();

            drfs.execute(i);

            fs.execute();

            System.out.println("Cycle " + String.valueOf(i) + ":");
            System.out.println("Fetch       : " + fs.getCurInstr() + " " + fs.getCurInstrString() + " " + fs.getStalledStr());
            System.out.println("DRF         : " + drfs.getCurInstr() + " " + drfs.getCurInstrString() + " " + drfs.getStalledStr() + "\n");
            System.out.println("<RENAME TABLE> : " + UnifiedRegisterFile.printRenameTableEntries() + "\n");
            System.out.println("<REV RENAME TABLE> : " + UnifiedRegisterFile.printRevRenameTableEntries() + "\n");
            System.out.println("<LSQ> :" + LSQ.printCurrentInstructions() + "\n");
            System.out.println("<ROB> :" + ROB.printCurrentInstructions() + "\n");
            System.out.println("<IQ> :" + IssueQueue.printCurrentInstructions() + "\n");
            System.out.println("INTFU       : " + exs.getCurInstr() + " " + exs.getCurInstrString() + " " + exs.getStalledStr());
            System.out.println("MUL1        : " + mul1s.getCurInstr() + " " + mul1s.getCurInstrString() + " " + mul1s.getStalledStr());
            System.out.println("MUL2        : " + mul2s.getCurInstr() + " " + mul2s.getCurInstrString() + " " + mul2s.getStalledStr());
            System.out.println("MEM         : " + mem3s.getCurInstr() + " " + mem3s.getCurInstrString() + " " + mem3s.getStalledStr() + "\n");
            ROB.printCommittedInstructions();
            System.out.println("================================\n");
            DataForwarding();

            // If DRF stage is stalled, Fetch is stalled too.
            if (drfs.isStalled()) {
                fs.setStalled(true);
            }
            else {
                fs.setStalled(false);
            }

            // In case of branching, flush the instructions in DRF and F stage and start fetching from target address
            if (branch) {
                FlushInstructions(branchClockCycle, branchInstrCFID);
                fs.setNextInstAddress(targetAddress);
                branch = false;
            }

            // If pipeline is halted, stop fetching.
            if (halted) {
                fs.outputInstruction = null;
            }

            // Logic for sending instructions from one stage to the next

            // MUL2 <-- MUL1
            if (! mul2s.isStalled()) {
                // Multiply units are not stalled. Instructions can be given from MUL1 to MUL2.
                mul2s.inputInstruction = mul1s.outputInstruction;
                mul1s.setStalled(false);
            }

            // MEM <-- LSQ
            if (! mem3s.isStalled()) {
                mem3s.inputInstruction = LSQ.getNextInstruction();
            }

            // MUL, EX <-- IQ
            mul1s.inputInstruction = IssueQueue.getNextInstruction(ProjectConsts.FU.MUL);
            exs.inputInstruction = IssueQueue.getNextInstruction(ProjectConsts.FU.INT);

            // IQ, LSQ, ROB <-- DRF
            if (drfs.outputInstruction != null) {
                if (drfs.outputInstruction.getOpCode() != ProjectConsts.I.HALT)
                    IssueQueue.add(drfs.outputInstruction, i);
                drfs.addToROB();
                drfs.addToLSQ();
            }

            // DRF <-- F
            if (! fs.isStalled()) {
                drfs.inputInstruction = fs.outputInstruction;
                fs.outputInstruction = null;
            }
        }

        System.out.println("Clock cycles simulated      : " + String.valueOf(clockCycles));
        System.out.println("Instructions completed      : " + String.valueOf(instructionsCompleted));
        System.out.println("CPI                         : " + String.valueOf((float)clockCycles/instructionsCompleted));

    }

    private static void ForwardToIQandLSQ(InstructionInfo outputInstruction) {

        int destReg = outputInstruction.getdRegAddr();
        int data = outputInstruction.getIntermResult();
        if (destReg != -1) {
            IssueQueue.GetForwardedData(destReg, data);
            LSQ.GetForwardedData(destReg, data);
        }
    }

    private static void ForwardToDRF(InstructionInfo outputInstruction, int drfSrc1, int drfSrc2) {

        // Forward the registers
        int destReg = outputInstruction.getdRegAddr();
        if (destReg != -1) {
            if (destReg == drfSrc1) {
                drfs.inputInstruction.setsReg1Val(outputInstruction.getIntermResult());
                drfs.inputInstruction.setSrc1Forwarded(true);
            }

            if (destReg == drfSrc2) {
                drfs.inputInstruction.setsReg2Val(outputInstruction.getIntermResult());
                drfs.inputInstruction.setSrc2Forwarded(true);
            }
        }
    }

    public static void DataForwarding() {
        int src1;
        int src2;

        // Forwarding to IQ, LSQ

        // Forwarding from EX to IQ, LSQ
        if (exs.outputInstruction != null && exs.outputInstruction.getOpCode() != ProjectConsts.I.LOAD)
            ForwardToIQandLSQ(exs.outputInstruction);

        // Forwarding from MUL2 to IQ, LSQ
        if (mul2s.outputInstruction != null)
            ForwardToIQandLSQ(mul2s.outputInstruction);

        // Forwarding from MEM to IQ, LSQ
        if (mem3s.outputInstruction != null && mem3s.outputInstruction.getOpCode() == ProjectConsts.I.LOAD)
            ForwardToIQandLSQ(mem3s.outputInstruction);

        // Forwarding from LOAD-Forwarded instruction to IQ and LSQ
        if (loadForwarded != null)
            ForwardToIQandLSQ(loadForwarded);

        // TODO: This condition is moved here from top of this function. Remove this TODO only when all test cases are working.
        if (branch)
            return;

        // Forwarding to instruction in DRF

        // Do forwarding only if the instruction in DRF is not stalled
        if (drfs.inputInstruction != null && (! drfs.isStalled())) {

            src1 = drfs.inputInstruction.getsReg1Addr();
            src2 = drfs.inputInstruction.getsReg2Addr();

            // Forwarding from EX to DRF
            if (exs.outputInstruction != null && exs.outputInstruction.getOpCode() != ProjectConsts.I.LOAD)
                ForwardToDRF(exs.outputInstruction, src1, src2);

            // Forwarding from MUL2 to DRF
            if (mul2s.outputInstruction != null)
                ForwardToDRF(mul2s.outputInstruction, src1, src2);

            // Forwarding from MEM to DRF
            if (mem3s.outputInstruction != null && mem3s.outputInstruction.getOpCode() == ProjectConsts.I.LOAD)
                ForwardToDRF(mem3s.outputInstruction, src1, src2);


            // Forwarding from LOAD-Forwarded instruction to DRF
            if (loadForwarded != null)
                ForwardToDRF(loadForwarded, src1, src2);

            // Set loadForwarded to null because we don't want to again forward it in the next clock cycle.
            loadForwarded = null;
        }
    }

    public static void FlushInstructions(int branchClockCycle, int branchInstrCFID) {
        drfs.outputInstruction = null;
        fs.outputInstruction = null;

        ROB.FlushInstructions(branchClockCycle);

        int CFIDindex = CFIDQueue.getIndexOfDispatchedCFID(branchInstrCFID);

        for(CFIDindex += 1; CFIDindex < CFIDQueue.dispatchedCFID.size();) {

            int CFIDtoFlush = CFIDQueue.dispatchedCFID.get(CFIDindex);

            IssueQueue.FlushInstructions(CFIDtoFlush);
            LSQ.FlushInstructions(CFIDtoFlush);

            if (exs.inputInstruction != null && exs.inputInstruction.getCFID() == CFIDtoFlush)
                exs.inputInstruction = null;

            if (mul1s.inputInstruction != null && mul1s.inputInstruction.getCFID() == CFIDtoFlush)
                mul1s.inputInstruction = null;

            if (mul2s.inputInstruction != null && mul2s.inputInstruction.getCFID() == CFIDtoFlush)
                mul2s.inputInstruction = null;

            CFIDQueue.removeFromDispatchedCFID(CFIDtoFlush);
            CFIDQueue.addToFreeCFID(CFIDtoFlush);
        }


    }

    public static void Display() {
        System.out.println("\nContents of Pipeline stages:");
        System.out.println("F stage : " + fs.getCurInstr() + " | " + fs.getCurInstrString());
        System.out.println("DRF stage : " + drfs.getCurInstr() + " | " + drfs.getCurInstrString());
        System.out.println("EX stage : " + exs.getCurInstr() + " | " + exs.getCurInstrString());
        System.out.println("MUL1 stage : " + mul1s.getCurInstr() + " | " + mul1s.getCurInstrString());
        System.out.println("MUL2 stage : " + mul2s.getCurInstr() + " | " + mul2s.getCurInstrString());
        System.out.println("MEM stage : " + mem3s.getCurInstr() + " | " + mem3s.getCurInstrString());

        System.out.println("----------------------------------");

        System.out.println("Rename Table:");
        System.out.println("RAT[" + String.valueOf(0) + "] : U" + UnifiedRegisterFile.rev_rename_table[0]);
        for (int i = 1; i < ProjectConsts.totalRegisters; i++)
        	if(UnifiedRegisterFile.rev_rename_table[i]!= 0)
        		System.out.println("RAT[" + String.valueOf(i) + "] : U" + UnifiedRegisterFile.rev_rename_table[i]);

        System.out.println("----------------------------------");

        System.out.println("Unified Registers:");
        System.out.println("Value           | Allocated    | Status");
        System.out.println("URF" + String.valueOf(0) + "  : " + String.valueOf(UnifiedRegisterFile.ReadFromRegister(0)) + "      | "
                + String.valueOf(UnifiedRegisterFile.GetAllocated(0)) + "      | " + String.valueOf(UnifiedRegisterFile.GetRegisterStatus(0)));
        for (int i = 1; i < (ProjectConsts.totalPhysicalRegisters); i++) {
        	if(UnifiedRegisterFile.GetRegisterStatus(i))
        		System.out.println("URF" + String.valueOf(i) + "  : " + String.valueOf(UnifiedRegisterFile.ReadFromRegister(i)) + "      | "
        				+ String.valueOf(UnifiedRegisterFile.GetAllocated(i)) + "      | " + String.valueOf(UnifiedRegisterFile.GetRegisterStatus(i)));
        }

        System.out.println("----------------------------------");

        System.out.println("Memory locations:");
        for (int i = 0; i < 100; i++) {
            System.out.print("Mem[" + String.valueOf(i) + "] = " + String.valueOf(DataMemory.readFromMemory(i*ProjectConsts.dataAddressLength)) + "\t");
            if (i%5 == 0 && i!=0)
                System.out.println("\n");
        }
        System.out.println("\n");

        System.out.println("----------------------------------");
    }

    public static InstructionInfo getLoadForwarded() {
        return loadForwarded;
    }

    public static void setLoadForwarded(InstructionInfo loadForwarded) {
        Pipeline.loadForwarded = loadForwarded;
    }

    public static int getInstructionsCompleted() {
        return instructionsCompleted;
    }

    public static void setInstructionsCompleted(int instructionsCompleted) {
        Pipeline.instructionsCompleted = instructionsCompleted;
    }

    public static void incrementInstructionsCompleted() {
        instructionsCompleted++;
    }
}