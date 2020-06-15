package com.cs520p2;

import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) {

        int choice = 0;
        Scanner in = new Scanner(System.in);

        if (args.length != 1) {
            System.out.println("Please pass a file name as input.");
            System.exit(1);
        }

        File f = new File(args[0]);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("Error! File " + args[0] + " not found.");
            System.exit(1);
        }

        while (choice != 4) {
            System.out.println("\nMenu:");
            System.out.println("(input file: " + args[0] + ")");
            System.out.println("1. Initialize");
            System.out.println("2. Simulate");
            System.out.println("3. Display");
            System.out.println("4. Exit");
            System.out.println("Please enter your choice: ");

            choice = in.nextInt();

            switch (choice) {
                case 1:
                    RegisterFile.SetupRegisters();
                    UnifiedRegisterFile.SetupRegisters();
                    IssueQueue.SetupIssueQueue();
                    ROB.SetupROB();
                    LSQ.SetupLoadStoreQueue();
                    CodeMemory.readFromFile(args[0]);
                    DataMemory.initialize();
                    CFIDQueue.initialize();
                    Pipeline.Setup();
                    System.out.println("Initialization successful.");
                    break;

                case 2:
                    System.out.println("Enter the number of cycles to simulate: ");
                    int cycles = in.nextInt();
                    Pipeline.Simulate(cycles);
                    break;

                case 3:
                    Pipeline.Display();
                    break;

                case 4:
                    break;

                default:
                    System.out.println("Please enter proper input.");
            }
        }
    }
}