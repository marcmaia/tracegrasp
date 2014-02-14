/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import builder.tree.ToolTipTreeNode;
import data.mining.Cluster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import utils.MultiLineToolTip;

/**
 *
 * @author Luciana
 */
public class GraphicComponentView extends JFrame{
    //private Cluster[] clusters;
//    private JFrame jFrameGraph;
    private Map relationComponentClass;
//    private ToolTipTreeNode component;
   // private ArrayList<Integer> removedClusters;
    private JPanel jPanel;
    private int clusterNumber;
    private int classNumber;
    private Map[] clusters; // Contains the classes into of each cluster and the common classes in the last position

    public GraphicComponentView(Cluster[] selectedClusters){
        prepareClusters(selectedClusters);
        relationComponentClass = new HashMap();
        takeOffRepeatedClasses();
    }

    public boolean begin() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        JInternalFrame[] createdComponents = intersectClusters();
        clusterNumber = 0;
//        jFrameGraph = new JFrame();
        jPanel = new JPanel();
        jPanel.setAutoscrolls(true);
        jPanel.removeAll();
        for(JInternalFrame component : createdComponents){
            if(component != null) {
                this.jPanel.add(component, BorderLayout.PAGE_END);
                clusterNumber++;
            }
        }
        JButton[] classes = insertClasses();
        classNumber = 0;
        for(JButton component : classes){
            if(component != null) {
                this.jPanel.add(component, BorderLayout.CENTER);
                classNumber++;
            }
        }
        this.repaint();
        
        
//        jFrameGraph.setSize(900, 600);
//        jFrameGraph.getContentPane().add(this.jPanel, BorderLayout.CENTER);
//        jFrameGraph.setLocationRelativeTo(null);
//        jFrameGraph.setVisible(true);
        this.setSize(900, 600);
        this.getContentPane().add(this.jPanel, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        paintArc((Graphics2D)(this.jPanel.getGraphics()));
        repaint();
        return true;
    }

