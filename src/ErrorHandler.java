import java.util.ArrayList;
import java.util.List;

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

    /**
     * Takes a linesToLog arrayList for adding errors.
     * @param list lines to be added to .log file
     */
    public ErrorHandler(List<String> list){
        this.toLogList = list;
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
     * Adds specific error messages based on errors found in
     * numOfErr to .log file.
     * @param numOfErr contains errors found in a line
     */
    public void ErrorsToLog(ArrayList<Integer> numOfErr){
        if(errorList.contains(1) && errorList.contains(3)){//don't report if Too Few Operands
            errorList.remove(Integer.valueOf(1));
        }
        for(int i : errorList){
            if(i == 0 || i == 1 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10) {
                toLogList.add(Errors(i, problemWordList.get(0)));
                numOfErr.add(i);
            }
            else{
                toLogList.add(Errors(i, null));
                numOfErr.add(i);
            }
        }
    }

    /**
     * Points to correct error statement for .log file.
     * @param errNum triggered error number
     * @param word problem word
     * @return error statement for .log file
     */
    public String Errors(int errNum, String word){
        String error = null;
        switch (errNum){
            case 0: error = " ** Wrong Operand Type: Immediate value '"  + word + "' where register was expected.";
                    break;
            case 1: error = " ** Ill-Formed Operand: '" + word + "' is not a valid operand type. Use R0-R7 or a defined variable.";
                    break;
            case 2: error = " ** Too Many Operands: You have exceeded the valid number of operands for this opcode.";
                    break;
            case 3: error = " ** Too Few Operands: You have not met the valid number of operands for this opcode.";
                    break;
            case 4: error = " ** Ill-Formed Label: '" + word + "' can't exceed 12 chars and must contain one colon."
                            + "\n ** Colon must be at the end of the label.";
                    break;
            case 5: error = " ** Invalid Opcode: '" + word + "' is not a valid opcode. Please review valid opcodes "
                            + "for more details.";
                    break;
            case 6: error = " ** Branches to Non-Existent Label: '" + word + "' is not a label that was previously created.";
                    break;
            case 7: error = " ** Wrong Operand Type: Value '" + word + "' where immediate value was expected.";
                    break;
            case 8: error = " ** Ill-Formed Exit Opcode: '" + word + "' is not a valid opcode to end a program with. Try 'END' instead.";
                    break;
            case 9: error = " ** Ill-Formed Start Opcode: '" + word + "' is not a valid opcode to start a program with. Try 'SRT' instead.";
                    break;
            case 10: error = " ** Missing Label in Branch: '" + word + "' is not label. Please replace with a valid label.";
                    break;
            case 11: error = " ** Misplaced SRT: Can't start a new program until this one has ended";
                    break;
            case 12: error = " ** Misplaced END: SRT hasn't be instantiated yet.";
                    break;
            case 13: error = " ** Code Outside of Program: All lines of code must be contained between SRT and END opcodes.";
                    break;
            case 14: error = " ** END Not Detected: All PAL programs must conclude with END opcode.";
                    break;
            case 15: error = " ** Invalid DEF: All DEFs must be declared after SRT and before any other executable ops.";
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
     */
    public void ENDOrSRT(String line, int n, int lineCount, List<String> linesToLog, ArrayList<Integer> numOfErr){
        if (n == 0){
            AddToErrorList(8);
            AddToProblemWordList(line);
            linesToLog.add(lineCount + " " + line);
            ErrorsToLog(numOfErr);
        }else if(n == 1){
            AddToErrorList(9);
            AddToProblemWordList(line);
            linesToLog.add(lineCount + " " + line);
            ErrorsToLog(numOfErr);
        }
    }
}