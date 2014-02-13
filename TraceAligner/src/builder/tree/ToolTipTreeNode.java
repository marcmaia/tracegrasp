/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import java.util.LinkedList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Luciana
 */
public class ToolTipTreeNode extends DefaultMutableTreeNode{
    private String toolTipText;
    private LinkedList<String> parameterValues;
    private String methodName;
    private int paint;
    
    public ToolTipTreeNode(String node, String toolTipText, int level, LinkedList<String> parameterValues, int paint) {
        super(node, true);
        this.parameterValues = parameterValues;
        methodName = node;
        this.toolTipText = toolTipText+"\nLevel: "+level;
        this.paint = paint;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getToolTipText(){
        return toolTipText;
    }

    public int getPaint(){
        return paint;
    }

    public LinkedList<String> getParameterValues(){
        return parameterValues;
    }
}
