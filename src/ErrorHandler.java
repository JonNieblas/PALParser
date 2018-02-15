import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Class ErrorHandler takes different error numbers and adds
 * the correct error messages to the .log file.
 * Errors are reported here & handled here on a line-by-line basis.
 * @author Jonathan Nieblas
 */
public class ErrorHandler {
    /** Contains error num types reported to be found in a line. */
    private ArrayList<Integer> errorList = new ArrayList<>();
    /** Contains words that trigger an error in a line. */
    private ArrayList<String> problemWordList = new ArrayList<>();
    /** Contains error messages to be reported to .log file.*/
    private List<String> toLogList;
    /** Contains encountered labels from .pal file. */
    private ArrayList<String> encounteredLabels = new ArrayList<>();
    /** Contains valid registers. */
    private ArrayList<String> validRegisters = new ArrayList<>();
    /** Contains valid variables from DEF opcodes. */
    private ArrayList<String> validVars = new ArrayList<>();

    /**
     * Takes a linesToLog arrayList for adding errors.
     * @param list lines to be added to .log file
     */
    public ErrorHandler(List<String> list){
        this.toLogList = list;
        for(int i = 0; i < 8; i++){
            validRegisters.add("R" + i);
        }
    }

    /**
     * Allows other Class methods to add errors to errorList.
     * @param errID error identifier number
     */
    public void AddToErrorList(int errID){
        errorList.add(errID);
    }

    /**
     * Allows other Class methods to add problem words to
     * problemWordList.
     * @param word problem word
     */
    public void AddToProblemWordList(String word){
        problemWordList.add(word);
    }

    /**
     * Points to correct error statement for .log file.
     * @param errNum triggered error number
     * @param word problem word
     * @return error statement for .log file
     */
    public String Errors(int errNum, String word){
        String error = null;
        if(!(word == null)) {
            word = word.trim();
        }
        switch (errNum){
            case 0: error = " ** Wrong Operand Type: Immediate value '"  + word + "' where register was expected.";
                    break;
            case 1: error = " ** Ill-Formed Operand: '" + word + "' is not a valid operand type. Use R0-R7 or a defined variable.";
                    break;
            case 2: error = " ** Too Many Operands: You have exceeded the valid number of operands for this opcode.";
                    break;
            case 3: error = " ** Too Few Operands: You have not met the valid number of operands for this opcode.";
                    break;
            case 4: error = " ** Ill-Formed Label: '" + word + "' exceeds 15 characters or colon is not at the end of the label.";
                    break;
            case 5: error = " ** Invalid Opcode: '" + word + "' is not a valid opcode. Please review valid opcodes "
                            + "for more details.";
                    break;
            case 6: error = " ** WARNING - Branches to Non-Existent Label: Labels '" + word + "' don't exist in the program.";
                    break;
            case 7: error = " ** Wrong Operand Type: Value '" + word + "' where immediate value was expected.";
                    break;
            case 8: error = " ** Ill-Formed END Opcode: '" + word + "' is not a valid opcode to end a program with. Try 'END' instead.";
                    break;
            case 9: error = " ** Ill-Formed Start Opcode: '" + word + "' is not a valid opcode to start a program with. Try 'SRT' instead.";
                    break;
            case 10: error = " ** Missing Label in Branch: '" + word + "' is not a label. Please replace with a valid label.";
                    break;
            case 11: error = " ** Misplaced SRT: Can't start a new program until this one has ended";
                    break;
            case 12: error = " ** Misplaced END: SRT hasn't be instantiated yet.";
                    break;
            case 13: error = " ** WARNING - Line(s) Found Outside of Program: \n " + word;
                    break;
            case 14: error = " ** Program Exit Not Detected: All PAL programs must conclude with END opcode.";
                    break;
            case 15: error = " ** Invalid DEF: All DEFs must be declared after SRT and before any other executable opcodes.";
                    break;
            case 16: error = " ** WARNING - Label(s) Not Used: '" + word + "' was/were unused in the program.";
                    break;
        }
        return error;
    }

