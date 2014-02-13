/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import builder.tree.Node;
import builder.tree.ToolTipTreeNode;
import builder.tree.Tree;
import data.handler.CarryFileMemory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import utils.MultiLineToolTip;

/**
 *
 * @author Luciana
 */
public class ColapsedTrees {
    private DefaultMutableTreeNode alignedTree;
    private CarryFileMemory sequence1_Trace;
    private CarryFileMemory sequence2_Trace;
    private LinkedList<Node> root;
    private ToolTipTreeNode tree;
    private JTree jTree;

    public ColapsedTrees(DefaultMutableTreeNode root, String sequence1_Trace, String sequence2_Trace) {
        this.alignedTree = root;
        this.sequence1_Trace =  new CarryFileMemory(sequence1_Trace+"\\data.trace");
        this.sequence2_Trace = new CarryFileMemory(sequence2_Trace+"\\data.trace");
        this.root = new LinkedList<Node>();
    }

    public void colapseTraces() throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            /*Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout(2, 1));*/
            JScrollPane jScrollPaneTree = new JScrollPane();
            JScrollPane jScrollPaneArgs = new JScrollPane();
            JPanel jPanelTree = new JPanel();
        //readSequenceTraces();TODO: voltar
            buildColapsedTree();
        createJTree();
        jTree = paintTreeNodes();
        TreePath path = new TreePath(tree);
        TreeUtils.expandAllTree(jTree, path, true);
        jScrollPaneTree.setViewportView(jTree);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
            frame.getContentPane().add(jPanelTree, BorderLayout.PAGE_END);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null);
            //frame.setTitle(absolutPath);
            frame.setVisible(true);
        //System.out.println("oi");
    }

    private void createJTree() {
       ToolTipTreeNode[] nodes = new ToolTipTreeNode[root.size()];
       tree = new ToolTipTreeNode("root", "", 0, null, 0);
       ToolTipTreeNode nodeTree;
       Iterator<Node> iterator = root.iterator();
       int index = 0;
       while(iterator.hasNext()){
            Node subTree = iterator.next();
            nodes[index] = new ToolTipTreeNode(subTree.getLabel(), subTree.getPackageName(), subTree.getLevel(), subTree.getParameterValues(), subTree.getShiftedSequence());
            tree.add(nodes[index]);
           //LinkedList<String> checkedClusters = checkLevel(subTree.getChildren());
//           if(checkedClusters != null){
                for(int i=0; i<subTree.getChildCount(); i++){
                   Node node = subTree.getChildAt(i);
//                   if(checkedClusters.contains(node.getLabel())){
                       nodeTree = new ToolTipTreeNode(node.getLabel(), node.getPackageName(), node.getLevel(), node.getParameterValues(), node.getShiftedSequence());
                       nodes[index].add(nodeTree);
                       if(node.hasChildren()) addChildren(nodeTree, node.getChildren());
//                   }
               }
//           }
           index++;
       }
   }

       private static void addChildren(ToolTipTreeNode jTreeNode, LinkedList<Node> nodeList) {
           for(int i=0; i<nodeList.size(); i++){
                Node child = nodeList.get(i);
                    ToolTipTreeNode newChild = new ToolTipTreeNode(child.getLabel(), child.getPackageName(), child.getLevel(), child.getParameterValues(), child.getShiftedSequence());
                    if(child.hasChildren()) {
                        addChildren(newChild, child.getChildren());
                    }
                    jTreeNode.add(newChild);
            }
       }

    private JTree paintTreeNodes() {
       /* Inserts the multi-line toolTip in each node */
        JTree jTreePaint = new JTree(tree) {
                public JToolTip createToolTip() { /* multi-line toolTip*/
                    MultiLineToolTip tip = new MultiLineToolTip();
                    tip.setComponent(this);
                    return tip;
                  }

                public String getToolTipText(MouseEvent evt) {/* toolTips */
                    if (getRowForLocation(evt.getX(), evt.getY()) == -1)
                      return null;
                    TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
                    return ((ToolTipTreeNode) curPath.getLastPathComponent()).getToolTipText();
                  }
                };
            jTreePaint.setToolTipText("");

            /* Paints the nodes at different colors */
            jTreePaint.setCellRenderer(new DefaultTreeCellRenderer(){
                 public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pIsSelected, boolean pIsExpanded,
                     boolean pIsLeaf, int pRow, boolean pHasFocus) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)pValue;
                        super.getTreeCellRendererComponent(pTree, pValue, pIsSelected,
                                 pIsExpanded, pIsLeaf, pRow, pHasFocus);
                        node.getUserObject().toString();
                        //repeatedTimes = getTimes(node.getUserObject().toString());
                        if(((ToolTipTreeNode)node).getPaint() == 2) {
                            setBackgroundNonSelectionColor(Color.yellow);
                        }
                        else if(((ToolTipTreeNode)node).getPaint() == 1) {
                            setBackgroundNonSelectionColor(Color.red);
                        }
                        else {
                            setBackgroundNonSelectionColor(Color.white);
                        }
                        return (this);
                }
            });
            return jTreePaint;
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
        sequence1 = removeNoise(sequence1);
        sequence2 = removeNoise(sequence2);
        int sequence1_Line = 0;
        int sequence2_Line = 0;
        int childCount = 0;
        Node nodeRoot = new Node();
        nodeRoot.setLevel(1);
        for(Enumeration e = alignedTree.children(); e.hasMoreElements();){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
            childCount = child.getChildCount();
            if(child.getUserObject().toString().equals("Aligned Sequences")){
                sequence1_Line = analyzeTree(childCount, sequence1_Line, nodeRoot, sequence1, -1);
                sequence2_Line = sequence2_Line + childCount;
            }else if(child.getUserObject().toString().equals("Shifted Sequence1")){
                if(root.size() > 0) nodeRoot = root.get(root.size()-1);
                sequence2_Line = analyzeTree(childCount, sequence2_Line, nodeRoot, sequence2, 1);
            }else {
                if(root.size() > 0) nodeRoot = root.get(root.size()-1);
                sequence1_Line = analyzeTree(childCount, sequence1_Line, nodeRoot, sequence1, 2);
            }
        }
    }
    
    private void buildColapsedTree()throws FileNotFoundException, IOException{
        String[] sequence1 = sequence1_Trace.carryCompleteFile();
        String[] sequence2 = sequence2_Trace.carryCompleteFile();
        sequence1 = removeNoise(sequence1);
        sequence2 = removeNoise(sequence2);
        int sequence1_Line = 0;
        int sequence2_Line = 0;
        int childCount = 0;
        Node nodeRoot = new Node();
        nodeRoot.setLevel(1);
        for(Enumeration e = alignedTree.children(); e.hasMoreElements();){
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
            childCount = child.getChildCount();
            if(child.getUserObject().toString().equals("Aligned Sequences")){
                sequence1_Line = analyzeTree(childCount, sequence1_Line, nodeRoot, sequence1, -1);
                sequence2_Line = sequence2_Line + childCount;
            }else if(child.getUserObject().toString().equals("Shifted Sequence1")){
                if(root.size() > 0) nodeRoot = root.get(root.size()-1);
                sequence2_Line = analyzeTree(childCount, sequence2_Line, nodeRoot, sequence2, 1);
            }
            else if(child.getUserObject().toString().equals("Shifted Sequence2")){
                if(root.size() > 0) nodeRoot = root.get(root.size()-1);
                sequence1_Line = analyzeTree(childCount, sequence1_Line, nodeRoot, sequence1, 2);
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
    private int analyzeTree(int childCount, int currentLine, Node nodeRoot, String[] sequence, int shiftedSequence){
        while(childCount > 0){
            if(!sequence[currentLine].startsWith("<SEQ_")){
                if(root.size() > 0) {
                    nodeRoot = root.get(root.size()-1);
                    if(nodeRoot.getShiftedSequence() != shiftedSequence && nodeRoot.getShiftedSequence() != -1 && shiftedSequence != -1) {
                        nodeRoot = new Node();
                        createSpecialNode(nodeRoot, 1);
                    }
                }
                prepareNode(sequence[currentLine], nodeRoot, shiftedSequence);
                //nodeRoot = root.get(root.size()-1); removido 17/04/2011
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
    private void prepareNode(String line, Node nodeRoot, int sequence){
        Tree tempTree = new Tree();
        if(root.size() >0){
            String[] newLine = line.split(",");
            Node node = new Node();
            node.setLabel(tempTree.getClass(newLine[0]) + "." + newLine[1]);
            node.setLevel(Integer.parseInt(newLine[2]));
            node.setPackageName(newLine[0]);
            node.setShiftedSequence(sequence);
            if(node.getLabel().equals("FigTextGroup.firePropChange"))//FigMNode.redraw
                {
                    int count = 1;
                }
            if(node.getLevel() == 1){
                nodeRoot = null;
                nodeRoot = node;
                root.add(nodeRoot);
            }else if(nodeRoot.getLevel()+1 == node.getLevel()){
               if((nodeRoot.getShiftedSequence() != -1) && (nodeRoot.getShiftedSequence() != node.getShiftedSequence()) && (sequence != -1) ) // Inseri esta linha remover se caso der errado. 17/047/2011
                {
                    Node node1 = new Node();
                    createSpecialNode(node1, Integer.parseInt(newLine[2])-1);

/*                    node1.setLabel("Special Node");
                    node1.setLevel(Integer.parseInt(newLine[2])-1);
                    node1.setPackageName("");
                    node1.setShiftedSequence(-1);*/
                    node1.addChild(node);
                    root.add(node1);
                }else {
                    nodeRoot.increaseSubNodes();
                    nodeRoot.addChild(node);
                }
            }else{
                tempTree.insertFurtherNodes(nodeRoot, node, sequence);
            }
        }else{
           String[] tempLine = new String[1];
           tempLine[0] = line;
           nodeRoot = tempTree.insertFirstNodeTrace(tempLine);
//           nodeRoot.setLabel(temp.getLabel());
//           nodeRoot.setLevel(temp.getLevel());
//           nodeRoot.setPackageName(temp.getPackageName());
//           nodeRoot.setParameterValues(temp.getParameterValues().toString());
//           nodeRoot.setSubNodeNumber(temp.getSubNodeNumber());
//           nodeRoot.setShiftedSequence(temp.getShiftedSequence());
           root.add(nodeRoot);
        }
         
    }

    private String[] removeNoise(String[] sequence) {
        String[] tempSequence = new String[sequence.length];
        int index = 0;
        for(String line : sequence){
            if(!line.startsWith("<SEQ")){
                tempSequence[index] = line;
                index++;
            }
        }
        return tempSequence;
    }

    private void createSpecialNode(Node node1, int level) {
        node1.setLabel("Special Node");
        node1.setLevel(level);
        node1.setPackageName("");
        node1.setShiftedSequence(-1);
    }

    //                for(Enumeration enume = child.children(); e.hasMoreElements();){
//                    DefaultMutableTreeNode subChild = (DefaultMutableTreeNode) enume.nextElement();
//                    String[] line = sequence1[sequence1_Line].split(",");
//                    node.setLabel(line[1]);
//                    node.setLevel(Integer.parseInt(line[2]));
//                    node.setPackageName(line[0]);
//                }


}
