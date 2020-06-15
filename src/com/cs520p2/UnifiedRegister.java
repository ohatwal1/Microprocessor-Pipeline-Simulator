package com.cs520p2;

public class UnifiedRegister {

    private int value;
    private boolean allocated;
    private boolean renamed;
    private boolean status;
    private boolean zFlag;

    public UnifiedRegister() {
        value = 0;
        allocated = false;
    }

    public UnifiedRegister(int value, boolean allocated) {
        this.value = value;
        this.allocated = allocated;
        this.renamed = false;
        this.status = false;
        this.zFlag = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public boolean isRenamed() {
        return renamed;
    }

    public void setRenamed(boolean renamed) {
        this.renamed = renamed;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean iszFlag() {
        return zFlag;
    }

    public void setzFlag(boolean zFlag) {
        this.zFlag = zFlag;
    }
}
