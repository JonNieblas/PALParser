import java.util.ArrayList;
import java.util.List;
/**
 * Class Opcode takes a line that has been verified to contain an opcode,
 * passes it to the appropriate opcode method, and verifies if the line
 * follows PAL syntax rule. If not, passes errors to Class ErrorHandler.
 * @author Jonathan Nieblas
 */
public class Opcode {
    /** Contains valid registers for sources/destinations. */
    private ArrayList<String> validRegisters = new ArrayList<>();
    /** Stores variables created by DEF opcode; Contains list of valid source/destination names. */
    private ArrayList<String> validVariables = new ArrayList<>();
    /** Stores every valid label that was branched to. Compared to original Label List for error 16.*/
    private ArrayList<String> encounteredLabels = new ArrayList<>();
    /** Split variable later used to split a line by each comma encountered. */
    private String[] wordSplitter;


    /**
     * Creates validRegisters ArrayList when created.
     */
    public Opcode (){
        for(int i = 0; i < 8; i++){
            validRegisters.add("R" + i);
        }
    }

    /**
     * Takes information from Class PALParser and searches for the correct
     * opcode handler.
     * @param opcode used to find correct opcode method
     * @param line to be checked for syntax errors
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param labelList containing valid labels
     * @param numOfErr contains each type of error encountered
     */
    public void OpcodeMethodHandler(String opcode, String line, List<String> linesToLog, int lineCount,
                                    ArrayList<String> labelList, ArrayList<Integer> numOfErr){
        String newLine = line.replace(opcode, "");//removes opcode from statement
        newLine = newLine.replace(" ", "");//removes spaces from statement

        switch(opcode){
            case "ADD":
            case "SUB":
            case "MUL":
            case "DIV": ASMDOpcode(newLine, linesToLog, lineCount, line, numOfErr);
                break;
            case "COPY": COPYOpcode(newLine, linesToLog, lineCount, line, numOfErr);
                break;
            case "MOVE": MOVEOpcode(newLine, linesToLog, lineCount, line, numOfErr);
                break;
            case "INC":
            case "DEC": IDOpcode(newLine, linesToLog, lineCount, line, numOfErr);
                break;
            case "BEQ":
            case "BGT": BEBGOpcode(newLine, linesToLog, lineCount, labelList, line, numOfErr);
                break;
            case "BR": BROpcode (newLine, linesToLog, lineCount, labelList, line, numOfErr);
                break;
            case "DEF": DEFOpcode(newLine, linesToLog, lineCount, line, numOfErr);
                break;
        }
    }

    /**
     * Handles a line with ADD/SUB/MUL/DIV opcode and checks
     * for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine original line -comments
     * @param numOfErr contains each type of error encountered
     */
    public void ASMDOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");

        for(String word : wordSplitter){
            if(wordCount < 4){
                RegisterChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "ASMD", originalLine, numOfErr);
    }

