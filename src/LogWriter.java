public class LogWriter {
//
//    /**
//     * Takes an ArrayList containing all lines, errors & comments,
//     * then writes it to a .log file named after the original .pal
//     * file name.
//     */
//    public void FileWriter(){
//        Path logFile = Paths.get("logs/" + fileNameAppended + ".log");//used for writing to .log file
//        try {
//            Files.write(logFile, linesToLog, Charset.forName("UTF-8"));
//        }
//        catch(IOException ex){
//            System.out.println("Error writing originalLine to " + logFile);
//        }
//    }
//
//    /**
//     * Adds correct header information to ArrayList linesToLog.
//     */
//    public void LogHeaderWriter(){
//        Date date = new Date();
//        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
//
//        String title = "PAL Error Check";
//        String palName = fileNameAppended;
//        String logName = fileNameAppended + ".log";
//        String strDate = dateFormat.format(date);
//        String myName = "Jonathan Nieblas";
//        String courseName = "CS 3210";
//
//        String header = (title + " || " + palName + " || " + logName + " || " + strDate + " || "
//                + myName + " || " + courseName);
//        linesToLog.add(header);
//        linesToLog.add(" ");
//        linesToLog.add("PAL Program Listing:");
//        linesToLog.add(" ");
//    }
//
//    /**
//     * Adds a summary to the .log file after a .pal program
//     * has been parsed.
//     */
//    public void LogSummaryWriter(){
//
//        int totalErrors = CountTotalErrors();
//        linesToLog.add(" ");
//        linesToLog.add("Summary ----------");
//        linesToLog.add(" ");
//        linesToLog.add("total Errors = " + totalErrors);
//        TotalErrorsToLog();
//        if(totalErrors > 0){
//            linesToLog.add("Processing Complete - PAL program is invalid.");
//        } else{
//            linesToLog.add("Processing Complete - PAL program is valid.");
//        }
//    }
//
//    /**
//     * Counts the number of each type of error in the .pal file.
//     * Also, adds up the total number of errors.
//     * @return total num of errors in .pal file
//     */
//    public int CountTotalErrors(){
//        for(int i : numberOfErrors){
//            switch(i){
//                case 0: err0++;
//                    break;
//                case 1: err1++;
//                    break;
//                case 2: err2++;
//                    break;
//                case 3: err3++;
//                    break;
//                case 4: err4++;
//                    break;
//                case 5: err5++;
//                    break;
//                case 6: err6++;
//                    break;
//                case 7: err7++;
//                    break;
//                case 8: err8++;
//                    break;
//                case 9: err9++;
//                    break;
//                case 10: err10++;
//                    break;
//                case 11: err11++;
//                    break;
//                case 12: err12++;
//                    break;
//                case 13: err13++;
//                    break;
//                case 14: err14++;
//                    break;
//                case 15: err15++;
//                    break;
//                case 16: err16++;
//                    break;
//            }
//        }
//        int total = err0 + err1 + err2 + err3 + err4 + err5 + err6 + err7 + err8 + err9 + err10 + err11
//                + err12 + err13 + err14 + err15 + err16;
//        return total;
//    }
//
//    /**
//     * Prints total number of each type of error to summary
//     * if any errors are present at all.
//     */
//    public void TotalErrorsToLog(){
//        if(err0 > 0){
//            linesToLog.add(" " + err0 + " Wrong Operand Type(s) (Registers/Variables)");
//        } if(err1 > 0){
//            linesToLog.add(" " + err1 + " Ill-Formed Operand(s)");
//        } if(err2 > 0){
//            linesToLog.add(" " + err2 + " Too Many Operands");
//        } if(err3 > 0){
//            linesToLog.add(" " + err3 + " Too Few Operands");
//        } if(err4 > 0){
//            linesToLog.add(" " + err4 + " Ill-Formed Label(s)");
//        } if(err5 > 0){
//            linesToLog.add(" " + err5 + " Invalid Opcode(s)");
//        } if(err6 > 0){
//            linesToLog.add(" " + err6 + " Branches to Non-Existent Label(s)");
//        } if(err7 > 0){
//            linesToLog.add(" " + err7 + " Wrong Operand Type(s) (Immediate Values)");
//        } if(err8 > 0){
//            linesToLog.add(" " + err8 + " Ill-Formed Exit Opcode(s)");
//        } if(err9 > 0){
//            linesToLog.add(" " + err9 + " Ill-Formed Start Opcode(s)");
//        } if(err10 > 0){
//            linesToLog.add(" " + err10 + " Missing Label(s) in Branch(es)");
//        } if(err11 > 0){
//            linesToLog.add(" " + err11 + " Misplaced SRT(s)");
//        } if(err12 > 0){
//            linesToLog.add(" " + err12 + " Misplaced END(s)");
//        } if(err13 > 0){
//            linesToLog.add(" " + err13 + " Code(s) Outside of Program");
//        } if(err14 > 0){
//            linesToLog.add(" " + err14 + " END Not Detected");
//        } if(err15 > 0){
//            linesToLog.add(" " + err15 + " Invalid DEF(s)");
//        } if(err16 > 0){
//            linesToLog.add(" " + err16 + " Label(s) Not Used Warning");
//        }
//    }
//
//    /**
//     * Creates opList, containing valid opcodes.
//     */
//    public void CreateOpList(){
//        opList.add("ADD");
//        opList.add("SUB");
//        opList.add("MUL");
//        opList.add("DIV");
//        opList.add("COPY");
//        opList.add("MOVE");
//        opList.add("INC");
//        opList.add("DEC");
//        opList.add("BEQ");
//        opList.add("BGT");
//        opList.add("BR");
//    }
}
