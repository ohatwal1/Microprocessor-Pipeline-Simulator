package com.cs520p2;

import java.util.ArrayList;
import java.util.Iterator;

public class LSQ {

    private static ArrayList<LSQEntry> lsq = new ArrayList<>();

    public static void SetupLoadStoreQueue() {
        lsq = new ArrayList<>();
    }

    public static boolean isLSQFull() {
        return (lsq.size() >= ProjectConsts.totalLSQEntries);
    }

    public static int add(InstructionInfo newInstrn, int clockCycle) {

        LSQEntry newLSQEntry = new LSQEntry(newInstrn, clockCycle);

        lsq.add(newLSQEntry);

        return lsq.size()-1;
    }

    public static InstructionInfo getNextInstruction() {

        if (lsq.size() == 0)
            return null;

        LSQEntry earliestInstruction = lsq.get(0);

        // Return the first instruction if it has its address computed.

//        System.out.println("LSQ: Check if I can send instruction " + earliestInstruction.getIns().getInsString()
//                + " which has addr:" + String.valueOf(earliestInstruction.getAddress()) + " & " +
//                String.valueOf(earliestInstruction.isAddressReady()));

        // Address should be ready AND (Either instruction should be LOAD, OR, ((if it is STORE) source value should be available
        // AND instruction at head of ROB should be this STORE).
        if(earliestInstruction.isAddressReady() &&
                (earliestInstruction.getMemType() == ProjectConsts.MemType.LOAD ||
                        (earliestInstruction.isSrcReady() && earliestInstruction.getClockCycle() == ROB.GetDispatchedClockCycleOfHead()))) {

            // Copy the generated address into the InstructionInfo object to be sent to MEM stage.
            earliestInstruction.getIns().setIntermResult(earliestInstruction.getAddress());

            // If the instruction type is STORE, copy the value to be STOREd as well.
            // Also, mark the entry of this STORE at head of ROB as, ready to commit.
            if (earliestInstruction.getMemType() == ProjectConsts.MemType.STORE) {
                earliestInstruction.getIns().setsReg1Val(earliestInstruction.getValue());
                ROB.setStatus(earliestInstruction.getIns().getDispatchedClockCycle(), true);
            }

            lsq.remove(0);

            return earliestInstruction.getIns();
        }

        // Instruction at head of LSQ is not ready
        return null;
    }

    public static void GetForwardedData(int registerAddress, int data) {

        //System.out.println("IQ got forward for register " + String.valueOf(registerAddress) + " with data "
        //        + String.valueOf(data));

        for(LSQEntry instruction : lsq) {

            // Store the forwarded data only if the instruction is a STORE (Because only STORE has sources which can be forwarded),
            // AND that source of STORE instruction is not ready
            // AND the register address matches with the source of STORE.
            if ((instruction.getMemType() == ProjectConsts.MemType.STORE) &&
                    (!instruction.isSrcReady()) &&
                    (instruction.getSrcPhyRegAddr() == registerAddress)) {

                    instruction.getIns().setsReg1Val(data);
                    instruction.setValue(data);
                    instruction.getIns().setSrc1Forwarded(true);
                    instruction.setSrcReady(true);
            }
        }
    }

    private static int GetIndexByClockCycle(int clockCycle) {
        // Accepts a clock cycle and returns the index of the instruction which was dispatched in that clock cycle.

        for (int i = 0; i < lsq.size(); i++) {
            if (lsq.get(i).getClockCycle() == clockCycle)
                return i;
        }

        //System.out.println("Error! No instruction in LSQ for the given clock cycle " + String.valueOf(clockCycle) + " found!");
        return -1;
    }

    private static int GetDestIndexForBypasserLoad(LSQEntry bypasserLoad) {
        // Tries to find the smallest index position at the head of LSQ where a LOAD which is bypassing can be stored.
        int startIndex = 0;

        // Start with the head of LSQ, and go ahead until there are no ready-to-execute LOADs. Return the index immediately
        // after these ready-to-execute LOADs.
        while ((startIndex < lsq.size()) &&
                (lsq.get(startIndex) != bypasserLoad) &&
                (lsq.get(startIndex).getMemType() == ProjectConsts.MemType.LOAD) &&
                (lsq.get(startIndex).isAddressReady()))
            startIndex++;

        return startIndex;
    }

