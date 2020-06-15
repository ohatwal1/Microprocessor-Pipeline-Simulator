package com.cs520p2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CodeMemory {

    private static ArrayList<CodeLine> codeLines = new ArrayList<>();

    public static CodeLine getInstruction(int offset) {
        if (offset < 0 || offset >= codeLines.size()) {
            return null;
        }

        return codeLines.get(offset);
    }

    public static boolean readFromFile(String fileName) {

        codeLines = new ArrayList<>();

        String aLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            int currentFileLineNumber = 0;
            int currentInstructionAddress = ProjectConsts.codeBaseAddress;

            while((aLine = br.readLine()) != null) {

                codeLines.add(new CodeLine(currentFileLineNumber, currentInstructionAddress, aLine));

                currentFileLineNumber++;
                currentInstructionAddress += ProjectConsts.codeInstructionLength;
            }
            br.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error: File with the name :" + fileName + " does not exist.");
            return false;

        } catch (IOException e) {
            System.out.println("Error: while reading the file. Please try again.");
            return false;
        }

        return true;
    }

    public static void printCodeLines() {
        if (codeLines == null || codeLines.isEmpty()) {
            System.out.println("No code was read.");
            return;
        }

        System.out.println("PRINTING THE READ CODE:");
        for(CodeLine codeLine : codeLines) {
            System.out.println(codeLine.getFileLineNumber() + " | " + codeLine.getAddress() + " | " + codeLine.getInsString());
        }

    }
}
