/*
 * This class is used after the execution GSP algorithm
 */

package data.processing;

import data.handler.CarryFileMemory;
import data.mining.InterestingPatterns;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luciana
 */
public class RecoveryPattern {
    private CarryFileMemory readFile;
    private CarryFileMemory readSequences;
    private BufferedWriter writeFinalPattens;
    private LinkedList<LinkedList<String>> recoveredPatterns;

    //TODO: retirar o try catch
    public RecoveryPattern(String patterns, String outPatterns, String sequences) {
        this.readFile = new CarryFileMemory(patterns);
        this.readSequences = new CarryFileMemory(sequences);
        this.recoveredPatterns = new LinkedList<LinkedList<String>>();
        if(!outPatterns.equals("")) try {
            this.writeFinalPattens = new BufferedWriter(new FileWriter(outPatterns));
        } catch (IOException ex) {
            Logger.getLogger(RecoveryPattern.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RecoveryPattern() {
        this.recoveredPatterns = new LinkedList<LinkedList<String>>();
    }



    /**
     * Sequence file does not have packages or/and classes names
     */
    public void getPatterns() throws FileNotFoundException, IOException {
        LinkedList<LinkedList<String>> applicantList = readSequenceFile(this.readFile.carryCompleteFile());
        LinkedList<LinkedList<String>> sequenceList = readSequenceFile(this.readSequences.carryCompleteFile());
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<LinkedList<String>> iteratorSequence = sequenceList.iterator();
        while(iteratorSequence.hasNext()){
            LinkedList<String> sequence = iteratorSequence.next();
            Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
            while(iteratorApplicant.hasNext()){
                LinkedList<String> pattern = iteratorApplicant.next();
                int i=0;
                LinkedList<String> newPattern = new LinkedList<String>();
                for(int j=0; j<pattern.size(); j++){
                    while(i<sequence.size()){
                        if(sequence.get(i).startsWith(pattern.get(j))){
                            newPattern.add(sequence.get(i));
                            i++;
                            break;
                        }else {
                            newPattern.clear();
                            j=-1;
                            i++;
                            break;
                        }
                    }
                }
                if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) patternList.add(newPattern);
            }
        }
        recordDesingPatterns(patternList);
    }

    /**
     * Gets all classes and packages which methods belongs at the current sequence
     */
    public LinkedList<LinkedList<String>> getSkeleton(LinkedList<LinkedList<String>> applicantList, LinkedList<LinkedList<String>> sequenceList){
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<LinkedList<String>> iteratorSequence = sequenceList.iterator();
        while(iteratorSequence.hasNext()){
            LinkedList<String> sequence = iteratorSequence.next();
            Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
            while(iteratorApplicant.hasNext()){
                LinkedList<String> pattern = iteratorApplicant.next();
                int i=0;
                LinkedList<String> newPattern = new LinkedList<String>();
                LinkedList<String> methods = new LinkedList<String>();
                for(int j=0; j<pattern.size(); j++){
                    while(i<sequence.size()){
                        if((sequence.get(i).split("-"))[1].startsWith(pattern.get(j))){
                            newPattern.add(sequence.get(i));
                            methods.add((sequence.get(i).split("-"))[1]);
                            i++;
                            break;
                        }else {
                            newPattern.clear();
                            methods.clear();
                            j=-1;
                            i++;
                            break;
                        }
                    }
                }
                if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) {
                    patternList.add(newPattern);
                    if(!this.recoveredPatterns.contains(methods)) this.recoveredPatterns.add(methods);
                }
            }
        }
       // recordDesingPatterns(patternList);
        return patternList;
    }

    public LinkedList<LinkedList<String>> getSuffixSkeleton(LinkedList<LinkedList<String>> applicantList, LinkedList<LinkedList<String>> sequenceList){
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<LinkedList<String>> iteratorSequence = sequenceList.iterator();
        while(iteratorSequence.hasNext()){
            LinkedList<String> sequence = iteratorSequence.next();
            Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
            while(iteratorApplicant.hasNext()){
                LinkedList<String> pattern = iteratorApplicant.next();
                int i=0;
                LinkedList<String> newPattern = new LinkedList<String>();
                LinkedList<String> methods = new LinkedList<String>();
                for(int j=0; j<pattern.size(); j++){
                    while(i<sequence.size()){
                        if((sequence.get(i).split("-"))[1].endsWith(pattern.get(j))){
                            newPattern.add(sequence.get(i));
                            methods.add((sequence.get(i).split("-"))[1]);
                            i++;
                            break;
                        }else {
                            newPattern.clear();
                            methods.clear();
                            j=-1;
                            i++;
                            break;
                        }
                    }
                }
                if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) {
                    patternList.add(newPattern);
                    if(!this.recoveredPatterns.contains(methods)) this.recoveredPatterns.add(methods);
                }
            }
        }
       // recordDesingPatterns(patternList);
        return patternList;
    }

