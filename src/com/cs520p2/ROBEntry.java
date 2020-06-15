package com.cs520p2;

public class ROBEntry {

    private InstructionInfo ins;
    private boolean status;
    private int dest_arch_register;
    private int dest_phy_register;
    private int result;
    private int clockCycle;

    ROBEntry() {
        status = false;
        dest_phy_register = -1;
        dest_arch_register = -1;
        clockCycle = 0;
    }
    public InstructionInfo getIns() {
        return ins;
    }

    public void setIns(InstructionInfo ins) {
        this.ins = ins;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getDest_arch_register() {
        return dest_arch_register;
    }

    public void setDest_arch_register(int dest_arch_register) {
        this.dest_arch_register = dest_arch_register;
    }

    public int getDest_phy_register() {
        return dest_phy_register;
    }

    public void setDest_phy_register(int dest_phy_register) {
        this.dest_phy_register = dest_phy_register;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getClockCycle() {
        return clockCycle;
    }

    public void setClockCycle(int clockCycle) {
        this.clockCycle = clockCycle;
    }

}
