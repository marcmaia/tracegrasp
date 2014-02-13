package data.processing;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




import data.handler.CarryFileMemory;
import data.handler.DataHandler;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Luciana
 */
public class CleanTrace {
    private CarryFileMemory traceFile;
    private BufferedWriter writer;
    private DataHandler dh;

    public CleanTrace(String trace, String outFile) throws IOException{
        this.traceFile = new CarryFileMemory(trace);
        this.dh = new DataHandler(trace);
        this.writer = new BufferedWriter(new FileWriter(outFile));
    }

    /**
     * For small traces. Takes only class and method names.
     */
    public void readFile() throws IOException, FileNotFoundException{
        String[] file = this.traceFile.carryCompleteFile();
        String builder = "";
        for(String line : file){
            String[] method = line.split(";");
            int index = method[0].lastIndexOf(".");
            builder = builder + method[0].substring(index+1)+"."+method[1]+","+method[2]+"\n";
        }
        write(builder);
    }

    /**
     * This method reads a Compressed-Reduced file only.
     * Reads the out file generated through the Metric class at a compressed file.
     */
    public void readFilePackages() throws IOException, FileNotFoundException, Error{
        String[] file = this.traceFile.carryCompleteFile();
        String builder = "";
        for(String line : file){
            if(!line.startsWith("<SEQ_")){
                String[] method = line.split(",");
                builder = builder + method[0]+"."+method[1]+","+method[2]+"\n";
            }
        }
        write(builder);
    }

    /**
     * This method reads a compressed trace only
     */
    public void readBigFile() throws IOException{
        this.dh.open();
        Vector items;
        String build = new String();
        for(items = this.dh.read(); items.size()>0; items=this.dh.read()){
            String[] method = (String[])items.elementAt(0);
            build = build + method[0] + "." + method[1] + "," +method[2]+"\n";
            writeSteps(build);
            build="";
        }
        this.dh.close();
        writeSteps(build);
        closeFile();
    }

    private void closeFile() throws IOException {
        this.writer.flush();        
    }

    private void write(String builder) throws IOException {
        this.writer.append(builder);
        this.writer.flush();        
    }

    private void writeSteps(String builder) {
        try {
            this.writer.append(builder);
        } catch (IOException ex) {
            Logger.getLogger(CleanTrace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


//    public static void main(String[] args){
//        String trace = "F:\\Projeto Mestrado - Nova Fase\\traces\\BasicoTestejava-Compressed-Reduced-Compressed\\Thread-1\\data.trace";
//        String out = "F:\\Projeto Mestrado - Nova Fase\\traces\\BasicoTestejava-Compressed-Reduced-Compressed\\Thread-1\\clear1.txt";
//        CleanTrace clear = new CleanTrace(trace, out);
//        clear.readFilePackages();
//       // clear.readBigFile();
//    }
}