    /**
     *
     * @param applicantList
     * @param sequenceList
     * @return
     */
    public LinkedList<LinkedList<String>> getFullMethods(LinkedList<LinkedList<String>> applicantList, 
        LinkedList<LinkedList<String>> sequenceList){
        
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<LinkedList<String>> iteratorSequence = sequenceList.iterator();
        while(iteratorSequence.hasNext()){
            LinkedList<String> sequence = iteratorSequence.next();
            Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
            while(iteratorApplicant.hasNext()){
                LinkedList<String> pattern = iteratorApplicant.next();
                int i=0;
                LinkedList<String> newPattern = new LinkedList<String>();
                for(int j=0; j<pattern.size(); j++){
                    while(i<sequence.size()){
                        if((sequence.get(i).split("-"))[1].equals(pattern.get(j))){
                            newPattern.add(sequence.get(i));                            
                            i++;
                            break;
                        }else {
                            newPattern.clear();
                            j=-1;
                            i++;
                            break;
                        }
                    }
                }
                if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) {
                    patternList.add(newPattern);
                }
            }
        }
        this.recoveredPatterns.addAll(applicantList);
        return patternList;
    }

        public LinkedList<LinkedList<String>> getFullMethods(LinkedList<LinkedList<String>> applicantList,
        Map sequenceList){

        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<Integer> iteratorSequence = sequenceList.keySet().iterator();
        while(iteratorSequence.hasNext()){//For each scenario traces
            LinkedList<LinkedList<LinkedList<String>>> sequences = (LinkedList<LinkedList<LinkedList<String>>>)sequenceList.get(iteratorSequence.next());
            Iterator<LinkedList<LinkedList<String>>> iteratorTrace = sequences.iterator();
            while(iteratorTrace.hasNext()){//For each thread of a functionality
                LinkedList<LinkedList<String>> sequenceTrace = iteratorTrace.next();
                Iterator<LinkedList<String>> iteratorSubSequenceTrace = sequenceTrace.iterator();
                while(iteratorSubSequenceTrace.hasNext()){
                    LinkedList<String> sequence = iteratorSubSequenceTrace.next();
                    Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
                    while(iteratorApplicant.hasNext()){//For each sequence found
                        LinkedList<String> pattern = iteratorApplicant.next();
                        int i=0;
                        LinkedList<String> newPattern = new LinkedList<String>();
                        for(int j=0; j<pattern.size(); j++){
                            while(i<sequence.size()){
                                if((sequence.get(i).split("-"))[1].equals(pattern.get(j))){
                                    newPattern.add(sequence.get(i));
                                    i++;
                                    break;
                                }else {
                                    newPattern.clear();
                                    j=-1;
                                    i++;
                                    break;
                                }
                            }
                        }
                        if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) {
                            patternList.add(newPattern);
                        }
                    }
                }
            }
        }
        this.recoveredPatterns.addAll(applicantList);
        return patternList;
    }

    public LinkedList<LinkedList<String>> getFullClasses(LinkedList<LinkedList<String>> applicantList,
           LinkedList<LinkedList<String>> sequenceList){
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        Iterator<LinkedList<String>> iteratorSequence = sequenceList.iterator();
        while(iteratorSequence.hasNext()){
            LinkedList<String> sequence = iteratorSequence.next();
            Iterator<LinkedList<String>>  iteratorApplicant = applicantList.iterator();
            while(iteratorApplicant.hasNext()){
                LinkedList<String> pattern = iteratorApplicant.next();
                int i=0;
                LinkedList<String> newPattern = new LinkedList<String>();
                for(int j=0; j<pattern.size(); j++){
                    while(i<sequence.size()){
                        String[] temp = (sequence.get(i).split("-"))[0].split("\\.");
                        if(temp[temp.length-1].equals(pattern.get(j))){
                            newPattern.add((sequence.get(i).split("-"))[0]);
                            i++;
                            break;
                        }else {
                            newPattern.clear();
                            j=-1;
                            i++;
                            break;
                        }
                    }
                }
                if(newPattern.size()==pattern.size() && !patternList.contains(newPattern)) {
                    patternList.add(newPattern);
                }
            }
        }
        InterestingPatterns filter = new InterestingPatterns();
        filter.setSequentialPatterns(applicantList);
        filter.selectPatterns();
        this.recoveredPatterns.addAll(filter.getSequentialPatterns());
        return patternList;
    }

    /**
     * Gets sequential names with method names only.
     * @return
     */
    public LinkedList<LinkedList<String>> getMethods(){
        return this.recoveredPatterns;
    }

    public void setMethods(LinkedList<LinkedList<String>> methods){
        this.recoveredPatterns.addAll(methods);
    }

    
    
    private LinkedList<LinkedList<String>> readSequenceFile(String[] file) {        
        LinkedList<String> sequence = null;
        LinkedList<LinkedList<String>> sequenceList = new LinkedList<LinkedList<String>>();
        for(int i=0; i<file.length; i++){
            String[] singleSequence = file[i].split(",");
            sequence = new LinkedList();
            for(int j=0; j<singleSequence.length; j++){                
                sequence.add(singleSequence[j]);
            }
            sequenceList.add(sequence);
        }
        return sequenceList;
    }

    private void recordDesingPatterns(LinkedList<LinkedList<String>> patternList) {
        StringBuilder builder = new StringBuilder();
        Iterator<LinkedList<String>> patternListIterator = patternList.iterator();
        while(patternListIterator.hasNext()){
            LinkedList<String> pattern = patternListIterator.next();
            Iterator<String> patternIterator = pattern.iterator();
            while(patternIterator.hasNext()){
                builder.append(patternIterator.next()+",");
            }
            builder.append("\n");
        }
        try {
            this.writeFinalPattens.write(builder.toString());
            this.writeFinalPattens.flush();
        } catch (IOException ex) {
            Logger.getLogger(RecoveryPattern.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
//
//    public static void main(String[] args){
//        /* first step */
////        String patterns = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\frequent_sequencies.txt";
////        String sequences = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\structure_sequency.txt";
////        String outPatterns = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\recovered_Patterns.txt";
////        RecoveryPattern recoveryPattern = new RecoveryPattern(patterns, outPatterns, sequences);
//        //recoveryPattern.getPatterns();

        /* second step*/
//          String sequences = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\classesStructureSequency.txt";
//          String patterns = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\recovered_Patterns.txt";
//          String outPatterns = "F:\\Projeto Mestrado - Nova Fase\\New Patterns\\patternTree.txt";
//          RecoveryPattern recoveryPattern = new RecoveryPattern(patterns, outPatterns, sequences);
//          recoveryPattern.getSkeleton();
//    }
}
