/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import builder.tree.ToolTipTreeNode;
import data.handler.CarryFileMemory;
import java.awt.Color;
import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 *
 * @author Luciana
 */
public class AlignmentTree extends JTree{
    private DefaultMutableTreeNode root;
    private Map tableSeq1;
    private Map tableSeq2;
    private JTree jtree;

    public void startAlignmentTree(String dir, String header) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, FileNotFoundException, IOException{
        readFiles(dir);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame();
        JScrollPane jScrollPaneTree = new JScrollPane();
        createTree(dir);
        jtree = new JTree(root);
        paintNode();
        jScrollPaneTree.setViewportView(jtree);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Aligned Sequence Result - " + header);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void generateAlignmentTree(String dir)throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, FileNotFoundException, IOException{
        readFiles(dir);
        createTree(dir);
    }

    public DefaultMutableTreeNode getDefaultMutableTreeNode(){
        return root;
    }

    private void createTree(String dir) throws FileNotFoundException, IOException {
        CarryFileMemory readSeq1 = new CarryFileMemory(dir+"\\dataAlignment.trace");
        CarryFileMemory readSeq2 = new CarryFileMemory(dir+"\\dataAlignment2.trace");
        String[] seq1 = readSeq1.carryCompleteFile();
        String[] seq2 = readSeq2.carryCompleteFile();
        int i=0;
        int j=0;
        int count = 0;
        this.root = new DefaultMutableTreeNode();
        DefaultMutableTreeNode node = null;
        int numberOfMisalignmentBlocks = 0;
        while(i<seq1.length || i<seq2.length){
            if(seq1.length > i && seq1[i].equals("0")){
                //Create a shift node seq1
                if(seq1.length > j && seq1[i].equals("0")) node = new DefaultMutableTreeNode("Shifted Sequence1", true);
                j = i;
                while(seq1.length > j && seq1[j].equals("0")){
                    node.add(new DefaultMutableTreeNode(tableSeq2.get(seq2[j])));
                    j++;
                }
                count++;
                i=j;
                numberOfMisalignmentBlocks++;
            }else if(seq2.length > i && seq2[i].equals("0")){
                //Creates a shift node seq2
                node = new DefaultMutableTreeNode("Shifted Sequence2", true);
                j = i;
                while(seq2.length > j && seq2[j].equals("0")){
                    node.add(new DefaultMutableTreeNode(tableSeq1.get(seq1[j])));
                    j++;
                }
                i=j;
                numberOfMisalignmentBlocks++;
            }else if(seq1.length > i && seq2.length > i){
                //Creates a simple node
                    node = new DefaultMutableTreeNode("Aligned Sequences", true);
                    while(seq2.length > i && seq1.length > i && !seq1[i].equals("0") && !seq2[i].equals("0")){
                        node.add(new DefaultMutableTreeNode(tableSeq2.get(seq2[i])));
                        i++;
                    }
            }
            root.add(node);
        }
        readSeq1 = null;
        readSeq2 = null;
        System.out.println("Número de blocos desalinhados: "+numberOfMisalignmentBlocks);
    }

    private void readFiles(String root) throws FileNotFoundException, IOException {
        tableSeq1 = new HashMap();
        tableSeq2 = new HashMap();
        CarryFileMemory readAlignSeq1 = new CarryFileMemory(root+"\\dataBeforeAlignment.trace");
        CarryFileMemory readAlignSeq2 = new CarryFileMemory(root+"\\dataBeforeAlignment2.trace");
        String[] alignSeq1 = readAlignSeq1.carryCompleteFile();
        String[] alignSeq2 = readAlignSeq2.carryCompleteFile();
        for(String line : alignSeq1){
            String[] splitLine = line.trim().split(" ");
            tableSeq1.put(splitLine[0], splitLine[1]);
        }
        for(String line : alignSeq2){
            String[] splitLine = line.trim().split(" ");
            tableSeq2.put(splitLine[0], splitLine[1]);
        }
    }

    public void paintNode(){
        /* Paints the nodes at different colors */
            jtree.setCellRenderer(new DefaultTreeCellRenderer(){
                 public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pIsSelected, boolean pIsExpanded,
                     boolean pIsLeaf, int pRow, boolean pHasFocus) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)pValue;
                        super.getTreeCellRendererComponent(pTree, pValue, pIsSelected,
                                 pIsExpanded, pIsLeaf, pRow, pHasFocus);
                        //node.getUserObject().toString();
                        if(node.getUserObject() != null){
                            if(node.getUserObject().toString().startsWith("Shifted") && node.getUserObject().toString().endsWith("2")) {
                                setBackgroundNonSelectionColor(Color.yellow);
                            }
                            else if(node.getUserObject().toString().startsWith("Shifted") && node.getUserObject().toString().endsWith("1")) {
                                setBackgroundNonSelectionColor(Color.red);
                            }
                            else {
                                setBackgroundNonSelectionColor(Color.white);
                            }
                        }
                        return (this);
                }
            });
    }
    
}