    /**
     * Handles an Ill-Formed END/SRT opcode error.
     * @param line containing END/SRT error
     * @param n counter signaling 1 = SRT or 0 = END
     * @param lineCount current line's num in .pal
     * @param linesToLog for lines & errors to be added to
     * @param numOfErr contains each type of error encountered
     * @param problemWord word to be used in error message
     */
    public void IncorrectENDOrSRTHandler(String line, int n, int lineCount, List<String> linesToLog,
                                         ArrayList<Integer> numOfErr, String problemWord){
        if (n == 0){
            ErrorStatementPreparer(8, problemWord, linesToLog, lineCount, line, numOfErr, null);
        }else if(n == 1){
            ErrorStatementPreparer(9, problemWord, linesToLog, lineCount, line, numOfErr, null);
        }
    }

    /**
     * Checks to see if the incorrect operand is an immediate value or an ill-formed operand.
     * @param word ill-formed operand/immediate value
     */
    public void IncorrectOperandType(String word){
        word = word.replace(",", "");
        if(word.matches("^-?\\d+$")){
            AddToErrorList(0);
            AddToProblemWordList(word);
        } else {
            AddToErrorList(1);
            AddToProblemWordList(word);
        }
    }

    /**
     * Checks that a string is an immediate value.
     * @param word string to be checked
     */
    public void IsValidInteger(String word){
        word = word.replace(",", "");
        if(!word.matches("^-?\\d+$")){
            AddToErrorList(7);
            AddToProblemWordList(word);
        }
    }

    /**
     * Checks that registers being used in Source/Destination sports are valid
     * @param validVariables list of valid variables from DEF
     * @param word valid/invalid register
     */
    public void IsValidRegister(ArrayList<String> validVariables, String word){
        validVars = validVariables;
        if(!validRegisters.contains(word) && !validVariables.contains(word)){
            IncorrectOperandType(word);
        }
    }

    /**
     * Checks that a branch references an existing label or a valid label.
     * @param validVariables contains valid variables from DEF
     * @param encounteredLabels stores labels that program has encountered
     * @param label from branch instruction
     * @return encounteredLabels updated list
     */
    public ArrayList<String> IsValidLabel(ArrayList<String> validVariables, ArrayList<String> encounteredLabels, String label){
        if(validRegisters.contains(label) || validVariables.contains(label)){
            AddToErrorList(10);
            AddToProblemWordList(label);
        } else{
            encounteredLabels.add(label);
        }
        return encounteredLabels;
    }

    /**
     * Passes correct information of label error issue to
     * LabelErrorHandler().
     * @param labelList list of unused labels or non-existant labels
     * @param numberOfErrors contains frequency of each error
     * @param opcode obj containing encounteredLabels
     * @param erri num of error counter
     * @param i num of error
     * @return erri updated error counter
     */
    public int LabelErrorPasser(List<String> labelList, ArrayList<Integer> numberOfErrors, Opcode opcode, int erri, int i){
        labelList = ValidLabelsVsInvalidLabels(opcode, labelList);
        if(i == 16) {
            erri = LabelErrorHandler(labelList, numberOfErrors, i, erri);//check for any unused labels
        } else if(i == 6) {
            erri = LabelErrorHandler(encounteredLabels, numberOfErrors, i, erri);//check for any unused labels
        }
        return erri;
    }

    /**
     * Compares valid labels to encountered labels, finding which valid labels were
     * used and which encountered labels were non-existent.
     * @param opcode obj containing encounteredLabels list
     * @param labelList contains list of valid labels
     * @return labelList list of unused valid labels
     */
    public List<String> ValidLabelsVsInvalidLabels(Opcode opcode, List<String> labelList){
        ArrayList<String> labelsFound = new ArrayList<>();
        encounteredLabels = opcode.getEncounteredLabels();

        labelsFound.addAll(encounteredLabels);
        encounteredLabels.removeAll(labelList);//labels that don't exist
        labelList.removeAll(labelsFound);//labels we didn't use
        return labelList;
    }

