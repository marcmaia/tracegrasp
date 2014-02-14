/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processing;

import data.handler.CarryFileMemory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author luciana.lourdes
 */
public class MarkBorder {

    private StringBuilder builder = new StringBuilder();
    private String directory;

    public MarkBorder(String directorySO) {
        directory = directorySO;
    }


    public String[] delimitBorders(String filePath, String sequenceFilePath) throws FileNotFoundException, IOException {
        insertSequencePatterns(filePath, sequenceFilePath);
        return null;
    }

     private void insertSequencePatterns(String filePath, String sequenceFilePath) throws FileNotFoundException, IOException {
        File[] traceThreadDirs = getThreads(filePath);
        File[] sequenceThreadDirs = getFiles(sequenceFilePath);
        insertMarks(traceThreadDirs, sequenceThreadDirs);
    }



    private File[] getThreads(String ThreadPath) {
        return new File(ThreadPath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() && file.getName().startsWith("Thread");
            }
        });
    }

    private File[] getFiles(String files){
        return new File(files).listFiles(new FileFilter(){
           public boolean accept(File file){
               return file.isFile();
           }
        });
    }

    private void insertMarks(File[] traceThreadDirs, File[] sequenceThreadDirs) throws FileNotFoundException, IOException{
         for (int i = 0; i < traceThreadDirs.length; i++) {
		File trace_file = new File(traceThreadDirs[i], "data.trace");
		if (trace_file.exists()) {
                    File temp_output_dir = new File(traceThreadDirs[i].getAbsolutePath()+directory+"marked"+sequenceThreadDirs[0].getName().substring(9));
                    buildMarksFrom(trace_file, temp_output_dir, sequenceThreadDirs[i].getPath());
		} else {
                  builder.append("\nTrace file not found in: \n").append(traceThreadDirs[i].getPath());
		}

        }
    }

    private void buildMarksFrom(File trace_file, File new_output_dir, String sequenceFile) throws FileNotFoundException, IOException {
        java.util.Formatter newTraceFile = new java.util.Formatter(new_output_dir);
        String[] readSequenceFile = new CarryFileMemory(sequenceFile).carryCompleteFile();
        pegaTamanho(readSequenceFile);
        String[] readerTrace = new CarryFileMemory(trace_file.toString()).carryCompleteFile();
        HashMap trace = new HashMap();
        for(String sequence : readSequenceFile){
            String[] items = sequence.split(",");
            int i = 0;
            int index = -1;
            while(index < readerTrace.length){
                ArrayList<Integer> newTrace = new ArrayList<Integer>();
                while(i < items.length && (-1 != (index = indexOf(items[i], readerTrace)))){
                    if(newTrace.size() > 0 && index > newTrace.get(newTrace.size()-1)){
                        newTrace.add(index);
                        i++;
                    }else break;
                }
                if(i == items.length){
                    trace.put(sequence, newTrace);

                }
            }
            System.out.print("oi");
        }
        System.out.print("oi");
    }

    private int indexOf(String item, String[] readerTrace) {
        for(int i = 0; i < readerTrace.length; i++){
            if(!readerTrace[i].startsWith("<SEQ")){
                String[] vetLine = readerTrace[i].split(",");
                String temp = vetLine[0]+"."+vetLine[1];
                if(item.equals(temp)) return i;
            }
        }
        return -1;
    }

    private int indexOf(int tam, String[] readerTrace, ArrayList<String> sequences) {
        for(int i = 0; i < readerTrace.length; i++){
            if(readerTrace[i].length() == tam && !sequences.contains(readerTrace[i])){
               return i;
            }
        }
        return -1;
    }

    private void pegaTamanho(String[] readSequenceFile) {
        int[] tam = new int[readSequenceFile.length];
        for(int i = 0; i < readSequenceFile.length; i++){
            tam[i] = readSequenceFile[i].split(",").length;
        }
        ArrayList<String> sequences = new ArrayList<String>();
        Arrays.sort(tam, 0, tam.length);
        int k = tam[tam.length-1];
        int j = indexOf(k, readSequenceFile, sequences);
        for(int i = 0 ; i < readSequenceFile.length; i++){
            while(j < readSequenceFile.length){
                if(tam[i] < tam[j]){
                    boolean contain = contains(readSequenceFile[i], readSequenceFile[j]);
                    if(contain && !sequences.contains(readSequenceFile[j])){
                        sequences.add(readSequenceFile[j]);
                    }
                }else j++;
            }
        }
    }

    private boolean contains(String string, String string0) {
        String[] item = string.split(",");
        String[] item1 = string0.split(",");
        int i = 0;
        int k = 0;
        while(i < item.length && k < item1.length){
            if(item[i].equals(item1[k])){
                i++;
            }
            k++;
        }
        if(i == item.length) return true;
        return false;
    }

}
