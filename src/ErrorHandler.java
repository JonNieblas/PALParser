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
            if(i == 0 || i == 1 || i == 4 || i == 5 || i == 6 || i == 7 ) {
                toLogList.add(Errors(i, problemWordList.get(0)));
            }
            else{
                toLogList.add(Errors(i, null));
            }
        }
    }

    //points to correct error statement for .log
    public String Errors(int i, String word){
        String error = "Error Statement";

        if (i == 0){
            error = "*** Wrong Operand Type: Immediate Value "  + word + " Where Register Was Expected.";
        }
        else if(i == 1){
            error = "*** Ill-Formed Operand: " + word + " Is Not a Valid Operand Type. Registers R0 - R13 Are Valid.";
        }
        else if(i == 2){
            error = "*** Too Many Operands: You Have Exceeded The Valid Number Of Operands For This Opcode.";
        }
        else if(i == 3){
            error = "*** Too Few Operands: You Have Not Met The Valid Number Of Operands For This Opcode.";
        }
        else if(i == 4){
            error = "*** Ill-Formed Label: " + word + " Must Be At Most 12 Characters Long And Contain One Colon. " +
                    "\n*** Colon Must Be At The End Of The Label.";
        }
        else if(i == 5){
            error = "*** Invalid Opcode: " + word + " Is Not a Valid Opcode. Please Review Valid Opcodes " +
                    "For More Details.";
        }
        else if(i == 6){
            error = "*** Branches to Non-Existent Label: " + word + " Is Not a Branch That Was Previously Created.";
        }
        else if(i == 7){
            error = "*** Wrong Operand Type: String " + word + " Where Immediate Value Was Expected.";
        }

        return error;
    }
}
