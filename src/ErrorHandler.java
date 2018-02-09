import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private ArrayList<Integer> errorList = new ArrayList<>();
    private List<String> toLogList;

    public ErrorHandler(List<String> list){
        this.toLogList = list;
    }

    public void AddToErrorList(int err){
        errorList.add(err);
    }

    //adds errors to log file
    public void ErrorsToLog(){
        for(int i : errorList){
            toLogList.add(Errors(i));
        }
    }

    //points to correct error statement for .log
    public String Errors(int i){
        String error;

        if (i == 0){
            error = "*** Wrong Operand Type: Immediate Value Where Register Was Expected";
        }
        else if(i == 1){
            error = "*** Ill-Formed Operand: Not a valid Operand Type";
        }
        else if(i == 2){
            error = "*** Too Many Operands: Only Three Operands Are Allowed With This Opcode";
        }
        else if(i == 3){
            error = "*** Too Few Operands: Only Three Operands Are Allowed With This Opcode";
        }
        else{
            error = "?";//change or initialize
        }

        return error;
    }
}