    /**
     * Handles a line with COPY opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine original line -comments
     * @param numOfErr contains each type of error encountered
     */
    public void COPYOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            RegisterChecker(err, word);
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "MC", originalLine, numOfErr);
    }

    /**
     * Handles a line with MOVE opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine original line- comments
     * @param numOfErr contains each type of error encountered
     */
    public void MOVEOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount == 0){
                ShouldBeInteger(err, word);
            } else if(wordCount > 0){
                RegisterChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "MC", originalLine, numOfErr);
    }

    /**
     * Handles a line with INC/DEC opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void IDOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            RegisterChecker(err, word);
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "ID", originalLine, numOfErr);
    }

    /**
     * Handles a line with BEQ/BGT opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param labelList contains pre-created labels
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void BEBGOpcode(String newLine, List<String> linesToLog, int lineCount, ArrayList<String> labelList,
                           String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        String label = LabelFinder(originalLine).trim();
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount < 2){
                RegisterChecker(err, word);
            }else if (wordCount == 2) {
                LabelChecker(err, label, labelList);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "BEBG", originalLine, numOfErr);
    }

    /**
     * Handles a line with BR opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param labelList contains pre-created labels
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void BROpcode(String newLine, List<String> linesToLog, int lineCount, ArrayList<String> labelList,
                         String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        String label = LabelFinder(originalLine).trim();
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount == 0){
                LabelChecker(err, label, labelList);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "BR", originalLine, numOfErr);
    }

    /**
     * Handles a line with DEF opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void DEFOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine, ArrayList<Integer> numOfErr){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount == 0){
                validVariables.add(word);
            }if(wordCount == 1){
                ShouldBeInteger(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "DEF", originalLine, numOfErr);
    }

    /**
     * Finds a label within a branch instruction's line.
     * @param originalLine contains a label to be extracted
     * @return label that was extracted
     */
    public String LabelFinder(String originalLine){
        String label;
        if (originalLine.contains("BEQ") || originalLine.contains("BGT")) {
            label = originalLine.substring(originalLine.lastIndexOf(','));
            label = label.replace(",","");
        }else{
            label = originalLine.replace("BR", " ");
        }
        return label;
    }

    /**
     * Takes info from an opcode handler, passes information to
     * WordsInLine(), then adds line + errors to .log file.
     * @param err reported to if any errors
     * @param linesToLog for lines & errors to be added to
     * @param wordCount num of split words in a line
     * @param lineCount current line's num in .pal
     * @param opcode type of opcode for WordsInLine()
     * @param line original line
     * @param numOfErr contains each type of error encountered
     */
    public void LogListAdder(ErrorHandler err, List<String> linesToLog, int wordCount, int lineCount,
                             String opcode, String line, ArrayList<Integer> numOfErr){
        WordsInLine(err, wordCount, opcode);
        linesToLog.add(lineCount + " " + line);
        err.ErrorsToLog(numOfErr);
    }

    /**
     * Checks that registers being used in Source/Destination sports are valid
     * @param err reported to if any errors
     * @param word valid/invalid register
     */
    public void RegisterChecker(ErrorHandler err, String word){
        if(!validRegisters.contains(word) && !validVariables.contains(word)){
            OperandStringOrInteger(err, word);
        }
    }

    /**
     * Checks that a branch references an existing label or a valid label.
     * @param err reported to if any errors
     * @param label from branch instruction
     * @param labelList contains pre-created labels
     */
    public void LabelChecker(ErrorHandler err, String label, ArrayList<String> labelList){
        if(validRegisters.contains(label) || validVariables.contains(label)){
            err.AddToErrorList(10);
            err.AddToProblemWordList(label);
        } else if(labelList.contains(label)){
            encounteredLabels.add(label);
        } else{
            err.AddToErrorList(6);
            err.AddToProblemWordList(label);
        }
    }

    /**
     * Checks to see if the incorrect operand is an immediate value or an ill-formed operand.
     * @param err reported to if any errors
     * @param word ill-formed operand/immediate value
     */
    public void OperandStringOrInteger(ErrorHandler err, String word){
        word = word.replace(",", "");
        if(word.matches("^-?\\d+$")){
            err.AddToErrorList(0);
            err.AddToProblemWordList(word);
        } else {
            err.AddToErrorList(1);
            err.AddToProblemWordList(word);
        }
    }

    /**
     * Checks that a string is an immediate value.
     * @param err reported to if any errors
     * @param word string to be checked
     */
    public void ShouldBeInteger(ErrorHandler err, String word){
        word = word.replace(",", "");
        if(!word.matches("^-?\\d+$")){
            err.AddToErrorList(7);
            err.AddToProblemWordList(word);
        }
    }

    /**
     * Checks the word count in a line based on opcode type.
     * Used to report if a statement has too many/too few operands.
     * @param err reported to if any errors
     * @param wordCount num of split words in a line
     * @param opcode type of opcode
     */
    public void WordsInLine(ErrorHandler err, int wordCount, String opcode){
        switch(opcode){
            case "ASMD":
            case "BEBG":
                WordCountError(3, wordCount, err);
                break;
            case "MC":
            case "DEF":
                WordCountError(2, wordCount, err);
                break;
            case "ID":
            case "BR":
                WordCountError(1, wordCount, err);
                break;
        }
    }

    /**
     * Takes information from WordsInLine() based on which opcode statement
     * is being tested and reports correct error.
     * @param i correct num of words in opcode's statement
     * @param wordCount num of split words from opcode's statement
     * @param err reported to if any errors
     */
    public void WordCountError(int i, int wordCount, ErrorHandler err){
        if(wordCount > i){
            err.AddToErrorList(2);
        } else if(wordCount < i){
            err.AddToErrorList(3); }
    }

    public ArrayList<String> getEncounteredLabels(){
        return this.encounteredLabels;
    }
}