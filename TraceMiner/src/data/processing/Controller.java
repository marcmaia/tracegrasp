/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.processing;

import data.handler.CarryFileMemory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.devlib.schmidt.CopyFile;
import tracecompressor.Compressor;
import tracemetrics.MetricsReporter;
import utils.MeasureTime;
/**
 *
 * @author Luciana
 */
public class Controller{
    private StringBuilder builder;
    private int metricsInfo;
    private String dir;
    private CarryFileMemory carryFileMemory;
    private String filePath;
    private long[] time;
    private String[] featuresPath;
    private int step;
    private int option;
    private String path;
    private JPanel panel;
    private JLabel label;
    private boolean done;

    public Controller(String metrics, String dir)throws NumberFormatException{
        builder = new StringBuilder();
        this.dir = dir;
        if(!metrics.equals("")) this.metricsInfo = Integer.parseInt(metrics);
    }

    public Controller(String carryFileMemory){
        this.carryFileMemory = new CarryFileMemory(carryFileMemory);
        this.filePath = carryFileMemory;
    }

    public Controller() {}
   
    public StringBuilder getMessages(){
        return this.builder;
    }

    public void initialiseMessages(){
        this.builder = new StringBuilder();
    }

    public void setDirectory(String dir){
        this.dir = dir;
    }

    public void readFeatureMarkFile() throws FileNotFoundException, IOException, SecurityException {
        String[] fileContent = carryFileMemory.carryCompleteFile();
        File file = new File(filePath);
        if(!file.getParent().startsWith("/")) this.dir = "\\";
        else this.dir = "/";
        File createFeaturePath = new File(file.getParentFile().getParent()+ dir + "Functionalities");
        createFeaturePath.mkdir();
        int i=0;
        int j=0;
        if(fileContent.length%2 == 0){
            featuresPath = new String[fileContent.length/2];
            time = new long[fileContent.length];
        }
        else {
            featuresPath = new String[(fileContent.length/2)+1];
            time = new long[fileContent.length+1];
            time[fileContent.length] = Long.parseLong(((fileContent[fileContent.length-1].split(","))[2].split("\r")[0]));
        }
        for(String line : fileContent){
            String[] rows = line.split(",");
            if(i%2==0){
                File createFeatureDirectory = new File(createFeaturePath.getAbsolutePath()+dir+rows[1]+ dir + file.getParentFile().getName());
                createFeatureDirectory.mkdirs();
                featuresPath[j] = createFeatureDirectory.getAbsolutePath();
                j++;
            }
            time[i] = Long.parseLong(rows[2].split("\r")[0]);
            i++;
        }
    }

    public void splitFeatures() throws IOException, Error {
        int countFeatureTime = 0;
        for(String feature : featuresPath){
            copyFeatureThread(feature, countFeatureTime);
            countFeatureTime = countFeatureTime+2;
        }
    }

    private void copyFeatureThread(String feature, int countFeatureTime) throws IOException, Error{
        File files = new File(filePath);
        if(files.isFile()) files = files.getParentFile();
        for(File file : files.listFiles()){
            if(file.isDirectory()){
                File newFile = new File(feature+dir+file.getName());
                newFile.mkdir();
                transferData(file, newFile, countFeatureTime);
            }
        }
    }

