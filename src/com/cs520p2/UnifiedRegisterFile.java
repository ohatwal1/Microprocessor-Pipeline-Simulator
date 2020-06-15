package com.cs520p2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

class PhysicalRegisterBackup {
    public int [] bak_rename_table;
    public boolean [] bak_rename_table_bit;
    public int bak_psw_rename_table;
    public boolean bak_psw_rename_table_bit;
    public ArrayList<UnifiedRegister> bak_physicalRegisters;

    PhysicalRegisterBackup(int [] bak_rename_table, boolean [] bak_rename_table_bit, int bak_psw_rename_table,
                           boolean bak_psw_rename_table_bit, ArrayList<UnifiedRegister> bak_physicalRegisters) {
        this.bak_rename_table = bak_rename_table;
        this.bak_rename_table_bit = bak_rename_table_bit;
        this.bak_psw_rename_table = bak_psw_rename_table;
        this.bak_psw_rename_table_bit = bak_psw_rename_table_bit;
        this.bak_physicalRegisters = bak_physicalRegisters;
    }
}

public class UnifiedRegisterFile {

    // Maps the architectural registers to their corresponding physical registers
    public static int [] rename_table = new int[ProjectConsts.totalRegisters];

    // Bit vector to indicate whether the latest value for an architectural register is in architectural register (false)
    // or in physical register (true)
    public static boolean [] rename_table_bit = new boolean[ProjectConsts.totalRegisters];

    // Maps the architectural registers to their corresponding physical registers
    public static int [] rev_rename_table = new int[ProjectConsts.totalRegisters];

    // Bit vector to indicate whether the latest value for an architectural register is in architectural register (false)
    // or in physical register (true)
    public static boolean [] rev_rename_table_bit = new boolean[ProjectConsts.totalRegisters];

    // Points to the physical register containing latest value of PSW flags
    public static int psw_rename_table;

    // Bit to indicate whether latest value of PSW is in PSW architectural register (false) or
    // in some physical register (true)
    public static boolean psw_rename_table_bit;

    // Stores the clock cycle when last flag producer instruction was dispatched.
    public static int last_flag_producer_clock_cycle = -1;

    private static ArrayList<UnifiedRegister> unifiedRegisters = new ArrayList<>();

    private static HashMap<Integer, PhysicalRegisterBackup> backups = new HashMap<>();

    public static void SetupRegisters() {
    	//TODO: Add another copy of registers for Architectural registers
        unifiedRegisters = new ArrayList<>();

        for (int i = 0; i < ProjectConsts.totalPhysicalRegisters; i++) {
            unifiedRegisters.add(i, new UnifiedRegister());
        }
    }

    public static boolean FreePhysicalRegisterAvailable() {

        for (int i = 0; i < ProjectConsts.totalPhysicalRegisters; i++) {
            if (! unifiedRegisters.get(i).isAllocated())
                return true;
        }
        return false;
    }

    public static int GetNewPhysicalRegister() {

        boolean found = false;
        int newIndex;

        for (newIndex = 0; newIndex < ProjectConsts.totalPhysicalRegisters; newIndex++) {
            if (! unifiedRegisters.get(newIndex).isAllocated()) {
                found = true;
                break;
            }
        }

        if (! found)
            return -1;

        unifiedRegisters.get(newIndex).setAllocated(true);
        unifiedRegisters.get(newIndex).setRenamed(false);
        unifiedRegisters.get(newIndex).setStatus(false);
        unifiedRegisters.get(newIndex).setzFlag(false);

        return newIndex;
    }

