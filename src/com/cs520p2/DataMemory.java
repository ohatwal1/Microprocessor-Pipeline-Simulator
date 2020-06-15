package com.cs520p2;

public class DataMemory {

    private static int [] dataArray;

    public static void initialize() {
        dataArray = new int[ProjectConsts.dataTotalLocations];
    }

    public static void writeToMemory(int value, int address) {

        if (address < ProjectConsts.dataBaseAddress || address > (ProjectConsts.dataBaseAddress + ProjectConsts.dataTotalLocations)
                || address % ProjectConsts.dataAddressLength != 0) {
            System.out.println("Write to memory failed! Illegal address, " + address + " provided.");
            return;
            // TODO: Throw an exception in this case?
        }

        dataArray[address / ProjectConsts.dataAddressLength] = value;
    }

    public static int readFromMemory(int address) {

        if (address < ProjectConsts.dataBaseAddress || address > (ProjectConsts.dataBaseAddress + ProjectConsts.dataTotalLocations)
                || address % ProjectConsts.dataAddressLength != 0) {
            System.out.println("Read from memory failed! Illegal address, " + address + " provided.");
            return -1;
            // TODO: Throw an exception in this case?
        }

        return dataArray[address / ProjectConsts.dataAddressLength];
    }
}