/*
 * Class which transforms trace into sequency structures
 */

package data.processing;



import data.handler.DataHandler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luciana
 */
public class SequenceStructure {
    private DataHandler file;
    private BufferedWriter writer;
    private LinkedList<LinkedList<String>> structure;

    //TODO: retirar try catch
    public SequenceStructure(String fileName, String structureFile){
        this.file = new DataHandler(fileName);
        this.structure = new LinkedList<LinkedList<String>>();
        try {
            if(!structureFile.equals("")) this.writer = new BufferedWriter(new FileWriter(structureFile));
        } catch (IOException ex) {
            Logger.getLogger(SequenceStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public SequenceStructure(String fileName){
        this.file = new DataHandler(fileName);
        this.structure = new LinkedList<LinkedList<String>>();
    }

    public LinkedList<LinkedList<String>> getStructure(){
        return this.structure;
    }

    /**
     * Builds a new structure from the trace
     */
    public void handleFile() throws IOException {
        this.file.open();
        Vector line=this.file.read();
        LinkedList<String> methodList = new LinkedList<String>();
        int lastId = 1;
        String lastMethod = ((String[])line.elementAt(0))[1];
        for(line=this.file.read(); line.size()>0; line=this.file.read()){
            String[] method = (String[])line.elementAt(0);
            if(!method[0].startsWith("<SEQ")){
                if (Integer.parseInt(method[2])> lastId){
                    methodList.add(method[1]);
                    lastId = Integer.parseInt(method[2]);
                    lastMethod = method[1];
                }else if(Integer.parseInt(method[2])< lastId){
                    if(methodList.size()>1) structure.add(methodList);//Adds in sequency the subsequency
                    //Creates a new subsequency
                    methodList = new LinkedList<String>();
                    methodList.add(method[1]);
                    lastId = Integer.parseInt(method[2]);
                    lastMethod = method[1];
                }else if((Integer.parseInt(method[2])== lastId) && !lastMethod.equals(method[1])){
                    if(methodList.size()>1) structure.add(methodList);
                    LinkedList<String> temporary = new LinkedList<String>();
                    temporary.addAll(methodList);
                    temporary.set(methodList.size()-1, method[1]);
                    methodList = new LinkedList<String>();
                    methodList.addAll(temporary);
                    lastMethod = method[1];
                }
            }
        }
        this.file.close();
        saveStructure();
    }

    /**
     * Reads the trace file and sets the sequences with packages and classes
     */
    public void getTraceSequences() throws IOException, NullPointerException {
        this.file.open();
        Vector line=this.file.read();
        LinkedList<String> methodList = new LinkedList<String>();
        int lastId = 1;
        String lastMethod = ((String[])line.elementAt(0))[1];
        methodList.add(((String[])line.elementAt(0))[0]+"-"+lastMethod);
        for(line=this.file.read(); line.size()>0; line=this.file.read()){
            String[] method = (String[])line.elementAt(0);
            if(!method[0].startsWith("<SEQ")){
                if (Integer.parseInt(method[2])> lastId){
                    methodList.add(method[0]+"-"+method[1]);
                    lastId = Integer.parseInt(method[2]);
                    lastMethod = method[1];
                }else if(Integer.parseInt(method[2])< lastId){
                    if(methodList.size()>1) structure.add(methodList);//Adds in sequency the subsequency
                    //Creates a new subsequency
                    methodList = new LinkedList<String>();
                    methodList.add(method[0]+"-"+method[1]);
                    lastId = Integer.parseInt(method[2]);
                    lastMethod = method[1];
                }else if((Integer.parseInt(method[2])== lastId) && !lastMethod.equals(method[1])){
                    LinkedList<String> temporary = new LinkedList<String>();
                    if(methodList.size()>1) structure.add(methodList);
                    if(methodList.size()>0){
                        temporary.addAll(methodList);
                        temporary.set(methodList.size()-1, method[0]+"-"+method[1]);
                        methodList = new LinkedList<String>();
                        methodList.addAll(temporary);
                    }
                    lastMethod = method[1];
                }
            }
          //  if(this.structure.size()>1000) save();
        }
        if(!structure.contains(methodList)) structure.add(methodList);
        this.file.close();
       /// saveStructure();
    }

    private void save() {
        StringBuilder builder = new StringBuilder();
        Iterator<LinkedList<String>> structureIterator = this.structure.iterator();
        while(structureIterator.hasNext()){
            LinkedList<String> methodList = structureIterator.next();
            Iterator<String> methodIterator = methodList.iterator();
            while(methodIterator.hasNext()){
                builder.append(methodIterator.next()+",");
            }
            builder.append("\n");
        }
        this.structure = new LinkedList<LinkedList<String>>();
        try {
            this.writer.write(builder.toString());
        } catch (IOException ex) {
            Logger.getLogger(SequenceStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /***
     * After building the structure it saves in a file
     */
    private void saveStructure() {
        StringBuilder builder = new StringBuilder();
        Iterator<LinkedList<String>> structureIterator = this.structure.iterator();
        while(structureIterator.hasNext()){
            LinkedList<String> methodList = structureIterator.next();
            Iterator<String> methodIterator = methodList.iterator();
            while(methodIterator.hasNext()){
                builder.append(methodIterator.next()+",");
            }
            builder.append("\n");
        }
        try {
            this.writer.write(builder.toString());
            this.writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(SequenceStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


//    public static void main(String[] args){
//        String dataBase = "F:\\Projeto Mestrado - Nova Fase\\traces\\BasicoTestejava-Compressed-Reduced-Compressed\\Thread-1\\data.trace";
////      String dataBase = "D:\\Pesquisa Mestrado\\Compressor de rastros\\ArgoUML\\Contexto1\\Trace5\\Merge\\newMerge.txt";
////      String structureFile = "F:\\Projeto Mestrado - Nova Fase\\ArgoUML\\Thread\\structure_sequency_GSP_ToChameleon.txt";
//        String structureFile = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\classesStructureSequency.txt";
//        SequenceStructure readFile = new SequenceStructure(dataBase, structureFile);
////        readFile.handleFile();
//        readFile.getTraceSequences();
//       //readFile.handleFileToChameleon();
//    }




}
