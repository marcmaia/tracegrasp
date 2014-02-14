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
public class MultiLineToolTip extends JToolTip {

      public MultiLineToolTip() {
        setUI(new MultiLineToolTipUI());
      }
}

