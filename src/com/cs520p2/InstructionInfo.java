package com.cs520p2;

import com.cs520p2.ProjectConsts.*;

public class InstructionInfo {

    private int PC;
    private int instrSequenceNo;
    private String insString;
    private String renamedInsString;
    private I opCode;
    private int sReg1Addr, sReg1Val;
    private int sReg2Addr, sReg2Val;
    private int src1, src2;
    private int dRegAddr, dRegVal;
    private int intermResult;
    private int memAddr, memData;
    private int literal;
    //private int lsqIndex;
    //private int robIndex;
    private int dispatchedClockCycle;
    private int CFID;
    private boolean decoded; // TODO: I don't know if this will be required. just keeping it for now.
    private boolean literalPresent;
    private boolean isGonnaSetFlags;
    private boolean flagConsumer;
    private boolean registersFetched;
    private boolean src1Forwarded;
    private boolean src2Forwarded;
    private boolean flagsForwarded;
    private boolean forwardedZeroFlag;

    public InstructionInfo(String insString, int PC, int insSeqNo) {
        this.insString = insString;
        this.PC = PC;
        this.instrSequenceNo = insSeqNo;

        decoded = false;
        registersFetched = false;

        // Initialize the values to some initial state. -1 cannot be an address. Hence, initializing these values to -1.
        // We should check this register address and confirm that it is not -1 before trying to read the value of that
        // register.
        sReg1Addr = -1;
        sReg2Addr = -1;
        dRegAddr = -1;
        memAddr = -1;
        literal = -1;  // TODO: I know this is wrong. But keeping for now.
        intermResult = -1;  // TODO: I know this is wrong. But keeping for now.
        //lsqIndex = -1;
        //robIndex = -1;
        src1Forwarded = false;
        src2Forwarded = false;
        flagsForwarded = false;
        forwardedZeroFlag = false;
        literalPresent = false;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public String getInsString() {
        return insString;
    }

    public void setInsString(String insString) {
        this.insString = insString;
    }

    public I getOpCode() {
        return opCode;
    }

    public void setOpCode(I opCode) {
        this.opCode = opCode;
    }

    public int getsReg1Addr() {
        return sReg1Addr;
    }

    public void setsReg1Addr(int sReg1Addr) {
        this.sReg1Addr = sReg1Addr;
    }

    public int getsReg1Val() {
        return sReg1Val;
    }

    public void setsReg1Val(int sReg1Val) {
        if (!isSrc1Forwarded())
            this.sReg1Val = sReg1Val;
    }

    public int getsReg2Addr() {
        return sReg2Addr;
    }

    public void setsReg2Addr(int sReg2Addr) {
        this.sReg2Addr = sReg2Addr;
    }

    public int getsReg2Val() {
        return sReg2Val;
    }

    public void setsReg2Val(int sReg2Val) {
        if (!isSrc2Forwarded())
            this.sReg2Val = sReg2Val;
    }

    public int getdRegAddr() {
        return dRegAddr;
    }

    public void setdRegAddr(int dRegAddr) {
        this.dRegAddr = dRegAddr;
    }

    public int getdRegVal() {
        return dRegVal;
    }

    public void setdRegVal(int dRegVal) {
        this.dRegVal = dRegVal;
    }

    public int getMemAddr() {
        return memAddr;
    }

    public void setMemAddr(int memAddr) {
        this.memAddr = memAddr;
    }

    public int getMemData() {
        return memData;
    }

    public void setMemData(int memData) {
        this.memData = memData;
    }

    public int getLiteral() {
        return literal;
    }

    public void setLiteral(int literal) {
        this.literal = literal;
    }

    public boolean isDecoded() {
        return decoded;
    }

    public void setDecoded(boolean decoded) {
        this.decoded = decoded;
    }

    public int getIntermResult() {
        return intermResult;
    }

    public void setIntermResult(int intermResult) {
        this.intermResult = intermResult;
    }

    public int getSequenceNo() {
        return instrSequenceNo;
    }

    public void setSequenceNo(int instrSequenceNo) {
        this.instrSequenceNo = instrSequenceNo;
    }

    public void setIsGonnaSetFlags(boolean newValue) {
        this.isGonnaSetFlags = newValue;
    }

    public boolean getIsGonnaSetFlags() {
        return isGonnaSetFlags;
    }

    public boolean isFlagConsumer() {
        return flagConsumer;
    }

    public void setFlagConsumer(boolean flagConsumer) {
        this.flagConsumer = flagConsumer;
    }

    public boolean isRegistersFetched() {
        return registersFetched;
    }

    public void setRegistersFetched(boolean registersFetched) {
        this.registersFetched = registersFetched;
    }

    public boolean isSrc1Forwarded() {
        return src1Forwarded;
    }

    public void setSrc1Forwarded(boolean src1Forwarded) {
        this.src1Forwarded = src1Forwarded;
    }

    public boolean isSrc2Forwarded() {
        return src2Forwarded;
    }

    public void setSrc2Forwarded(boolean src2Forwarded) {
        this.src2Forwarded = src2Forwarded;
    }

    public boolean isFlagsForwarded() {
        return flagsForwarded;
    }

    public void setFlagsForwarded(boolean flagsForwarded) {
        this.flagsForwarded = flagsForwarded;
    }

    public boolean isForwardedZeroFlag() {
        return forwardedZeroFlag;
    }

    public void setForwardedZeroFlag(boolean forwardedZeroFlag) {
        this.forwardedZeroFlag = forwardedZeroFlag;
    }

    //public int getLsqIndex() {
    //    return lsqIndex;
    //}

    //public void setLsqIndex(int lsqIndex) {
    //    this.lsqIndex = lsqIndex;
    //}

    //public int getRobIndex() {
    //    return robIndex;
    //}

    //public void setRobIndex(int robIndex) {
    //    this.robIndex = robIndex;
    //}

    public int getDispatchedClockCycle() {
        return dispatchedClockCycle;
    }

    public void setDispatchedClockCycle(int dispatchedClockCycle) {
        this.dispatchedClockCycle = dispatchedClockCycle;
    }

    public int getCFID() {
        return CFID;
    }

    public void setCFID(int CFID) {
        this.CFID = CFID;
    }

    public boolean isLiteralPresent() {
        return literalPresent;
    }

    public void setLiteralPresent(boolean literalPresent) {
        this.literalPresent = literalPresent;
    }

    public int getSrc1() {
        return src1;
    }

    public void setSrc1(int src1) {
        this.src1 = src1;
    }

    public int getSrc2() {
        return src2;
    }

    public void setSrc2(int src2) {
        this.src2 = src2;
    }

    public String getRenamedInsString() {
        return renamedInsString;
    }

    public void setRenamedInsString(String renamedInsString) {
        this.renamedInsString = renamedInsString;
    }

    public void appendToRenamedInsString(String newPart) {
        this.renamedInsString += newPart;
    }
}
