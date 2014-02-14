/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package log.messages;

/**
 *
 * @author Luciana
 */
public class LOG {
    
    public static String compressorInvalidParameters(){
        return "\n >> Insert the Minimum Granularity to Perform the Summarizer. \n";
    }

    public static String getMessageStep2(){
        return "Executing Step 2";
    }

    public static String getMessageStep3(){
        return "Executing Step 3";
    }

    public static String updatingRepository(){
        return "Updating the Trace Repository ...";
    }

    public static String updatedRepository(){
        return "Updating the Trace Repository ... Done";
    }

    public static String numberFormatException(){
        return "\n An Exception has Ocurred Trying to Convert a String Parameter in Integer.";
    }

    public static String errorOcurred(String step){
        return "\n=============\nError at the "+step + ". ";
    }

    public static String recoveringFunctionalities(){
        return "Recovering Functionalities in the Trace ...";
    }

    public static String recoveredFunctionalities(){
        return "Recovering Functionalities in the Trace ... Done.";
    }

    public static String recoveringTreeView(){
        return "Recovering Tree View ...";
    }

    public static String recoveredTreeView(){
        return "Recovering Tree View ... Done.";
    }

    public static String aspectTraceFile(){
        return "\\data.trace";
    }
}
