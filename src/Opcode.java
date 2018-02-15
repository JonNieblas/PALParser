import java.util.ArrayList;
import java.util.List;
/**
 * Class Opcode takes a line that has been verified to contain an opcode,
 * passes it to the appropriate opcode method, and verifies if the line
 * follows PAL syntax rule. If not, passes errors to Class ErrorHandler.
 * @author Jonathan Nieblas
 */
public class Opcode {
    /** Stores variables created by DEF opcode; Contains list of valid source/destination names. */
    private ArrayList<String> validVariables = new ArrayList<>();
    /** Stores every valid label that was branched to. Compared to original Label List for error 16.*/
    private ArrayList<String> encounteredLabels = new ArrayList<>();
    /** Split variable later used to split a line by each comma encountered. */
    private String[] wordSplitter;

    /**
     * Takes a line & the .pal file's information and passes a spaces-removed line
     * to the correct opcode handler.
     * @param opcode used to find correct opcode method
     * @param line to be checked for syntax errors
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param numOfErr contains each type of error encountered
     * @param ogLine original line containing labels & opcodes
     */
    public void OpcodeMethodHandler(String opcode, String line, List<String> linesToLog, int lineCount,
                                    ArrayList<Integer> numOfErr, String ogLine){
        String newLine = line.replace(opcode, "");//removes opcode from statement
        newLine = newLine.replace(" ", "");//removes spaces from statement
        switch(opcode){
            case "ADD":
            case "SUB":
            case "MUL":
            case "DIV": ASMDOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "COPY": COPYOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "MOVE": MOVEOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "INC":
            case "DEC": IDOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "BEQ":
            case "BGT": BEBGOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "BR": BROpcode (newLine, linesToLog, lineCount, ogLine, numOfErr);
                break;
            case "DEF": DEFOpcode(newLine, linesToLog, lineCount, ogLine, numOfErr);
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
                err.IsValidRegister(validVariables, word);
            }
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "ASMD", originalLine, numOfErr);
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
            err.IsValidRegister(validVariables, word);
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "MC", originalLine, numOfErr);
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
                err.IsValidInteger(word);
            } else if(wordCount > 0){
                err.IsValidRegister(validVariables, word);
            }
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "MC", originalLine, numOfErr);
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
            err.IsValidRegister(validVariables, word);
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "ID", originalLine, numOfErr);
    }

    /**
     * Handles a line with BEQ/BGT opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void BEBGOpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine,
                           ArrayList<Integer> numOfErr){
        int wordCount = 0;
        String label = LabelFinder(originalLine).trim();
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount < 2){
                err.IsValidRegister(validVariables, word);
            }else if (wordCount == 2) {
                encounteredLabels = err.IsValidLabel(validVariables, encounteredLabels, label);
            }
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "BEBG", originalLine, numOfErr);
    }

    /**
     * Handles a line with BR opcode and checks for syntax errors.
     * @param newLine receives syntax check
     * @param linesToLog for lines & errors to be added to
     * @param lineCount current line's num in .pal
     * @param originalLine contains comments, spaces, & opcode
     * @param numOfErr contains each type of error encountered
     */
    public void BROpcode(String newLine, List<String> linesToLog, int lineCount, String originalLine,
                         ArrayList<Integer> numOfErr){
        int wordCount = 0;
        String label = LabelFinder(originalLine).trim();
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = newLine.split(",");
        for(String word : wordSplitter){
            if(wordCount == 0){
                encounteredLabels = err.IsValidLabel(validVariables, encounteredLabels, label);
            }
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "BR", originalLine, numOfErr);
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
                err.IsValidInteger(word);
            }
            wordCount++;
        }
        LogListWriter(err, linesToLog, wordCount, lineCount, "DEF", originalLine, numOfErr);
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
    public void LogListWriter(ErrorHandler err, List<String> linesToLog, int wordCount, int lineCount,
                              String opcode, String line, ArrayList<Integer> numOfErr){
        err.WordsInLine(wordCount, opcode);
        linesToLog.add(lineCount + " " + line);
        err.ErrorStatementsToLogWriter(numOfErr);
    }

    /**
     * Returns the arrayList containing branch labels, encounteredLabels.
     * @return encounteredLabels
     */
    public ArrayList<String> getEncounteredLabels(){
        return this.encounteredLabels;
    }
}