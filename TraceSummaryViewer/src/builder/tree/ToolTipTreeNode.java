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
    private boolean belongsToCluster;
    private int idNode;
    private int idFather;
    private int subNodeNumber;
    
    public ToolTipTreeNode(String node, String toolTipText, int level, LinkedList<String> parameterValues,
            boolean belongsToCluster, int idNode, int idFather, int subNodeNumber) {
        super(node, true);
        this.parameterValues = parameterValues;
        methodName = node;
        this.toolTipText = toolTipText+"\nLevel: "+level;
        this.belongsToCluster = belongsToCluster;
        this.idNode = idNode;
        this.idFather = idFather;
        this.subNodeNumber = subNodeNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getToolTipText(){
        return toolTipText;
    }

    public LinkedList<String> getParameterValues(){
        return parameterValues;
    }

    public boolean isBelongsToCluster() {
        return belongsToCluster;
    }

    public int getIdNode() {
        return idNode;
    }

    public int getIdFather() {
        return idFather;
    }

    public int getSubNodeNumber() {
        return subNodeNumber;
    }

    public void setSubNodeNumber(int subNodeNumber) {
        this.subNodeNumber = subNodeNumber;
    }

    public void setBelongsToCluster(boolean set){
        belongsToCluster = set;
    }

    public void setIdFather(int id){
        idFather = id;
    }
}