    @Override
     public void paint(Graphics g){
        super.paint(g);
        Component[] components = jPanel.getComponents();
        Iterator<String> iterator = relationComponentClass.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            ArrayList<Integer> clusterList = (ArrayList<Integer>)relationComponentClass.get(key);
            Rectangle source = new Rectangle();
            Rectangle end = new Rectangle();
            for(int cluster : clusterList){
                for(int index = 0; index < clusterNumber; index++){
                    if(components[index] instanceof JInternalFrame){
                        JInternalFrame clusterJInternalFrame = (JInternalFrame) components[index];
                        if((Integer.parseInt(clusterJInternalFrame.getTitle().split(" ")[2])-1) == cluster){
                            source = clusterJInternalFrame.getBounds();
                            break;
                        }
                    }
                }
            }
                for(int index = clusterNumber; index < clusterNumber + classNumber; index++){
                    if(components[index] instanceof JButton){
                        JButton clusterJButton = (JButton) components[index];
                        if(clusterJButton.getText().split(" ")[2].equals(key)){
                            end = clusterJButton.getBounds();
                            break;
                        }
                    }
                }
            g.setColor(Color.red);
            g.drawLine((int)source.getX()+((int)(source.getWidth()/2)), (int)source.getY()+(int)source.getHeight()+30, (int)end.getX()+((int)(end.getWidth()/2)), (int)end.getY()+(int)end.getHeight()+8);
            }//+(int)source.getHeight()
    }


    /* PRIVATE METHODS */

    /**
     * Intersects the clusters and inserts them in JInternalFrames
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     */
    private JInternalFrame[] intersectClusters() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
        JInternalFrame[] components = new JInternalFrame[clusters.length];
        int jInternalFrameIndex = 0;
        ArrayList<Integer> removedClusters = new ArrayList<Integer>();
        for(Map cluster : clusters){
//            coordinateClusters = new ArrayList<int[]>();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JInternalFrame jInternalFrame = new JInternalFrame();
            JScrollPane jScrollPane = new JScrollPane();
            
            if(cluster.size() > 0){
                ToolTipTreeNode root = new ToolTipTreeNode("root", "", -1, null, false, -1, -1, -1);
                createTree(cluster, null, null, root);
                    jInternalFrame.setVisible(true);
                    jInternalFrame.setMaximizable(true);
                    jInternalFrame.setResizable(true);
                    jInternalFrame.setTitle(" Component " + (jInternalFrameIndex+1) + " ");
                    jInternalFrame.setAutoscrolls(true);

                    JTree jTree = toolTipTreeNodes(root);

                    TreeUtils.expandAllTree(jTree, new TreePath(root), true);
                    jScrollPane.setViewportView(jTree);
                    jInternalFrame.getContentPane().add(jScrollPane, java.awt.BorderLayout.CENTER);
                

    //            javax.swing.GroupLayout jInternalFrameLayout = new javax.swing.GroupLayout(jInternalFrame.getContentPane());
    //            jInternalFrame.getContentPane().setLayout(jInternalFrameLayout);
    //            jInternalFrameLayout.setHorizontalGroup(
    //                jInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    //                .addGap(0, 50, Short.MAX_VALUE)
    //                .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    //            );
    //            jInternalFrameLayout.setVerticalGroup(
    //                jInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    //                .addGap(0, 60, Short.MAX_VALUE)
    //                .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    //            );


            }else {
                removedClusters.add(jInternalFrameIndex);
                jInternalFrame = null;
            }
            components[jInternalFrameIndex] = jInternalFrame;
            jInternalFrameIndex++;
        }
        preProcessingDependences(removedClusters);
        return components;
    }

    /**
     * Removes repeated classes between clusters
     */
    private void takeOffRepeatedClasses() {
        for(int outterLoop = 0; outterLoop < clusters.length; outterLoop++){
            for(int innerLoop = outterLoop + 1; innerLoop < clusters.length; innerLoop++){
                compare(clusters[outterLoop], clusters[innerLoop], innerLoop, outterLoop);
            }
        }
        relationComponentClass.remove("SpecialNode");
        Iterator<String> iterator = relationComponentClass.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            for(Map cluster : clusters){
                cluster.remove(key);
            }
        }
    }

    /**
     * Compares and removes repeatable items between two clusters
     * @param clusterA
     * @param clusterB
     */
    private void compare(Map clusterA, Map clusterB, int clusterIdentificationB, int clusterIdentificationA) {
        Iterator<String> iterator = clusterA.keySet().iterator();
        while(iterator.hasNext()){
            String keyA = iterator.next();
            if(clusterB.containsKey(keyA)) {
                clusterB.remove(keyA);
                ArrayList<Integer> cluster = new ArrayList<Integer>();
                if(!relationComponentClass.containsKey(keyA)) relationComponentClass.put(keyA, new ArrayList());
                else cluster = (ArrayList<Integer>)relationComponentClass.get(keyA);
                if(!cluster.contains(clusterIdentificationB)) cluster.add(clusterIdentificationB);
                if(!cluster.contains(clusterIdentificationA)) cluster.add(clusterIdentificationA);
                relationComponentClass.put(keyA, cluster);
            }
        }
    }


    /*** PRIVATE METHODS THAT BUILD A TREE ****/

    /**
     * Create a tree to insert into  a JIternalFrame which represents a component
     */
    private ToolTipTreeNode createSubTree(Cluster cluster){
        ArrayList<ToolTipTreeNode> nodes = cluster.getNodes();
        quicksortByLevel(nodes, 0, nodes.size()-1);
        ToolTipTreeNode root = new ToolTipTreeNode(nodes.get(0).getMethodName(),
                nodes.get(0).getToolTipText(), nodes.get(0).getLevel(), null, true,
                nodes.get(0).getIdNode(), nodes.get(0).getIdFather(), nodes.get(0).getSubNodeNumber());
        int indexNodes = 1;
        while(indexNodes < nodes.size()){
            if(nodes.get(indexNodes).getIdFather() == root.getIdNode()){
                root.add(nodes.get(indexNodes));
            }else addChildren(root, nodes.get(indexNodes));
            indexNodes++;
        }
        return root;
    }

    /**
     * Adds a child from node list of clusters
     * @param tree
     * @param node
     * @return
     */
    private boolean addChildren(ToolTipTreeNode tree, ToolTipTreeNode node) {
            for(int i = 0; i < tree.getChildCount(); i++){
                ToolTipTreeNode child = (ToolTipTreeNode)tree.getChildAt(i);
                if(child.getIdNode() == node.getIdFather()){
                    child.add(node);
                    return true;
                }
                else if(addChildren(child, node)) break;
            }
            return true;
    }


    /**
     * Sorts in crescent order considering the node level.
     * @param nodes
     * @param begin
     * @param end
     */
    private void quicksortByLevel(ArrayList<ToolTipTreeNode> nodes, int begin, int end) {
        int i = begin;
        int j = end;
        ToolTipTreeNode pivo = nodes.get((begin+end)/2);
        while(i < j) {
            while(nodes.get(i).getSubNodeNumber() > pivo.getSubNodeNumber()){
                i++;
            }
            while(nodes.get(j).getSubNodeNumber() < pivo.getSubNodeNumber()) {
                j--;
            }
            if(i <= j) {
                ToolTipTreeNode aux = nodes.get(i);
                nodes.set(i, nodes.get(j));
                nodes.set(j, aux);
                i++;
                j--;
            }
        }
        if(j>begin) quicksortByLevel(nodes, begin, j);
        if(i<end) quicksortByLevel(nodes, i, end);
    }


     private JTree toolTipTreeNodes(ToolTipTreeNode root) {
       /* Inserts the multi-line toolTip in each node */
        JTree jTreePaint = new JTree(root) {
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
                        //DefaultMutableTreeNode node = (DefaultMutableTreeNode)pValue;
                        super.getTreeCellRendererComponent(pTree, pValue, pIsSelected,
                                 pIsExpanded, pIsLeaf, pRow, pHasFocus);
                        ToolTipTreeNode node = (ToolTipTreeNode)pValue;
                        if(node.isBelongsToCluster()){
                            setBackgroundNonSelectionColor(Color.yellow);
                        }
                        else {
                            setBackgroundNonSelectionColor(Color.white);
                        }

                        return (this);
                }
            });

            return jTreePaint;
    }

     /********* NOVOS MÉTODOS ***********/

    private void prepareClusters(Cluster[] selectedClusters) {
        clusters = new Hashtable[selectedClusters.length];
        for(int i = 0; i < clusters.length; i++){
            clusters[i] = new Hashtable();
        }
        for(int indexCluster = 0; indexCluster < selectedClusters.length; indexCluster++){
            ArrayList<ToolTipTreeNode> nodes = selectedClusters[indexCluster].getNodes();
            if(nodes.size() == 0) break;
            quicksortByLevel(nodes, 0, nodes.size()-1);
            String[] method = nodes.get(0).getMethodName().split("\\.");
            clusters[indexCluster].put(method[0], new ArrayList<String>());
            for(int indexNode = 1; indexNode < nodes.size(); indexNode++){
                int i = -1;
                for(i = 0; i < indexNode; i++){
                        if(nodes.get(indexNode).getIdFather() == nodes.get(i).getIdNode()) break;
                }
                    String[] method2 = nodes.get(i).getMethodName().split("\\."); // Parent
                    ArrayList<String> classes = new ArrayList<String>();
                        if(clusters[indexCluster].containsKey(method2[0])){
                            classes = (ArrayList<String>)clusters[indexCluster].get(method2[0]);
                        }else clusters[indexCluster].put(method2[0], new ArrayList<String>());
                        method = nodes.get(indexNode).getMethodName().split("\\.");//Child
                        if(!classes.contains(method[0]) && !method[0].equals(method2[0])) {
                            classes.add(method[0]);
                            clusters[indexCluster].put(method2[0], classes);
                        }
                        if(!clusters[indexCluster].containsKey(method[0])) clusters[indexCluster].put(method[0], new ArrayList<String>());
            }
        }
    }

    private void createTree(Map cluster, ArrayList<String> adjacentVertices, String source, ToolTipTreeNode root) {
        if(adjacentVertices != null) {
            for(String end : adjacentVertices){
                if(!end.equals("SpecialNode") && !relationComponentClass.containsKey(end)){
                    boolean insert = addChildren(root, source, end, root);
                    if(!insert){
                        ToolTipTreeNode child = new ToolTipTreeNode(source, "", -1, null, false, -1, -1, -1);
                        child.add(new ToolTipTreeNode(end, "", -1, null, false, -1, -1, -1));
                        root.add(child);
                    }
                }
            }
            for(String key : adjacentVertices){
                if(!key.equals("SpecialNode")){
                    ArrayList<String> newNodes = (ArrayList<String>) cluster.get(key);
                    if(newNodes != null) {
                        cluster.remove(key);
                        createTree(cluster, newNodes, key, root);
                    }
                }
            }
        }else {
            Iterator<String> iterator = cluster.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                if(!key.equals("SpecialNode")){
                    ArrayList<String> newNodes = (ArrayList<String>) cluster.get(key);
                    if(newNodes != null) {
                        cluster.remove(key);
                        if(cluster.keySet().size() > 0){
                            iterator = cluster.keySet().iterator();
    //                        iterator = cluster.keySet().iterator();
                        }
                        createTree(cluster, newNodes, key, root);
                        iterator = cluster.keySet().iterator();
                    }
                    if(newNodes.size() == 0) {//Look for into the hash to know if this key has an edge
                        boolean exist = false;
                        Iterator<String> subSearch = cluster.keySet().iterator();
                        while(subSearch.hasNext()){
                            String subKey = subSearch.next();
                            ArrayList<String> subNodes = (ArrayList<String>) cluster.get(subKey);
                            for(String nodes : subNodes){
                                if(nodes.equals(key)){
                                    exist = true;
                                    break;
                                }
                            }
                            if(exist) break;
                        }
                        if(!exist) root.add(new ToolTipTreeNode(key, "", -1, null, false, -1, -1, -1));
                    }
                }
            }
        }
    }

    private boolean addChildren(ToolTipTreeNode tree, String source, String end, ToolTipTreeNode parent) {
        boolean insert = false;
        for(int i = 0; i < tree.getChildCount(); i++){
            ToolTipTreeNode currentNode = (ToolTipTreeNode)tree.getChildAt(i);
            if(currentNode.getMethodName().equals(source) && !parent.getMethodName().equals(end)) {
                currentNode.add(new ToolTipTreeNode(end, "", -1, null, false, -1, -1, -1));
                return true;
            }else if (currentNode.getMethodName().equals(source) && parent.getMethodName().equals(end)) {
                currentNode.setBelongsToCluster(true);
                parent.setBelongsToCluster(true);
                return true;
            }else if(addChildren(currentNode, source, end, (ToolTipTreeNode)tree.getChildAt(i))) {
                insert = true;
                break;
            }
        }
        return insert;
    }

    private JButton[] insertClasses() {
        Iterator<String> iterator = relationComponentClass.keySet().iterator();
        JButton[] classes = new JButton[relationComponentClass.size()];
        int counter = 0;
        while(iterator.hasNext()){
            String key = iterator.next();
            if(!key.equals("SpecialNode")){
                JButton node = new JButton();
                node.setBackground(new java.awt.Color(127, 194, 112));
                node.setText("  " + key + "  ");
                node.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
                node.addMouseMotionListener( new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e){
                        Component motion = e.getComponent();
                        //JButton btn = (JButton) e.getComponent();
                        int x1,y1;
                        x1 = motion.getX() + e.getX() - 15;
                        y1 = motion.getY() + e.getY() - 10;
                        motion.setLocation(x1,y1);
                        motion.getParent().repaint();
                        repaint();
                    }
                });
                classes[counter] = node;
                counter++;
            }
        }
        return classes;
    }

    private void paintArc(Graphics2D graphics2D) {
        Component[] components = jPanel.getComponents();
        Iterator<String> iterator = relationComponentClass.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            ArrayList<Integer> clusterList = (ArrayList<Integer>)relationComponentClass.get(key);
            Rectangle source = new Rectangle();
            Rectangle end = new Rectangle();
            for(int cluster : clusterList){
                for(int index = 0; index < clusterNumber; index++){
                    if(components[index] instanceof JInternalFrame){
                        JInternalFrame clusterJInternalFrame = (JInternalFrame) components[index];
                        if((Integer.parseInt(clusterJInternalFrame.getTitle().split(" ")[2])-1) == cluster){
                            source = clusterJInternalFrame.getBounds();
                            break;
                        }
                    }
                }
            }
                for(int index = clusterNumber; index < clusterNumber + classNumber; index++){
                    if(components[index] instanceof JButton){
                        JButton clusterJButton = (JButton) components[index];
                        if(clusterJButton.getText().split(" ")[2].equals(key)){
                            end = clusterJButton.getBounds();
                            break;
                        }
                    }
                }
            graphics2D.setColor(Color.red);
            graphics2D.drawLine((int)source.getX()+((int)(source.getWidth()/2)), (int)source.getY()+(int)source.getHeight()+30, (int)end.getX()+((int)(end.getWidth()/2)), (int)end.getY()+(int)end.getHeight()+8);
            }
   }

    private void preProcessingDependences(ArrayList<Integer> removedClusters) {
        Iterator<String> iterator = relationComponentClass.keySet().iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            ArrayList<Integer> clusterList = (ArrayList<Integer>)relationComponentClass.get(key);
            clusterList.removeAll(removedClusters);
            if(clusterList.size() == 0) {
                relationComponentClass.remove(key);
                iterator = relationComponentClass.keySet().iterator();
            }
            else relationComponentClass.put(key, clusterList);
        }
    }

