package com.cs520p2;

public class CodeLine {
    private int fileLineNumber;
    private int address; //Multiple of 4
    private String insString;

    CodeLine(int fileLineNumber, int address, String insString) {
        this.fileLineNumber = fileLineNumber;
        this.address = address;
        this.insString = insString;
    }

    public int getFileLineNumber() {
        return fileLineNumber;
    }

    public void setFileLineNumber(int fileLineNumber) {
        this.fileLineNumber = fileLineNumber;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getInsString() {
        return insString;
    }

    public void setInsString(String insString) {
        this.insString = insString;
    }

}
