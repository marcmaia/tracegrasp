/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Luciana
 */
public class PaintMultipleTreeNode extends DefaultMutableTreeNode{
    private String node;
    private boolean selected;

    public PaintMultipleTreeNode(String nodeName, boolean selected){
        super(nodeName, true);
        node = nodeName;
        this.selected = selected;
    }

    public void setSelected(boolean status){
        selected = status;
    }

    public boolean getSelected(){
        return selected;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

}
