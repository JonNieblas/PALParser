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

    //points to correct error statement
    public String Errors(int i){

        return null;
    }
}