    /**
     * Takes a list containing labels and checks if they were unused (in the case of labelList)
     * or if they don't exist, yet were inside of a branch instruction.
     * @param list labelList or list of non-existent labels
     * @param numOfErr contains each type of error encountered
     * @param errNum error number
     * @param erri error counter
     * @return erri updated error counter
     */
    public int LabelErrorHandler(List<String> list, ArrayList<Integer> numOfErr, int errNum, int erri){
        String loopWord = "";
        String labelsNotFound = "";
        if(!list.isEmpty() ){
            for(String label : list){
                if(!(loopWord.equals(label))) {
                    labelsNotFound = labelsNotFound + label + ": ";
                    erri++;
                }
                loopWord = label;
            }
            AddToErrorList(errNum);
            AddToProblemWordList(labelsNotFound);
            ErrorStatementsToLogWriter(numOfErr);
        }
        if(erri > 0) {
            return erri - 1;//because of AddToErrorList
        } else{
            return erri;
        }
    }

    /**
     * Writes an error statement to .log containing all lines caught outside of SRT--END.
     * Statement is written to the summary of the .log file.
     * @param outsideOfProg numbers of lines outside of SRT--END
     * @return totalOutsideErrors -actual frequency of error 13
     */
    public int LinesOutsideOfProgramCalculator(ArrayList<Integer> outsideOfProg){
        String outsideLineNums = "";
        Set<Integer> lineNumbers = new LinkedHashSet<>(outsideOfProg);
        int totalOutsideErrors = outsideOfProg.size() - lineNumbers.size();
        totalOutsideErrors = outsideOfProg.size() - totalOutsideErrors;
        int firstNum = 0;
        for(int i : lineNumbers){
            if(firstNum == 0) {
                outsideLineNums = outsideLineNums + i;
            } else{
                outsideLineNums = outsideLineNums + ", " + i;
            }
            firstNum++;
        }
        toLogList.add(Errors(13, outsideLineNums));
        return totalOutsideErrors;
    }

    /**
     * Takes an error and writes message to log.
     * @param errID - specific error number
     * @param problemWord - word that caused error
     * @param linesToLog - contains lines to be written to .log
     * @param lineCount - line number parser is on
     * @param line - no comments; written to log
     * @param numOfErr - stores collective num of errors in .pal
     * @param outsideOfProgram contains line numbers found outside of prog
     */
    public void ErrorStatementPreparer(int errID, String problemWord, List<String> linesToLog, int lineCount, String line,
                                       ArrayList<Integer> numOfErr, ArrayList<Integer> outsideOfProgram){
        AddToErrorList(errID);
        if (!problemWord.equals(" ")) {
            AddToProblemWordList(problemWord);
        } if(!line.equals(" ") && errID != 13) {
            linesToLog.add(lineCount + " " + line);
        } if(outsideOfProgram == null) {
            ErrorStatementsToLogWriter(numOfErr);
        } else{
            outsideOfProgram.add(lineCount);
            numOfErr.add(13);
        }
    }

    /**
     * Adds specific error messages based on errors found in
     * numOfErr to .log file.
     * @param numOfErr contains errors found in a line
     */
    public void ErrorStatementsToLogWriter(ArrayList<Integer> numOfErr){
        for(int i : errorList){
            if(i == 0 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 16) {
                toLogList.add(Errors(i, problemWordList.get(0)));
                numOfErr.add(i);
            } else{
                if(i != 1) {
                    toLogList.add(Errors(i, null));
                    numOfErr.add(i);
                }
            }
        }
        if(errorList.contains(1)) {
            for(String word : problemWordList) {
                if(!validRegisters.contains(word) && !validVars.contains(word)) {
                    toLogList.add(Errors(1, word));
                    numOfErr.add(1);
                }
            }
        }
    }

    /**
     * Checks the word count in a line based on opcode type.
     * Used to report if a statement has too many/too few operands.
     * @param wordCount num of split words in a line
     * @param opcode type of opcode
     */
    public void WordsInLine(int wordCount, String opcode){
        switch(opcode){
            case "ASMD":
            case "BEBG":
                WordCountError(3, wordCount);
                break;
            case "MC":
            case "DEF":
                WordCountError(2, wordCount);
                break;
            case "ID":
            case "BR":
                WordCountError(1, wordCount);
                break;
        }
    }

    /**
     * Takes information from WordsInLine() based on which opcode statement
     * is being tested and reports correct error.
     * @param i correct num of words in opcode's statement
     * @param wordCount num of split words from opcode's statement
     */
    public void WordCountError(int i, int wordCount){
        if(wordCount > i){
            AddToErrorList(2);
        } else if(wordCount < i){
            AddToErrorList(3); }
    }
}