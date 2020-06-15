# Microprocessor-Pipeline-Simulator

Microprocessor instruction processing pipeline simulator (Developed as part of CS520) This program simulates execution of an out-of-order, multi-FU processor pipeline for the given stream of (a subset of RISC-like) assembly instructions. The simulator has following features:

- Load-Store instructions to interact with memory
- Load-Store instruction queue to serialize access to memory with support for LOAD-Forwarding and LOAD-Bypassing
- Reorder buffer (ROB) for inorder writes to registers
- Unified register file and register renaming. 
- Issue Queue for waiting instructions
- Data forwarding from FUs and memory unit to Issue Queue and LSQ
- Support for conditional and unconditional branch instructions
- Support for flag-dependent instructions

All the source code is placed under src/ directory.

To compile the java files, go to src/com/company and type: $ javac *.java

To run the program, run the following command from src/ directory: $ java com.company.Main <input_file> e.g. $ java com.company.Main input.txt
