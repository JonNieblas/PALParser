import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private ArrayList<Integer> errorList = new ArrayList<>();
    private ArrayList<String> problemWordList = new ArrayList<>();
    private List<String> toLogList;

    public ErrorHandler(List<String> list){
        this.toLogList = list;
    }

    public void AddToErrorList(int err){
        errorList.add(err);
    }

    public void AddToProblemWordList(String word){
        problemWordList.add(word);
    }

    //adds errors to log file
    public void ErrorsToLog(){
        if(errorList.contains(1) && errorList.contains(3)){
            errorList.remove(Integer.valueOf(1));
        }
        for(int i : errorList){
            if(i == 0 || i == 1 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10) {
                toLogList.add(Errors(i, problemWordList.get(0)));
            }
            else{
                toLogList.add(Errors(i, null));
            }
        }
    }

    //points to correct error statement for .log
    public String Errors(int i, String word){
        String error = null;
        switch (i){
            case 0: error = "*** Wrong Operand Type: Immediate Value '"  + word + "' Where Register Was Expected.";
                    break;
            case 1: error = "*** Ill-Formed Operand: '" + word + "' Is Not a Valid Operand Type. Registers R0 - R7 Are Valid.";
                    break;
            case 2: error = "*** Too Many Operands: You Have Exceeded The Valid Number Of Operands For This Opcode.";
                    break;
            case 3: error = "*** Too Few Operands: You Have Not Met The Valid Number Of Operands For This Opcode.";
                    break;
            case 4: error = "*** Ill-Formed Label: '" + word + "' Can't Exceed 12 Chars And Must Contain One Colon."
                            + "\n*** Colon Must Be At The End Of The Label.";
                    break;
            case 5: error = "*** Invalid Opcode: '" + word + "' Is Not a Valid Opcode. Please Review Valid Opcodes "
                            + "For More Details.";
                    break;
            case 6: error = "*** Branches to Non-Existent Label: '" + word + "' Is Not a Label That Was Previously Created.";
                    break;
            case 7: error = "*** Wrong Operand Type: String '" + word + "' Where Immediate Value Was Expected.";
                    break;
            case 8: error = "*** Ill-Formed Exit Opcode: '" + word + "' is not a valid opcode to end a program with. Try 'END' instead.";
                    break;
            case 9: error = "*** Ill-Formed Start Opcode: '" + word + "' is not a valid opcode to start a program with. Try 'SRT' instead.";
                    break;
            case 10: error = "*** Expecting Label: '" + word + "' is not label. Please replace with a valid label.";
                    break;
            case 11: error = "*** SRT Detected in Program: Can't start a new program until this one has ended";
                    break;
            case 12: error = "*** END Declared Before SRT: SRT hasn't be instantiated yet.";
                    break;
            case 13: error = "*** Code Outside of Program: All lines of code must be contained between SRT and END opcodes.";
                    break;
            case 14: error = "*** END Not Detected: All PAL programs must conclude with END opcode.";
                    break;
        }
        return error;
    }

    public void ENDOrSRT(String line, int n, int currentLine, List<String> linesToLog){
        if (n == 0){
            AddToErrorList(8);
            AddToProblemWordList(line);
            linesToLog.add(currentLine + " " + line);
            ErrorsToLog();
        }else if(n == 1){
            AddToErrorList(9);
            AddToProblemWordList(line);
            linesToLog.add(currentLine + " " + line);
            ErrorsToLog();
        }
    }
}