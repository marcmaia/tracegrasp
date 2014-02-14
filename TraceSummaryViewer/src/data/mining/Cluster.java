/*
 *  Class which mantains the nodes (classes) classified to a cluster
 */

package data.mining;

import builder.tree.ToolTipTreeNode;
import java.util.ArrayList;

/**
 *
 * @author Luciana
 */
public class Cluster {
    private ArrayList<ToolTipTreeNode> nodes;

    public Cluster(){
        nodes = new ArrayList<ToolTipTreeNode>();
    }

    public void setCluster(ArrayList<ToolTipTreeNode> node){
        nodes.addAll(node);
    }

    public ArrayList<ToolTipTreeNode> getNodes(){
        return nodes;
    }
}