    public static void WriteToRegister(int index, int value) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error writing to physical register! Illegal index no. (" + index + ") given");
            return;
        }

        unifiedRegisters.get(index).setValue(value);
    }

    public static int ReadFromRegister(int index) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error reading from physical register! Illegal index no. (" + index + ") given");
            return -1;
            // TODO: This return is wrong!
        }

        return unifiedRegisters.get(index).getValue();
    }

    public static void SetRegisterStatus(int index, boolean status) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error setting physical register status! Illegal index no. (" + index + ") given");
            return;
        }

        unifiedRegisters.get(index).setStatus(status);
    }

    public static boolean GetRegisterStatus(int index) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error getting physical register status! Illegal index no. (" + index + ") given");
            return false;
            // TODO: This is wrong!
        }

        return unifiedRegisters.get(index).isStatus();
    }

    public static void SetAllocated(int index, boolean allocated) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error setting physical register allocation status! Illegal index no : " + index);
            return;
        }

        unifiedRegisters.get(index).setAllocated(allocated);
    }

    public static boolean GetAllocated(int index) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error getting physical register allocation status! Illegal index no : " + index);
            return false;
        }

        return unifiedRegisters.get(index).isAllocated();
    }

    public static void SetRenamed(int index, boolean renamed) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error setting physical register renamed status! Illegal index no : " + index);
            return;
        }

        unifiedRegisters.get(index).setRenamed(renamed);
    }

    public static boolean GetRenamed(int index) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error getting physical register renamed status! Illegal index no : " + index);
            return false;
        }

        return unifiedRegisters.get(index).isRenamed();
    }

    public static void SetZFlag(int index, boolean zFlag) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error setting physical register Z-Flag status! Illegal index no : " + index);
            return;
        }

        unifiedRegisters.get(index).setzFlag(zFlag);
    }

    public static boolean GetZFlag(int index) {
        if (index < 0 || index >= ProjectConsts.totalPhysicalRegisters) {
            System.out.println("Error setting physical register Z-Flag status! Illegal index no : " + index);
            return false;
        }

        return unifiedRegisters.get(index).iszFlag();
    }

    private static UnifiedRegister CopyPhysicalRegister(UnifiedRegister src) {
        UnifiedRegister dest = new UnifiedRegister();

        dest.setValue(src.getValue());
        dest.setAllocated(src.isAllocated());
        dest.setRenamed(src.isStatus());
        dest.setStatus(src.isStatus());
        dest.setzFlag(src.iszFlag());

        return dest;
    }

    private static ArrayList<UnifiedRegister> DeepCopyPhysicalRegisters(ArrayList<UnifiedRegister> phyRegs) {
        ArrayList<UnifiedRegister> clone = new ArrayList<>();
        for(UnifiedRegister p : phyRegs)
            clone.add(CopyPhysicalRegister(p));

        return clone;
    }

    public static void takeBackup(int PC) {
        PhysicalRegisterBackup newBackup = new PhysicalRegisterBackup(Arrays.copyOf(rename_table, rename_table.length),
                Arrays.copyOf(rename_table_bit, rename_table_bit.length),
                psw_rename_table, psw_rename_table_bit, DeepCopyPhysicalRegisters(unifiedRegisters));

        backups.put(PC, newBackup);
    }

    public static void restoreBackup(int PC) {
        PhysicalRegisterBackup backupToRestore = backups.get(PC);

        rename_table = backupToRestore.bak_rename_table;
        rename_table_bit = backupToRestore.bak_rename_table_bit;
        psw_rename_table = backupToRestore.bak_psw_rename_table;
        psw_rename_table_bit = backupToRestore.bak_psw_rename_table_bit;
        unifiedRegisters = backupToRestore.bak_physicalRegisters;
    }

    public static void printAll() {
        System.out.println(Arrays.toString(rename_table));
        System.out.println(Arrays.toString(rename_table_bit));
    }

    public static String renameInstruction(InstructionInfo inputInstruction) {

        switch (inputInstruction.getOpCode()) {

            case ADD:
            case SUB:
            case ADDL:
            case SUBL:
            case MUL:
            case AND:
            case OR:
            case EXOR:
                inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                if (inputInstruction.isLiteralPresent()) {
                    inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                }
                else {
                    if (inputInstruction.getsReg2Addr() != -1)
                        inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg2Addr()));
                    else
                        inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc2()) + ",");
                }

                break;

            case LOAD:
                inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case STORE:
                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                if (inputInstruction.getsReg2Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg2Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc2()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case MOVC:
                inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getdRegAddr()) + ",");
                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case BZ:
            case BNZ:
                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case HALT:
            case NOOP:
                break;

            case JUMP:
                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            case JAL:
                inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getdRegAddr()) + ",");

                if (inputInstruction.getsReg1Addr() != -1)
                    inputInstruction.appendToRenamedInsString("U" + String.valueOf(inputInstruction.getsReg1Addr()) + ",");
                else
                    inputInstruction.appendToRenamedInsString("R" + String.valueOf(inputInstruction.getSrc1()) + ",");

                inputInstruction.appendToRenamedInsString("#" + String.valueOf(inputInstruction.getLiteral()));
                break;

            default:
                break;
        }


        return inputInstruction.getRenamedInsString();
    }


    public static String printRenameTableEntries() {

	    StringBuilder outputString = new StringBuilder("");
	    boolean flag = true;
	    for (int index = 0; index < ProjectConsts.totalRegisters; index++)
	    {
		    if ( rename_table_bit[index] == true )
		    {
			    flag = false;
			    outputString.append("\n* R" + index  + " : U" + rename_table[index] );
		    }
	    }

	    if ( flag == true )
	    {
		    return " Empty";
	    }

	    return outputString.toString();
    }
    public static String printRevRenameTableEntries() {

	    StringBuilder outputString = new StringBuilder("");
	    boolean flag = true;
	    for (int index = 0; index < ProjectConsts.totalRegisters; index++)
	    {
		    if ( rev_rename_table_bit[index] == true )
		    {
			    flag = false;
			    outputString.append("\n* R" + index  + " : U" + rev_rename_table[index] );
		    }
	    }

	    if ( flag == true )
	    {
		    return " Empty";
	    }

	    return outputString.toString();
    }

}