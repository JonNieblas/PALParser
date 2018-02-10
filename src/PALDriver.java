import java.util.ArrayList;

public class PALDriver {
    public static void main(String args[]){

        String file = "practice_program.pal";
        ArrayList<String> opList = new ArrayList<>();

        //valid opcodes in PAL
        opList.add("ADD");
        opList.add("SUB");
        opList.add("MUL");
        opList.add("DIV");
        opList.add("COPY");
        opList.add("MOVE");
        opList.add("INC");
        opList.add("DEC");
        opList.add("BEQ");
        opList.add("BGT");
        opList.add("BR");

        PALParser parser1 = new PALParser (opList, file);
        parser1.Parser();
    }
}
