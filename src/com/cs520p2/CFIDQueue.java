package com.cs520p2;

import java.util.ArrayList;

public class CFIDQueue {

    public static ArrayList<Integer> freeCFID;

    public static ArrayList<Integer> dispatchedCFID;

    public static int lastCFID = -1;

    public static void initialize() {
        freeCFID = new ArrayList<>();
        dispatchedCFID = new ArrayList<>();

        freeCFID.add(1);
        freeCFID.add(2);
        freeCFID.add(3);
        freeCFID.add(4);
        freeCFID.add(5);
        freeCFID.add(6);
        freeCFID.add(7);
        freeCFID.add(8);
    }

    public static boolean isFreeCFIDAvailable() {
        return (freeCFID.size() > 0);
    }

    public static int getFreeCFID() {
        int temp = freeCFID.get(0);
        freeCFID.remove(0);

        return temp;
    }

    public static void addToDispatchedCFID(int label) {
        dispatchedCFID.add(label);
    }

    public static void addToFreeCFID(int label) {
        freeCFID.add(label);
    }

    public static void removeFromDispatchedCFID(int label) {

        dispatchedCFID.remove((Integer)label);
    }

    public static int getIndexOfDispatchedCFID(int branchCFID) {

        return dispatchedCFID.indexOf(branchCFID);
    }
}