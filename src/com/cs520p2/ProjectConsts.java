package com.cs520p2;

public class ProjectConsts {

	public static final int codeBaseAddress = 4000;
	public static final int codeInstructionLength = 4;
	public static final int dataBaseAddress = 0;
	public static final int dataAddressLength = 4;
	public static final int dataTotalLocations = 4000;
	public static final int totalRegisters = 16;
	public static final int totalPhysicalRegisters = 24;
	public static final int totalIssueQueueEntries = 16;
	public static final int totalROBEntries = 32;
	public static final int totalLSQEntries = 20;

	public enum I {
		ADD, SUB, ADDL, SUBL, ADDC, MUL, LOAD, STORE, MOVC, BZ, BNZ, JUMP, HALT, AND, OR, EXOR, NOOP, JAL
	}

	public enum FU {
		MUL, INT
	}

	public enum MemType {
		LOAD, STORE
	}

	public static boolean generatesResult(InstructionInfo ins) {

		return (ins.getOpCode() == I.ADD || ins.getOpCode() == I.SUB
				|| ins.getOpCode() == I.ADDL || ins.getOpCode() == I.SUBL
				|| ins.getOpCode() == I.MUL || ins.getOpCode() == I.LOAD
				|| ins.getOpCode() == I.MOVC || ins.getOpCode() == I.AND
				|| ins.getOpCode() == I.OR || ins.getOpCode() == I.EXOR
				|| ins.getOpCode() == I.JAL);
	}

	public static boolean isMemoryInstruction(InstructionInfo ins) {

		return (ins.getOpCode() == I.LOAD || ins.getOpCode() == I.STORE);
	}

	public static boolean isBranchInstruction(InstructionInfo ins) {

		return (ins.getOpCode() == I.BZ || ins.getOpCode() == I.BNZ
				|| ins.getOpCode() == I.JUMP || ins.getOpCode() == I.JAL);
	}

	public static boolean isFlagConsumerInstruction(InstructionInfo ins) {

		return (ins.getOpCode() == I.BZ || ins.getOpCode() == I.BNZ);
	}

}