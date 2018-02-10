/*
 * Opcode contains all methods dealing with opcodes, as well as the construction of the opList arrayList
 */
//****CONSIDER: moving some checkers to the ErrorHandler class. This one is getting busy
import java.util.ArrayList;
import java.util.List;

public class Opcode {

    private ArrayList<String> validSources = new ArrayList<String>();//Registers with Commas
    private ArrayList<String> validDestinations = new ArrayList<String>();//Registers without commas
    private String[] wordSplitter;

    /*
     * creates arrays when new Opcode obj is created
     */
    public Opcode (){
        RegisterListCreator();
    }

    /*
     * OpcodeHandler will take the opcode and pass the line it was contained in to
     * the correct method, along with the linesToLog list.
     */
    public void OpcodeHandler(String opcode, String line, List<String> linesToLog, int lineCount,
                              ArrayList<String> labelList, String originalLine){
        if(opcode.equals("ADD") || opcode.equals("SUB") || opcode.equals("MUL") || opcode.equals("DIV")){
            ASMDOpcode(line, linesToLog, lineCount, originalLine);
        } else if(opcode.equals("COPY")){
            COPYOpcode(line, linesToLog, lineCount, originalLine);
        } else if(opcode.equals("MOVE")){
            MOVEOpcode(line, linesToLog, lineCount, originalLine);
        } else if(opcode.equals("INC") || opcode.equals("DEC")){
            IDOpcode(line, linesToLog, lineCount, originalLine);
        } else if(opcode.equals("BEQ") || opcode.equals("BGT")){
            BEBGOpcode(line, linesToLog, lineCount, labelList, originalLine);
        } else if(opcode.equals("BR")){
            BROpcode (line, linesToLog, lineCount, labelList, originalLine);
        }
    }

    /*
     * Observes an ADD/SUB/MUL/DIV line to make sure there are no errors.
     * Reports errors to ErrorHandler.
     */
    public void ASMDOpcode(String line, List<String> linesToLog, int lineCount, String originalLine){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");

        for (String word : wordSplitter){
            if(wordCount > 0 && wordCount < 3){
                SourceChecker(err, word);
            } else if(wordCount == 3){
                DestinationChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "ASMD", originalLine);
    }

    /*
     * Observes a COPY line to make sure there are no errors.
     * Reports errors to ErrorHandler.
     */
    public void COPYOpcode(String line, List<String> linesToLog, int lineCount, String originalLine){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if(wordCount == 1){
                SourceChecker(err, word);
            } else if(wordCount == 2){
                DestinationChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "MC", originalLine);
    }

    /*
     * Observes a MOVE line to make sure there are no errors.
     * Reports errors to ErrorHandler.
     *
     */
    public void MOVEOpcode(String line, List<String> linesToLog, int lineCount, String originalLine){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if(wordCount == 1){
                ShouldBeInteger(err, word);
            } else if(wordCount == 2){
                DestinationChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "MC", originalLine);
    }

    /*
     * Checks for INC/DEC line to make sure there are no errors.
     * Uses DestinationChecker instead of SourceChecker bc there is only one operand in this opcode.
     * Reports errors to ErrorHandler.
     */
    public void IDOpcode(String line, List<String> linesToLog, int lineCount, String originalLine){
        int wordCount = 0;
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if(wordCount == 1){
                DestinationChecker(err, word);
            }
            wordCount++;
        }
        LogListAdder(err, linesToLog, wordCount, lineCount, "ID", originalLine);
    }

