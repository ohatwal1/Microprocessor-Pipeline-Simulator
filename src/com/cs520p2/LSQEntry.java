package com.cs520p2;

public class LSQEntry {
    // Generic fields
    private InstructionInfo ins;
    private ProjectConsts.MemType memType;

    // Target address
    private int address;
    private boolean addressReady;

    // Load specific fields
    private int destPhyRegAddr;

    // Store specific fields
    private boolean srcReady;
    private int srcPhyRegAddr;
    private int value;

    private int clockCycle;

    LSQEntry(InstructionInfo newIns, int newClockCycle) {

        ins = newIns;

        if (newIns.getOpCode() == ProjectConsts.I.LOAD) {
            memType = ProjectConsts.MemType.LOAD;

            // If the instruction is LOAD, set the destination physical register address
            destPhyRegAddr = newIns.getdRegAddr();
        }
        else if (newIns.getOpCode() == ProjectConsts.I.STORE) {
            memType = ProjectConsts.MemType.STORE;

            // If the instruction is STORE, set the source physical register address
            srcPhyRegAddr = newIns.getsReg1Addr();
            if (newIns.isSrc1Forwarded()) {
                // And also copy the value of this source if it is already produced.
                value = newIns.getsReg1Val();
                srcReady = true;
            }
        }

        address = -1;
        addressReady = false;

        clockCycle = newClockCycle;

    }

    public InstructionInfo getIns() {
        return ins;
    }

    public void setIns(InstructionInfo ins) {
        this.ins = ins;
    }

    public ProjectConsts.MemType getMemType() {
        return memType;
    }

    public void setMemType(ProjectConsts.MemType memType) {
        this.memType = memType;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public boolean isAddressReady() {
        return addressReady;
    }

    public void setAddressReady(boolean addressReady) {
        this.addressReady = addressReady;
    }

    public int getDestPhyRegAddr() {
        return destPhyRegAddr;
    }

    public void setDestPhyRegAddr(int destPhyRegAddr) {
        this.destPhyRegAddr = destPhyRegAddr;
    }

    public boolean isSrcReady() {
        return srcReady;
    }

    public void setSrcReady(boolean srcReady) {
        this.srcReady = srcReady;
    }

    public int getSrcPhyRegAddr() {
        return srcPhyRegAddr;
    }

    public void setSrcPhyRegAddr(int srcPhyRegAddr) {
        this.srcPhyRegAddr = srcPhyRegAddr;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getClockCycle() {
        return clockCycle;
    }

    public void setClockCycle(int clockCycle) {
        this.clockCycle = clockCycle;
    }

}
