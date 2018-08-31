# PALParser
A PAL assembly language parser required for Principles of Pragramming Languages (CS3210). The parser takes a .pal file and investigates the code to make sure that it has legal statements. If it does not, it will print error lines to a logfile, along with the lines of code itself.

## What is PAL?

PAL is a mock assembly language designed to be overly simplistic in form.
We used PAL in CS3210 in order to create a parser for a basic language.
There are x different categories of opcodes available in PAL. I organized
the different codes into these categories in order to reduce the code base
and to prevent replication of similar code from occuring.


## Opcodes

#### SRT
SRT is the opcode that must be placed at the start of a piece of PAL code.
Only one SRT can be used for a block of code.

#### END
END is used to complete a block of code, beginning with SRT. All PAL code
must be placed within an SRT -- END container.

#### ADD, SUB, MUL, DIV (ASMD)
The main theme between these four opcodes is that they take two registers,
either add, subtract, multiply, or divide them, then place them into a 
third register. Therefore, opcodes within this category can be judged
similarly.

#### COPY
COPY takes the value within a register and copy it to another.
This opcode is unique compared to the others, so it must be judged
alone.

#### MOVE
MOVE takes an immediate value and places it within a register.
This opcode is unique compared to the others.

#### INC/DEC (ID)
INC/DEC are responsible for either incrementing or decrementing a 
value within a register by one.These two opcodes are very simple
and nearly the same, therefore they are judged based off of the same
criteria.

#### BEQ/BGT (BEBG)
BEQ/BGT takes two registers, compare them, and jump to a label
if they're both equivalent(BEQ) or if the first register is greater
than the other (BGT). They are judged similarly due to the fact that
they contain similar formats.

#### BR
BR jumps to a specified label. It is the only opcode containing a 
single component, explaining its uniqueness to the other opcodes.

#### DEF
DEF assignes a memory address to a label and must be declared directly after
the SRT opcode.


## Directory Structure

### src/
This is where the five pieces of the parser are located.  

#### PALDriver.java
The main driver of the program, containing the main method. Additional
.pal files, as well as PALParser objects for the files are created here.

#### Opcode.java
Opcode.java contains methods correlating to each opcode. Each method
will check to make sure that the opcode it governs is used correctly 
in .pal programs

#### PALParser.java
PALParser.java contains methods that handle the .pal programs line by 
line, passing them to the correct opcode handlers.

#### ErrorHandler.java
ErrorHandler.java checks each individual line for specific errors 
relating to each individual group of opcodes.

#### LogWriter.java
LogWriter.java takes the List containing the original lines from 
the .pal file, along with the additional error code lines, and 
prints them to a .log file named after the original .pal file. 
LogWriter also creates an end-of-log summary, including error 
types found and total number of errors.

### pal/
This contains five PAL programs for testing purposes. Each one 
is ment to test different opcodes, as well as different structures
of PAL code.

### logs/
This contains logs for each individual PAL program found in pal/. 
The names of the log files are one to one with programs found in
pal/. The logs contain the original code, as well as specific
errors and warnings found by the program.
