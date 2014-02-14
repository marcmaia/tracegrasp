/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import javax.swing.JToolTip;

/**
 *
 * @author Luciana
 */
public final class Util {


    public static boolean isDigit(char c){
        try{
            Integer.parseInt(String.valueOf(c));
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }
}