    private void transferData(File sourceFile, File destFile, int countFeatureTime) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(destFile.getAbsolutePath()+dir+"data.trace"));
        StringBuilder buildFile = new StringBuilder();
        CarryFileMemory readToMemory = new CarryFileMemory(sourceFile.getAbsolutePath()+dir+"data.trace");
        String[] fileContent = readToMemory.carryCompleteFile();
        if(fileContent.length>1){
           for(String row : fileContent){
               if(!row.startsWith("<SEQ_")){
                    if(Long.parseLong(row.split(",")[4]) >= time[countFeatureTime] && Long.parseLong(row.split(",")[4]) <= time[countFeatureTime+1]){
                       buildFile.append(row+"\n");
                    }
               }
           }
        }
        if(buildFile.length()> 0){
            writer.write(buildFile.toString());
            writer.flush();
            writer.close();
        }else{
            writer.close();
            removeTrace(destFile.getAbsolutePath());
        }
    }

    public void setStep1(int option, String path, JPanel panel, JLabel label){
        this.option = option;
        this.step = 1;
        this.path = path;
        this.panel = panel;
        this.label = label;
        done = false;
    }
    public File step1(int step1, String path, JPanel panel, JLabel label) throws IOException {
   // public File step1() throws IOException {
        if(step1==0) {
            MeasureTime.startTask();
            Compressor compressor = new Compressor();
            builder.append(compressor.buildCompressedFromTraceDir(new File(path), dir, panel, label));
            MeasureTime.endTask();
            MeasureTime.getTime();
            return compressor.getCompressedFile();
        }else{
            MeasureTime.startTask();
            MetricsReporter metrics = new MetricsReporter(metricsInfo);
            builder.append(metrics.buildReportFromTraceDir(new File(path), dir, panel, label));
            MeasureTime.endTask();
            MeasureTime.getTime();
            return metrics.getMetricsFile();
        }
    }

    public File step2(int step, File newFilePath, JPanel panel, JLabel label) throws IOException {
        if(step == 1){
            MeasureTime.startTask();
            MetricsReporter metrics = new MetricsReporter(metricsInfo);
            builder.append(metrics.buildReportFromTraceDir(newFilePath, dir, panel, label));
            MeasureTime.endTask();
            MeasureTime.getTime();
            return metrics.getMetricsFile();
        }else{
            Compressor compressor = new Compressor();
            builder.append(compressor.buildCompressedFromTraceDir(newFilePath, dir, panel, label));
            return compressor.getCompressedFile();
        }
    }

    public File step3(int step, File newFilePathStep2, JPanel panel, JLabel label) throws IOException {
        if(step == 1){
            MeasureTime.startTask();
            Compressor compressor = new Compressor();
            builder.append(compressor.buildCompressedFromTraceDir(newFilePathStep2, dir, panel, label));
            MeasureTime.endTask();
            MeasureTime.getTime();
            return new File(compressor.getCompressedFile().getAbsolutePath()+dir+"Thread"+dir+"data.trace");
        }else{
            MetricsReporter metrics = new MetricsReporter(metricsInfo);
            builder.append(metrics.buildReportFromTraceDir(newFilePathStep2, dir, panel, label));
            return metrics.getMetricsFile();
        }
    }

   public String alterTraceRepository(String trace) throws IOException{
       File file = new File(trace);
       String nameFile = file.getName();
//       if(!file.getParentFile().getName().equals(nameFile.split("-")[0])){
       if(!file.getName().endsWith("Compressed") && !file.getName().endsWith("Calls") && !file.getName().endsWith("Raw")){
           File newName = new File(file.getAbsolutePath()+dir+nameFile+"-Raw");
           newName.mkdir();
           trace = newName.getAbsolutePath();
                for(File transferFile : file.listFiles()){
                    if(!newName.getAbsolutePath().equals(transferFile.getAbsolutePath())){
                        if(transferFile.isFile()) CopyFile.copyFile(transferFile, new File(newName.getAbsolutePath(), transferFile.getName()));
                        else if(transferFile.isDirectory()){
                            File newDiretory = new File(newName.getAbsolutePath()+dir+transferFile.getName());
                            newDiretory.mkdir();
                            for(File data : transferFile.listFiles()){
                                CopyFile.copyFile(data, new File(newDiretory.getAbsolutePath(), data.getName()));
                                data.delete();
                            }
                        }
                        transferFile.delete();
                    }
                }
       }
       return trace;
   }

   /**
    * Removes a required trace/traces
    * @param trace
    * @return       true if the trace(s) has(have) been removed./False if it failed.
    */
   public static boolean removeTrace(String trace) throws SecurityException {
       File files = new File(trace);
       for(File file : files.listFiles()){
            if(file.isDirectory()) removeTrace(file.getAbsolutePath());
            file.delete();
       }
       files.delete();
       if(files.exists()) return false;
       else return true;
   }

   private void copy(File origem,File destino,boolean overwrite) throws IOException{
      if (destino.exists() && !overwrite){
         System.err.println(destino.getName()+" já existe, ignorando...");
         return;
      }
      FileInputStream   fisOrigem = new FileInputStream(origem);
      FileOutputStream fisDestino = new FileOutputStream(destino);
      FileChannel fcOrigem = fisOrigem.getChannel();
      FileChannel fcDestino = fisDestino.getChannel();
      fcOrigem.transferTo(0, fcOrigem.size(), fcDestino);
      fisOrigem.close();
      fisDestino.close();
   }

   public boolean getDone(){
       return done;
   }

//    public void run(){
//        if(step == 1) try {
//            step1();
//            done = true;
//        } catch (IOException ex) {
//            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        done = false;
//
//
//    }

}