//    private void drawEdges(JPanel jPanel, ArrayList<int[]> coordinate, Map cluster) {
//        cluster.entrySet();
//        Object[] keySet = (Object[])cluster.keySet().toArray();
//        for(int i = 0; i < keySet.length; i++){
//            ArrayList<String> adjacents = (ArrayList<String>)cluster.get(keySet[i].toString());
//            Iterator<String> iterator = adjacents.iterator();
//            while(iterator.hasNext()){
//                String endEdge = iterator.next();
//                int index = getIndex(endEdge, keySet);
//                jPanel.getGraphics().setColor(Color.red);
//                jPanel.getGraphics().drawLine(coordinate.get(i)[0], coordinate.get(i)[1],
//                        coordinate.get(index)[0], coordinate.get(index)[1]);
//
//            }
//        }
//
//       // pnlGrafo.getGraphics().drawLine(x1, y1, x2, y2);
//    }
//
//    private int getIndex(String endEdge, Object[] keySet) {
//        for(int i = 0; i < keySet.length; i++){
//            if(keySet[i].toString().equals(endEdge)) return i;
//        }
//        return -1;
//    }



}



//    private JInternalFrame[] intersectClusters() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
//
//        JInternalFrame[] components = new JInternalFrame[clusters.length];
//        int jInternalFrameIndex = 0;
//        for(Map cluster : clusters){
//            coordinateClusters = new ArrayList<int[]>();
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            Iterator<String> nodes = cluster.keySet().iterator();
//
//            JInternalFrame jInternalFrame = new JInternalFrame();
//            jInternalFrame.setVisible(true);
//            jInternalFrame.setMaximizable(true);
//            jInternalFrame.setResizable(true);
//            jInternalFrame.setTitle(" Component " + (jInternalFrameIndex+1) + " ");
//            jInternalFrame.setAutoscrolls(true);
//
//            JPanel jPanel = new JPanel();
//            jPanel.setSize(50, 100);
//            jPanel.setBackground(Color.white);
//            jPanel.setAutoscrolls(true);
//            jPanel.setVisible(true);
//            while(nodes.hasNext()){
//                int[] coordinates = new int[2]; // x1, y1, x2, y2
//                JButton node = new JButton();
//                node.setBackground(new java.awt.Color(127, 194, 112));
//                node.setText("  " + nodes.next() + "  ");
//                node.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
//                node.addMouseMotionListener( new MouseMotionAdapter() {
//                    public void mouseDragged(MouseEvent e){
//                        Component motion = e.getComponent();
//                        JButton btn = (JButton) e.getComponent();
//                        int x1,y1;
//                        x1 = motion.getX() + e.getX() - 15;
//                        y1 = motion.getY() + e.getY() - 10;
//                        motion.setLocation(x1,y1);
//                        motion.getParent().repaint();
//                        repaint();
//                    }
//                });
//                node.setBounds((int)((jPanel.getWidth()-20)*Math.random()),
//                        (int)((jPanel.getHeight()-20)*Math.random()), node.getWidth(), node.getHeight());
//                coordinates[0] = (int)node.getBounds().getX();
//                coordinates[1] = (int)node.getBounds().getY();
//                //coordinates[2] = (int)node.getBounds().getWidth();
//                //coordinates[3] = (int)node.getBounds().getHeight();
//                coordinateClusters.add(coordinates);
//                jPanel.add(node);
//
//            }
//            drawEdges(jPanel, coordinateClusters, cluster);
//            javax.swing.GroupLayout jInternalFrameLayout = new javax.swing.GroupLayout(jInternalFrame.getContentPane());
//            jInternalFrame.getContentPane().setLayout(jInternalFrameLayout);
//            jInternalFrameLayout.setHorizontalGroup(
//                jInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGap(0, 50, Short.MAX_VALUE)
//                .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            );
//            jInternalFrameLayout.setVerticalGroup(
//                jInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                .addGap(0, 60, Short.MAX_VALUE)
//                .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            );
//
//            components[jInternalFrameIndex] = jInternalFrame;
//            jInternalFrameIndex++;
//        }
//        return components;
//    }