/**
 * Class PALDriver for Class PALParser.
 * Contains the name of the .pal program to be parsed.
 * @author Jonathan Nieblas
 */
public class PALDriver {
    public static void main(String args[]){

        //files to be parsed here
        String file = "practice_program.pal";

        PALParser parser1 = new PALParser (file);
        parser1.Parser();
    }
}