    // Called by INTFU with the calculated address.
    public static void GetForwardedAddress(int clockCycle, int address) {

        int lsqIndex = GetIndexByClockCycle(clockCycle);

        // Save the address received from INTFU in appropriate instruction.
        lsq.get(lsqIndex).setAddress(address);
        lsq.get(lsqIndex).setAddressReady(true);

        // LOAD Bypassing & LOAD Forwarding
        if (lsq.get(lsqIndex).getMemType() == ProjectConsts.MemType.LOAD) {

            LSQEntry bypasserLoad = lsq.get(lsqIndex);

            boolean canBypass = true;
            boolean storesFound = false;
            boolean forwardingPossible = false;
            int forwardFromIndex = -1;

            // For all instructions ahead of LOAD,
            for (int i = lsqIndex-1; i >= 0; i--) {

                // If that instruction is STORE,
                if ((lsq.get(i).getMemType() == ProjectConsts.MemType.STORE)) {

                    storesFound = true;

                    // Check for LOAD-Forwarding
                    // The STORE instruction should have its address ready, its address should match with the LOAD we're
                    // trying to forward, and its value to be STOREd should be available.
                    if (lsq.get(i).isAddressReady() && lsq.get(i).getAddress() == bypasserLoad.getAddress() &&
                            lsq.get(i).isSrcReady()) {
                        forwardingPossible = true;
                        forwardFromIndex = i;
                        break;
                    }

                    // Do not bypass if the STORE doesn't have address ready or the addresses are matching.
                    if (!lsq.get(i).isAddressReady() || lsq.get(i).getAddress() == bypasserLoad.getAddress()) {

                        canBypass = false;
                        break;
                    }
                }
            }

            if (forwardingPossible) {
                // LOAD-Forwarding is possible

                //System.out.println("LOAD Forwarding triggered.");

                // Copy the result from STORE which has matching address with ours.
                bypasserLoad.getIns().setIntermResult(lsq.get(forwardFromIndex).getValue());

                // Write this result in a physical register file
                UnifiedRegisterFile.WriteToRegister(bypasserLoad.getIns().getdRegAddr(), bypasserLoad.getIns().getIntermResult());
                UnifiedRegisterFile.SetRegisterStatus(bypasserLoad.getIns().getdRegAddr(), true);

                // Also update the ROB entry accordingly.
                ROB.setResult(bypasserLoad.getIns().getDispatchedClockCycle(), bypasserLoad.getIns().getIntermResult());
                ROB.setStatus(bypasserLoad.getIns().getDispatchedClockCycle(), true);

                // Make arrangements to forward the result we got to IQ, LSQ, DRF etc.
                Pipeline.setLoadForwarded(bypasserLoad.getIns());

                lsq.remove(bypasserLoad);
            }
            else if (storesFound && canBypass) {
                // LOAD bypassing is possible.

                //System.out.println("LOAD bypassing triggered.");

                // Get an index at the front of the LSQ, but behind ready-to-execute LOADs.
                int destIndex = GetDestIndexForBypasserLoad(bypasserLoad);

                lsq.remove(lsqIndex);
                lsq.add(destIndex, bypasserLoad);
            }
        }
    }

    public static void FlushInstructions(int CFIDtoFlush) {

        Iterator<LSQEntry> iterator = lsq.iterator();

        while(iterator.hasNext()) {

            LSQEntry instruction = iterator.next();

            if (instruction.getIns().getCFID() == CFIDtoFlush)
                iterator.remove();
        }
    }

    public static String printCurrentInstructions() {

           StringBuilder outputString = new StringBuilder("");

           if ( lsq.size() == 0 )
	    {
	       	 return " Empty";
	    }

           for (LSQEntry instruction : lsq) {
               outputString.append("\n* (I" + instruction.getIns().getSequenceNo() + ") " + instruction.getIns().getInsString());
           }

           return outputString.toString();
    }

}
