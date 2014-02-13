/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;



/**
 *
 * @author Luciana
 */
public final class MeasureTime {
    //long t1 = System.currentTimeMillis(); usar último caso, se os grafos forem muito pequenos
    //long t2 = System.currentTimeMillis();
    //long resultado=(t2-t1)/1000;
    private static long initialTime;
    private static long finalTime;

    /**
     * Used to get the initial hour
     */
    public static void startTask(){
        initialTime = System.currentTimeMillis();
    }

    /**
     * Used to close task with final time
     */
    public static void endTask(){
        finalTime = System.currentTimeMillis();
    }

    /**
     * Shows the time which the task spent to accomplish
     */
    public static void getTime(){
        long seconds=(finalTime-initialTime)/1000;
        long hour=seconds/3600,minutes=(seconds % 3600 )/60,sec_left=(seconds % 3600 ) % 60;
        String msg = "Foi executado em ";
        if(hour > 0) System.out.println(msg + hour+" hora(s) "+minutes+" minuto(s) "+sec_left+" segundos.");
        if(minutes > 0) System.out.println(msg + minutes+" minuto(s) "+sec_left+" segundos.");
        else System.out.println(msg + seconds + " segundos.");
        System.out.println(finalTime-initialTime+"ms");
    }
}