    /*
     * Observes BEQ/BGT lines to make sure they match syntax.
     * Reports errors if not.
     */
    public void BEBGOpcode(String line, List<String> linesToLog, int lineCount, ArrayList<String> labelList,
                           String originalLine){
        int wordCount = 0;
        String placeHolder = "";
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if(wordCount == 0){
                wordCount++;
            } else if(wordCount > 0 && wordCount < 3){
                SourceChecker(err, word);
                wordCount++;
            }else if(wordCount > 2){
                if(!validSources.contains(word) || !validDestinations.contains(word)) {
                    placeHolder = placeHolder + word + " ";
                }else{
                    //is an operand
                    wordCount++;
                }
            }
        }
        wordCount = LabelChecker(err, placeHolder, labelList, wordCount);
        LogListAdder(err, linesToLog, wordCount, lineCount, "BEBG", originalLine);
    }

    /*
     * Observes BR line to make sure it matches syntax.
     * Reports errors if not.
     */
    public void BROpcode(String line, List<String> linesToLog, int lineCount, ArrayList<String> labelList,
                         String originalLine){
        int wordCount = 0;
        String placeHolder = "";
        ErrorHandler err = new ErrorHandler(linesToLog);
        wordSplitter = line.split(" ");
        for(String word : wordSplitter){
            if(wordCount == 0){
                wordCount++;
            } else if (wordCount > 0){
                if(!validSources.contains(word) || !validDestinations.contains(word)){
                    placeHolder = placeHolder + word + " ";
                }else{
                    //is an operand
                    wordCount++;
                }
            }
        }
        wordCount = LabelChecker(err, placeHolder, labelList, wordCount);
        LogListAdder(err, linesToLog, wordCount, lineCount, "BR", originalLine);
    }

    /*
     * Creates an ArrayList of valid registers for both the source & destination registers
     */
    public void RegisterListCreator(){
        for(int i = 0; i < 8; i++){
            validSources.add("R" + i + ",");
            validDestinations.add("R" + i);
        }
    }

    /*
     * Adds correct information to linesToLog ArrayList, as well as error ArrayList
     */
    public void LogListAdder(ErrorHandler err, List<String> linesToLog, int wordCount, int lineCount, String opcode, String originalLine){
        WordsInLine(err, wordCount, opcode);
        linesToLog.add(lineCount + " " + originalLine);
        err.ErrorsToLog();
    }

    /*
     * Checks that each source is of the correct type
     * FIX: If one of the sources doesn't have a comma, create a different type of error code for it
     */
    public void SourceChecker(ErrorHandler err, String word){
        if(!validSources.contains(word)){
            word = word.replace(",", "");
            OperandStringOrInteger(err, word);
        }
    }

    /*
     * Checks that each destination is of the correct type.
     */
    public void DestinationChecker(ErrorHandler err, String word){
        if(!validDestinations.contains(word) && !validSources.contains(word)) {
            word = word.replace(",", "");
            OperandStringOrInteger(err, word);
        }
    }

    /*
     * Checks that Branch is going to existing label.
     */
    public int LabelChecker(ErrorHandler err, String word, ArrayList<String> labelList, int wordCount){
        word = word.trim();
        if(!labelList.contains(word)){
            wordCount++;
            err.AddToErrorList(6);
            err.AddToProblemWordList(word);
        }else if(labelList.contains(word)){
            wordCount++;
        }
        return wordCount;
    }

    /*
     * Checks to see if the incorrect operand is an immediate value or an ill-formed operand.
     * Second comparison in if statement catches word with comma (EX: R1,; 22,; wow,;
     */
    public void OperandStringOrInteger(ErrorHandler err, String word){
        word = word.replace(",", "");
        if(word.matches("^-?\\d+$")){
            err.AddToErrorList(0);//error for immediate value
            err.AddToProblemWordList(word);
        } else {
            err.AddToErrorList(1);//error for ill-formed operand
            err.AddToProblemWordList(word);
        }
    }

    /*
     * Checks that an string is an immediate value.
     * If not, it informs the ErrorHandler.
     */
    public void ShouldBeInteger(ErrorHandler err, String word){
        word = word.replace(",", "");
        if(!word.matches("^-?\\d+$")){
            err.AddToErrorList(7);//error for immediate value
            err.AddToProblemWordList(word);
        }
    }

    /*
     * If there are more or less words in a line then there should be,
     * this will alert the ErrorHandler.
     */
    public void WordsInLine(ErrorHandler err, int wordCount, String opcode){
        if(opcode.equals("ASMD") || opcode.equals("BEBG")){
            if (wordCount > 4) {
                err.AddToErrorList(2);
            } else if (wordCount < 4) {
                err.AddToErrorList(3);
            }
        } else if(opcode.equals("MC")){
            if (wordCount > 3) {
                err.AddToErrorList(2);
            } else if (wordCount < 3) {
                err.AddToErrorList(3);
            }
        } else if(opcode.equals("ID") || opcode.equals("BR")){
            if(wordCount > 2){
                err.AddToErrorList(2);
            } else if(wordCount < 2){
                err.AddToErrorList(3);
            }
        }
    }
}