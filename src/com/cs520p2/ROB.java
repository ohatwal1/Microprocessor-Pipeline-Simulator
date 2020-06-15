package com.cs520p2;

import java.util.ArrayList;

public class ROB {

    private static ArrayList<ROBEntry> rob = new ArrayList<>();

    private static InstructionInfo firstCommittedInstr;
    private static InstructionInfo secondCommittedInstr;

        public static void SetupROB() {
            rob = new ArrayList<>();
        }

        public static boolean isROBFull() {
            return (rob.size() >= ProjectConsts.totalROBEntries);
        }

        public static int add(InstructionInfo newInstrn, int dest_arch_register, int dest_phy_register, int clockCycle) {

            ROBEntry newROBEntry = new ROBEntry();
            newROBEntry.setDest_arch_register(dest_arch_register);
            newROBEntry.setDest_phy_register(dest_phy_register);

            // Set the actual instruction to one that was passed
            newROBEntry.setIns(newInstrn);

            newROBEntry.setClockCycle(clockCycle);

            rob.add(newROBEntry);

            return rob.size()-1;
        }

        public static void commit(int commitNo, MEM3Stage mem3s) {

            //firstCommittedInstr = null;
            //secondCommittedInstr = null;

            if (rob.size() == 0)
                return;
            else if (! rob.get(0).isStatus()) {
                //System.out.println("Instruction at head of ROB not ready.");
                return;
            }
            // TODO: Last minute change!
            else if ((rob.get(0).getIns().getOpCode() == ProjectConsts.I.HALT) && (mem3s.isStalled() || mem3s.inputInstruction != null)) {
                return;
            }
            else {
                ROBEntry head = rob.get(0);

                // Check if the instruction produces a result
                if (ProjectConsts.generatesResult(head.getIns())) {
                    // The result is to be committed to architectural registers.

                    // Write result to architectural register
                    RegisterFile.WriteToRegister(head.getDest_arch_register(), head.getResult());
                    RegisterFile.SetRegisterZFlag(head.getDest_arch_register(),
                            UnifiedRegisterFile.GetZFlag(head.getIns().getdRegAddr()));

                    // Set the rename table entry to indicate that result is committed to architectural register
                    // Only if the physical register is the most recent instance of architectural register.
                    if (UnifiedRegisterFile.rename_table[head.getDest_arch_register()] == head.getDest_phy_register()) {
                    	UnifiedRegisterFile.rev_rename_table_bit[head.getDest_arch_register()] = true;
                        UnifiedRegisterFile.rev_rename_table[head.getDest_arch_register()] = UnifiedRegisterFile.rename_table[head.getDest_arch_register()];
                        UnifiedRegisterFile.rename_table[head.getDest_arch_register()] = -1;
                        UnifiedRegisterFile.rename_table_bit[head.getDest_arch_register()] = false;
                    }

                    if (head.getClockCycle() == UnifiedRegisterFile.last_flag_producer_clock_cycle) {
                        // This was the last instruction that produced flags. Time to store the flags in arch registers.
                        UnifiedRegisterFile.psw_rename_table_bit = false;
                        UnifiedRegisterFile.psw_rename_table = head.getDest_arch_register();
                    }

                    // Free physical registers if necessary.
                    //UnifiedRegisterFile.SetAllocated(head.getDest_phy_register(), false);
                    //UnifiedRegisterFile.SetRegisterStatus(head.getDest_phy_register(), false);

                }

                // To keep track of completed instructions for calculating CPI.
                Pipeline.incrementInstructionsCompleted();


                if (commitNo == 1) {
                    // This was the first instruction commitment. Try committing the following instruction too.
                    firstCommittedInstr = head.getIns();
                    rob.remove(head);

                    commit(2, mem3s);
                }
                else {
                    // This was the second instruction commitment.
                    secondCommittedInstr = head.getIns();
                    rob.remove(head);
                }
            }
        }

        private static int GetInstructionIndexByClockCycle(int clockCycle) {

            for (int i = 0; i < rob.size(); i++) {
                if (rob.get(i).getClockCycle() == clockCycle)
                    return i;
            }

            System.out.println("Error! No instruction in ROB for the given clock cycle " + String.valueOf(clockCycle) + " found!");
            return -1;

        }

        public static int GetDispatchedClockCycleOfHead() {
            if (rob.size() == 0)
                return -1;
            return rob.get(0).getClockCycle();
        }

        public static void setStatus(int clockCycle, boolean status) {
            rob.get(GetInstructionIndexByClockCycle(clockCycle)).setStatus(status);
        }

        public static boolean getStatus(int clockCycle) {
            return rob.get(GetInstructionIndexByClockCycle(clockCycle)).isStatus();
        }

        public static void setResult(int clockCycle, int result) {
            rob.get(GetInstructionIndexByClockCycle(clockCycle)).setResult(result);
        }

        public static int getResult(int clockCycle) {
            return rob.get(GetInstructionIndexByClockCycle(clockCycle)).getResult();
        }

        public static void setClockCycle(int index, int newClockCycle) {
            rob.get(index).setClockCycle(newClockCycle);
        }

        public static int getClockCycle(int index) {
            return rob.get(index).getClockCycle();
        }

        public static String printCurrentInstructions() {

               StringBuilder outputString = new StringBuilder("");

               if ( rob.size() == 0 )
	        {
	       	 return " Empty";
	        }
               for (ROBEntry instruction : rob) {
                   outputString.append("\n* (I" + instruction.getIns().getSequenceNo() + ") " + instruction.getIns().getInsString());
               }

               return outputString.toString();
        }

        public static void FlushInstructions(int branchClockCycle) {

            int startIndex = GetInstructionIndexByClockCycle(branchClockCycle);
            startIndex += 1;
            int remainingInstructions = rob.size() - startIndex;

            for (int i=0;i < remainingInstructions; i++) {
                if (ProjectConsts.generatesResult(rob.get(startIndex).getIns())) {

                    if (UnifiedRegisterFile.rename_table[rob.get(startIndex).getDest_arch_register()] == rob.get(startIndex).getDest_phy_register()) {
                        UnifiedRegisterFile.rename_table[rob.get(startIndex).getDest_arch_register()] = -1;
                        UnifiedRegisterFile.rename_table_bit[rob.get(startIndex).getDest_arch_register()] = false;
                    }

                    UnifiedRegisterFile.SetAllocated(rob.get(startIndex).getDest_phy_register(), false);
                    UnifiedRegisterFile.SetRegisterStatus(rob.get(startIndex).getDest_phy_register(), false);
                }

                rob.remove(startIndex);
            }
        }

        public static void RemoveEntry(int dispatchedClockCycle) {

            int victimEntryIndex = GetInstructionIndexByClockCycle(dispatchedClockCycle);

            rob.remove(victimEntryIndex);
        }

        public static void printCommittedInstructions() {

            if (firstCommittedInstr == null && secondCommittedInstr == null) {
            }
            else {

                System.out.println("Commit:");

                if (firstCommittedInstr != null) {
                    System.out.println("* (I" + String.valueOf(firstCommittedInstr.getSequenceNo()) + ") " + firstCommittedInstr.getInsString());
                    firstCommittedInstr = null;
                }

                if (secondCommittedInstr != null) {
                    System.out.println("* (I" + String.valueOf(secondCommittedInstr.getSequenceNo()) + ") " + secondCommittedInstr.getInsString());
                    secondCommittedInstr = null;
                }

                System.out.print("\n");
            }
        }

    }
