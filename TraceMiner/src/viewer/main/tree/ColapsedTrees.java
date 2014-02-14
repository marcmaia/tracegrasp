/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import builder.tree.Node;
import builder.tree.Tree;
import data.handler.CarryFileMemory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Luciana
 */
public class ColapsedTrees {
    private DefaultMutableTreeNode alignedTree;
    private CarryFileMemory sequence1_Trace;
    private CarryFileMemory sequence2_Trace;
    private LinkedList<Node> root;

    public ColapsedTrees(DefaultMutableTreeNode root, String sequence1_Trace, String sequence2_Trace) {
        this.alignedTree = root;
        this.sequence1_Trace =  new CarryFileMemory(sequence1_Trace+"\\data.trace");
        this.sequence2_Trace = new CarryFileMemory(sequence2_Trace+"\\data.trace");
    }

    public void colapseTraces() throws FileNotFoundException, IOException{
        readSequenceTraces();
    }

    /**
     * Through the alignment obtained from sequence alignment algorithm
     * is create a colapsed tree.
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void readSequenceTraces() throws FileNotFoundException, IOException {
        String[] sequence1 = sequence1_Trace.carryCompleteFile();
        String[] sequence2 = sequence2_Trace.carryCompleteFile();
        int sequence1_Line = 0;
        int sequence2_Line = 0;
        int currentLineFile = 0;
        int childCount = 0;
        Node nodeRoot = new Node();
        nodeRoot.setLevel(1);
        for(Enumeration e = alignedTree.children(); e.hasMoreElements();){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
            childCount = child.getChildCount();
            if(child.getUserObject().toString().equals("Aligned Sequences")){
                currentLineFile = analyzeTree(childCount, currentLineFile, nodeRoot, sequence1);
                sequence1_Line = sequence2_Line = currentLineFile;
            }else if(child.getUserObject().toString().equals("Shifted Sequence 1")){
                sequence2_Line = analyzeTree(childCount, sequence2_Line, nodeRoot, sequence2);
            }else{
                sequence1_Line = analyzeTree(childCount, sequence1_Line, nodeRoot, sequence1);
            }
        }
    }

    /**
     * Analyzes the current tree node and decides if insert a new parent or
     * just a new child.
     * 
     * @param childCount
     * @param currentLine
     * @param nodeRoot
     * @param sequence
     * @return
     */
    private int analyzeTree(int childCount, int currentLine, Node nodeRoot, String[] sequence){
        while(childCount > 0){
            String[] line = sequence[currentLine].split(",");
            if(!line[0].startsWith("<SEQ_")){
                prepareNode(line, nodeRoot);
                childCount--;
            }
            currentLine++;
        }
        return currentLine;
    }

    /**
     * Method used to insert a new node in the tree
     * @param line
     * @param nodeRoot
     */
    private void prepareNode(String[] line, Node nodeRoot){
        Tree tempTree = new Tree();
        if(root.size() >0){
            Node node = new Node();
            node.setLabel(line[1]);
            node.setLevel(Integer.parseInt(line[2]));
            node.setPackageName(line[0]);
            if(node.getLevel() == 1){
                nodeRoot = null;
                nodeRoot = node;
                root.add(nodeRoot);
            }else if(nodeRoot.getLevel()+1 == node.getLevel()){
                nodeRoot.increaseSubNodes();
                nodeRoot.addChild(node);
            }else{
                tempTree.insertFurtherNodes(nodeRoot, node);
            }
        }else{
           nodeRoot = tempTree.insertFirstNodeTrace(line);
           root.add(nodeRoot);
        }

         
    }

    //                for(Enumeration enume = child.children(); e.hasMoreElements();){
//                    DefaultMutableTreeNode subChild = (DefaultMutableTreeNode) enume.nextElement();
//                    String[] line = sequence1[sequence1_Line].split(",");
//                    node.setLabel(line[1]);
//                    node.setLevel(Integer.parseInt(line[2]));
//                    node.setPackageName(line[0]);
//                }


}
