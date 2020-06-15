package com.cs520p2;

public class Register {

    private int value;
    private boolean valid;
    private boolean zFlag;

    public Register() {
        value = 0;
        valid = true;
        zFlag = false;
    }

    public Register(int value, boolean valid, boolean zFlag) {
        this.value = value;
        this.valid = valid;
        this.zFlag = zFlag;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean iszFlag() {
        return zFlag;
    }

    public void setzFlag(boolean zFlag) {
        this.zFlag = zFlag;
    }
}
