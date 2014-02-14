/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;



import builder.tree.Tree;
import builder.tree.ToolTipTreeNode;
import builder.tree.Node;
import data.mining.Cluster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import utils.MultiLineToolTip;
import utils.Util;

/**
 *
 * @author Luciana
 */
public class TreeDisplay extends JTree {
    private Tree tree;
    private DefaultTreeModel treemodel;// = new DefaultTreeModel(root);
    private ToolTipTreeNode root;
    private JTree jTree;
    private DefaultTreeModel model;
    private File filePath;
    private int independentClusterNumber;
    private int nodesNumber;
    private double allowedStandardDeviation;
    private String logger;
    private JTextPane jTextPaneArgs;
    private JInternalFrame jInternalFrameTree;
    private Cluster[] bestClusters;
    private ArrayList<ToolTipTreeNode> applicantsClusters;
    private ArrayList<ToolTipTreeNode> amalgamateClasses;
    private ArrayList<ToolTipTreeNode> selectedNodes; // contains the nodes wich is in the selected clusters
    /**
     *
     * @param root
     */
    public TreeDisplay(JTree root) {
//       treemodel = new DefaultTreeModel(root);
       setAutoscrolls(true);
       setModel(treemodel);
       setRootVisible(true);
       setShowsRootHandles(false);//to show the root icon
       getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //set single selection for the Tree
       setEditable(false);
    }

    public JInternalFrame getTree(){
        return jInternalFrameTree;
    }

    public Cluster[] getBestClusters(){
        return bestClusters;
    }

    public ArrayList<ToolTipTreeNode> getAmalgamateClasses(){
        return amalgamateClasses;
    }
    /**
    *   Displays the tree graphically
    * @param buildedTree
    * @param clusterNum
    * @param callNum
    * @param levelNum
    */
   public boolean begin(LinkedList<Node> buildedTree, String absolutPath, String dir, int numberOfNodes) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
            boolean updated = false;
            filePath = new File(absolutPath);
//            this.callNum = callNum;
//            this.levelNum = levelNum;
//            this.clusterNum = clusterNum;
//            this. dir = dir;
            this.nodesNumber = numberOfNodes;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //JFrame frame = new JFrame(); TODO: voltar
            jInternalFrameTree = new JInternalFrame();
            
            //jInternalFrameTree.setVisible(true);
            /*Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout(2, 1));*/
            JScrollPane jScrollPaneTree = new JScrollPane();
            JScrollPane jScrollPaneArgs = new JScrollPane();
            JPanel jPanelTree = new JPanel();
            jTextPaneArgs = new JTextPane();

            applicantsClusters = createJTree(buildedTree);
            classifyClusters();
            jTree = paintTreeNodes();
            model = (DefaultTreeModel) jTree.getModel();
            jTree.addMouseListener(new MouseAdapter(){            
            public void mouseClicked(MouseEvent event){
                    doMouseClicked(event);
                }
            });
            TreeUtils.expandAllTree(jTree, new TreePath(root), true);
            if(logger != null){
                updated = true;
            }
            //contentPane.add(new JScrollPane(jTree));
            jScrollPaneTree.setViewportView(jTree);
            //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            jInternalFrameTree.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
            jInternalFrameTree.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
            jPanelTree.setBorder(javax.swing.BorderFactory.createTitledBorder(" Parameter Values "));
            jScrollPaneArgs.setViewportView(jTextPaneArgs);
            javax.swing.GroupLayout jPanelLayoutTree = new GroupLayout(jPanelTree);
            jPanelTree.setLayout(jPanelLayoutTree);
            jPanelLayoutTree.setHorizontalGroup(jPanelLayoutTree.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneArgs, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE));
            jPanelLayoutTree.setVerticalGroup(jPanelLayoutTree.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneArgs, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE));
//            frame.getContentPane().add(jPanelTree, BorderLayout.PAGE_END);
//            frame.setSize(800, 800);
//            frame.setLocationRelativeTo(null);
//            frame.setTitle(absolutPath);
//            frame.setVisible(true);
            jInternalFrameTree.getContentPane().add(jPanelTree, BorderLayout.PAGE_END);
            jInternalFrameTree.setSize(400, 400);
            //jInternalFrameTree.setTitle(absolutPath);
            jInternalFrameTree.setTitle(filePath.getParentFile().getName());
//            jInternalFrameTree.setToolTipText("Depth of the Call Tree: "+levelNum+"Number of Methods by Level: "
//                +clusterNum+"\nMinimum Ratio: "+callNum+"%");
//            jInternalFrameTree.setToolTipText("<html>"+"Depth of the Call Tree: "+levelNum+"<br>"
//                    +"Number of Methods by Level: "+clusterNum+"<br>"+"Minimum Ratio: "+callNum+"%</html>");
            return updated;

//        jScrollPane1 = new javax.swing.JScrollPane();
//        jTree1 = new javax.swing.JTree();
//        jPanel1 = new javax.swing.JPanel();
//        jScrollPane2 = new javax.swing.JScrollPane();
//        jTextPane1 = new javax.swing.JTextPane();
//
//        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
//
//        jScrollPane1.setViewportView(jTree1);
//
//        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);
//
//        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(" Nomes "));
//
//        jScrollPane2.setViewportView(jTextPane1);
//
//        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
//        jPanel1.setLayout(jPanel1Layout);
//        jPanel1Layout.setHorizontalGroup(
//            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
//        );
//        jPanel1Layout.setVerticalGroup(
//            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
//        );
//
//        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);
   }

   /************** PRIVATE METHODS **************/

   private void doMouseClicked(MouseEvent get){
       logger = new String();
       TreePath tp = jTree.getPathForLocation(get.getX(), get.getY());
                if (tp == null)
                    return ;
                else if(get.getButton() == MouseEvent.BUTTON3){//Disables a node
                  jTree.setSelectionPath(tp);
                  JPopupMenu jPopupMenu = new JPopupMenu();
                  JMenuItem jMenuItemD = new JMenuItem("Disable Node");
//                  JMenuItem jMenuItemS = new JMenuItem("Save Tree");
                  jPopupMenu.add(jMenuItemD);
                  jMenuItemD.addActionListener(new ActionListener() {
                     public void actionPerformed(ActionEvent e) {
                        setNodeEnabled(false);
                     }
                  });
//                  jPopupMenu.add(jMenuItemS);
//                  jMenuItemS.addActionListener(new ActionListener() {
//                     public void actionPerformed(ActionEvent e) {
//                    try {
//                        SaveJTree.saveModel(null, new DefaultMutableTreeNode(jTree.getModel()), filePath, dir, callNum, levelNum, Integer.parseInt(clusterNum));
//                    } catch (FileNotFoundException ex) {
//                        logger = TreeDisplay.class.getName() + ex;
//                        System.out.println(logger);
//                    } catch (IOException ex) {
//                        logger = TreeDisplay.class.getName() + ex;
//                        System.out.println(logger);
//                    }
//                     }
//                  });
                  jPopupMenu.show(get.getComponent(), get.getX(), get.getY());
                }else if(get.getButton() == MouseEvent.BUTTON1){//Shows the parameter values
                    
                    jTree.setSelectionPath(tp);
                    TreePath path = jTree.getSelectionPath();
                    if(path == null)
                        return;
                    ToolTipTreeNode node = (ToolTipTreeNode) path.getLastPathComponent();
                    jTextPaneArgs.setText(node.getMethodName() + " :  " + node.getParameterValues().toString());
                }
   }

   private void setNodeEnabled(boolean enabled) {
      TreePath path = jTree.getSelectionPath();
      if (path == null)
        return;
      ToolTipTreeNode node = null;
//      if(!enabled){
//          for (int i = 0; i < path.length; i++) {
            node = (ToolTipTreeNode) path.getLastPathComponent();
            
            //int beforeChildCount = node.getChildCount();
            node.setAllowsChildren(enabled);

//            int afterChildCount = node.getChildCount();
//            if (beforeChildCount == afterChildCount) {
//              model.nodeChanged(node);
//            } else {
              model.reload(node);
//            }
            //}
//          }
//      }else{
//        node = (ToolTipTreeNode) path.getLastPathComponent();
//
//      }
      
    }


   /**
    *
    * @param nodeTree
    * @param nodeList
    * @param level
    * @param clusterNum
    * @param callNum
    */
   private static void addChildren(DefaultMutableTreeNode nodeTree, LinkedList<Node> nodeList, int level, int clusterNum, double callNum) {
       LinkedList<String> checkedNodes = checkLevel(clusterNum, callNum, nodeList);
       if(checkedNodes != null){
           for(int i=0; i<nodeList.size(); i++){
                Node child = nodeList.get(i);
                if(child.getLevel()<=level && checkedNodes.contains(child.getLabel())){
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(child.getLabel());
                    if(child.hasChildren()) {
                        addChildren(newChild, child.getChildren(), level, clusterNum, callNum);
                    }
                    nodeTree.add(newChild);
                }
            }
       }
   }


   /**
    *
    * @param buildedTree
    * @param clusterNum
    * @param callNum
    * @param level
    * @return
    */
   private  DefaultMutableTreeNode createTree(Tree buildedTree, int clusterNum, double callNum, int level) {
       tree = buildedTree;
       //root = new DefaultMutableTreeNode("root");
       DefaultMutableTreeNode nodeTree;
       Iterator<Node> iterator = tree.getTree().iterator();
       while(iterator.hasNext()){
           Node subTree = iterator.next();
           DefaultMutableTreeNode nodeTreeRoot = new DefaultMutableTreeNode(subTree.getLabel());
           //root.add(nodeTreeRoot);
           LinkedList<String> checkedClusters = checkLevel(clusterNum, callNum, subTree.getChildren());
           if(checkedClusters != null){
                for(int i=0; i<subTree.getChildCount(); i++){
                   Node node = subTree.getChildAt(i);
                   if(checkedClusters.contains(node.getLabel()) && level>=node.getLevel()){
                       nodeTree = new DefaultMutableTreeNode(node.getLabel());
                       nodeTreeRoot.add(nodeTree);
                        if(node.hasChildren()) addChildren(nodeTree, node.getChildren(), level, clusterNum, callNum);
                   }
               }
           }
       }
       

       return null;
   }

   private ArrayList<ToolTipTreeNode> createJTree(LinkedList<Node> buildedTree) {
//       tree = buildedTree;
      // root = new DefaultMutableTreeNode("root");
       ToolTipTreeNode[] nodes = new ToolTipTreeNode[tree.getTree().size()];
       root = new ToolTipTreeNode("root", "", 0, null, false, 0, -1, -1);
       ToolTipTreeNode nodeTree;
       Iterator<Node> iterator = buildedTree.iterator();
       int index = 0;
       while(iterator.hasNext()){
           Node subTree = iterator.next();
           nodes[index] = new ToolTipTreeNode(subTree.getLabel(), subTree.getPackageName(), subTree.getLevel(), subTree.getParameterValues(), subTree.isBelongsToCluster(), subTree.getIdNode(), subTree.getIdFather(), subTree.getSubNodeNumber());
           root.add(nodes[index]);
//           LinkedList<String> checkedClusters = checkLevel(clusterNum, callNum, subTree.getChildren());
//           if(checkedClusters != null){
                for(int i=0; i<subTree.getChildCount(); i++){
                   Node node = subTree.getChildAt(i);                   
//                   if(checkedClusters.contains(node.getLabel())){
                       nodeTree = new ToolTipTreeNode(node.getLabel(), node.getPackageName(), node.getLevel(), node.getParameterValues(), node.isBelongsToCluster(), node.getIdNode(), node.getIdFather(), node.getSubNodeNumber());
                       nodes[index].add(nodeTree);                       
                       if(node.hasChildren()) addChildren(nodeTree, node.getChildren());
//                   }
               }
//           }
           index++;
       }
       return clusteringTree();
   }

   private static void addChildren(ToolTipTreeNode jTreeNode, LinkedList<Node> nodeList) {
//        LinkedList<String> checkedNodes = checkLevel(clusterNum, callNum, nodeList);
//       if(checkedNodes != null){
           for(int i=0; i<nodeList.size(); i++){
                Node child = nodeList.get(i);
//                if(child.getLevel()<=level && checkedNodes.contains(child.getLabel())){
                    //DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(child.getLabel());
                    ToolTipTreeNode newChild = new ToolTipTreeNode(child.getLabel(), child.getPackageName(), child.getLevel(), child.getParameterValues(), child.isBelongsToCluster(), child.getIdNode(), child.getIdFather(), child.getSubNodeNumber());
                    if(child.hasChildren()) {
                        addChildren(newChild, child.getChildren());
                    }
                    jTreeNode.add(newChild);
//                }
            }
//       }
    }

   /**
    *
    * @param clusterNum
    * @param callNum
    * @param children
    * @return
    */
   private static LinkedList<String> checkLevel(int clusterNum, double callNum, LinkedList<Node> children) {
        double subNodes = 0;
        LinkedList<Node> sortedChildren = new LinkedList<Node>();
        LinkedList<Node> selectedNodes = new LinkedList<Node>();
        for(int i=0; i<children.size(); i++){//Sums the subNode for all them
            Node node = children.get(i);
            subNodes = subNodes + node.getSubNodeNumber();
        }
        if(subNodes>0){
            //subNodes = subNodes/tree.getTree().getChildCount();//Finds the sample mean
            sortedChildren.addAll(children);
            sortedChildren = sortChildren(sortedChildren);
            //About the Sample Standard deviation takes the clusterNum
            int half = sortedChildren.size()/2;
            if(clusterNum<sortedChildren.size()){
                while(selectedNodes.size() <= clusterNum/2){
                    selectedNodes.add(sortedChildren.get(half));
                    half++;
                }
                half = sortedChildren.size()/2;
                while(selectedNodes.size() < clusterNum){
                    selectedNodes.add(sortedChildren.get(half-1));
                    half--;
                }
            }else selectedNodes.addAll(sortedChildren);
            return minimumCovering(callNum, selectedNodes, subNodes);//Verifies if the clusters selected cover the callNum and returns the best clusters
       }else if(children.size()<=clusterNum) return selectLeaves(children);
       return null;
   }

   /**
    *
    * @param children
    * @return
    */
   private static LinkedList<String> selectLeaves(LinkedList<Node> children) {
        LinkedList<String> leafList = new LinkedList<String>();
        for(Node leaf : children){
            leafList.add(leaf.getLabel());
        }
        return leafList;
    }

   /**
    *
    * @param callNum
    * @param selectedNodes
    * @param subNodes
    * @return
    */
   private static LinkedList<String> minimumCovering(double callNum, LinkedList<Node> selectedNodes, double subNodes) {
       LinkedList<String> prune = new LinkedList<String>();
       for(int i=0; i<selectedNodes.size(); i++){
            if(selectedNodes.get(i).getSubNodeNumber()/subNodes>=callNum/100) prune.add(selectedNodes.get(i).getLabel());
       }
       return prune;
    }

   /**
    *
    * @param nodeList
    * @return
    */
   private static LinkedList<Node> sortChildren(LinkedList<Node> nodeList){
       for(int i=0; i<nodeList.size(); i++){
           for(int j=i+1; j<nodeList.size(); j++){
               if(nodeList.get(i).getSubNodeNumber()>nodeList.get(j).getSubNodeNumber()){
                   Node helpTransfer = nodeList.get(i);
                   nodeList.set(i, nodeList.get(j));
                   nodeList.set(j, helpTransfer);
               }
           }
       }
       return nodeList;
   }
   
   private JTree paintTreeNodes() {
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
                            for(int i =0; i <applicantsClusters.size(); i++){
                                if(applicantsClusters.get(i).getIdNode() == node.getIdNode()){
                                    setBackgroundNonSelectionColor(Color.yellow);
                                    break;
                                }
                            }
                            for(int i =0; i <selectedNodes.size(); i++){
                                    if(selectedNodes.get(i).getIdNode() == node.getIdNode()) {
                                        setBackgroundNonSelectionColor(Color.green);
                                        break;
                                    }
                            }
                        }
                        else if(!node.isBelongsToCluster()){
                            setBackgroundNonSelectionColor(Color.white);
                        }

                        return (this);
                }
            });
            return jTreePaint;
    }

   private static int getTimes(String node){
       String repeat = "";
       if(node.startsWith("[Repeat")){
                       int k=10;
                       while(Util.isDigit(node.charAt(k))){
                            repeat = repeat + node.charAt(k);
                            k++;
                       }
                       return Integer.parseInt(repeat);

       }
       return 0;
   }

   private static DefaultMutableTreeNode getTreeNode(TreePath path){
       return (DefaultMutableTreeNode)(path.getLastPathComponent());
   }

    private ArrayList<ToolTipTreeNode> clusteringTree() {
        ArrayList<ToolTipTreeNode> leaves = new ArrayList<ToolTipTreeNode>();
        for(int navigateRootChildren = 0; navigateRootChildren < root.getChildCount(); navigateRootChildren++){
            ToolTipTreeNode node = (ToolTipTreeNode)root.getChildAt(navigateRootChildren);
            if(node.isLeaf()) leaves.add(node);
            else navigatorLeaves(node, leaves);
        }
        return aglomerativeClustering(leaves);
    }

    private void navigatorLeaves(ToolTipTreeNode node, ArrayList<ToolTipTreeNode> leaves) {
        for(int indexNavigator = 0; indexNavigator < node.getChildCount(); indexNavigator++) {
            ToolTipTreeNode child = (ToolTipTreeNode)node.getChildAt(indexNavigator);
            if(child.isLeaf()) leaves.add(child);
            else navigatorLeaves(child, leaves);
        }

    }

    private ArrayList<ToolTipTreeNode> aglomerativeClustering(ArrayList<ToolTipTreeNode> leaves) {
        int roof = Math.round(nodesNumber/(float)independentClusterNumber);
        ArrayList<ToolTipTreeNode> clusters = new ArrayList<ToolTipTreeNode>();
        amalgamateClasses = new ArrayList<ToolTipTreeNode>();
        while(clusters.size() <= independentClusterNumber){
            int index = 0;
            int currentSize = leaves.size();
            while(index < leaves.size()) {
                ToolTipTreeNode parent = lookingForByDepthSearch(root, leaves.get(index).getIdFather());
                ArrayList<ToolTipTreeNode> leavesToCut = new ArrayList<ToolTipTreeNode>();
                Iterator<ToolTipTreeNode> iterator = leaves.iterator();
                while(iterator.hasNext()){
                        ToolTipTreeNode leaf = iterator.next();
                        if(leaf.getIdFather() == parent.getIdNode()) leavesToCut.add(leaf);
                }
                for(int i=0; i<parent.getChildCount(); i++){
                    if(!leavesToCut.contains((ToolTipTreeNode)parent.getChildAt(i))) leavesToCut.add((ToolTipTreeNode)parent.getChildAt(i));
                }
                if(leavesToCut.size()>1 && (parent.getSubNodeNumber() <= roof) ) {//||
                       // (((parent.getSubNodeNumber()/(double)roof)-1) <= allowedStandardDeviation)){
                    parent.setBelongsToCluster(true);
                    /*if(clusters.contains(leaves.get(index))){
                        clusters.set(clusters.indexOf(leaves.get(index)), parent);
                    }else*/ if(!clusters.contains(parent)) clusters.add(parent);
                    if(!exist(parent)) amalgamateClasses.add(parent);

                    //clusters.addAll(leavesToCut);
                    allowPaintNodes(leavesToCut, clusters);
                    leaves.removeAll(leavesToCut);
                    leaves.add(parent);
                    index = -1;
                }
                index++;
            }
            if(currentSize == leaves.size()) break;
        }
        if(clusters.size() > 0) sortInDecrescentOrder(clusters);
        for(ToolTipTreeNode node : amalgamateClasses){
            if(node.getMethodName().equals("SpecialNode")) {
                amalgamateClasses.remove(node);
                break;
            }
        }
        return clusters;
    }


    private ToolTipTreeNode lookingForByDepthSearch(ToolTipTreeNode node, int idParent) {
        ToolTipTreeNode child = null;
        for(int indexChildren = 0; indexChildren < node.getChildCount(); indexChildren++){
            child = (ToolTipTreeNode)node.getChildAt(indexChildren);
            if(child.getIdNode() == idParent) return child;
            else if(!child.isLeaf()) {
                child = lookingForByDepthSearch(child, idParent);
                if(child.getIdNode() == idParent) break;
            }
        }
        return child;
    }

    private void sortInDecrescentOrder(ArrayList<ToolTipTreeNode> clusters) {
        quicksort(clusters, 0, clusters.size()-1);
    }

    private void quicksort(ArrayList<ToolTipTreeNode> clusters, int begin, int end) {
        int i = begin;
        int j = end;
        ToolTipTreeNode pivo = clusters.get((begin+end)/2);
        while(i < j) {
            while(clusters.get(i).getSubNodeNumber() > pivo.getSubNodeNumber()){
                i++;
            }
            while(clusters.get(j).getSubNodeNumber() < pivo.getSubNodeNumber()) {
                j--;
            }
            if(i <= j) {
                ToolTipTreeNode aux = clusters.get(i);
                clusters.set(i, clusters.get(j));
                clusters.set(j, aux);
                i++;
                j--;
            }
        }
        if(j>begin) quicksort(clusters, begin, j);
        if(i<end) quicksort(clusters, i, end);
    }

    private void allowPaintNodes(ArrayList<ToolTipTreeNode> leavesToCut, ArrayList<ToolTipTreeNode> clusters) {
       for(int i = 0; i < leavesToCut.size(); i++) {
           leavesToCut.get(i).setBelongsToCluster(true);
           if(!clusters.contains(leavesToCut.get(i)))clusters.add(leavesToCut.get(i));
           if(!exist(leavesToCut.get(i))) amalgamateClasses.add(leavesToCut.get(i));
           almagamateNodes(leavesToCut.get(i), root, clusters);
       }
    }

    private void almagamateNodes(ToolTipTreeNode node, ToolTipTreeNode tree, ArrayList<ToolTipTreeNode> clusters){
        ToolTipTreeNode child = null;
        for(int indexChildren = 0; indexChildren < tree.getChildCount(); indexChildren++){
            child = (ToolTipTreeNode)tree.getChildAt(indexChildren);
            if(child.getIdNode() == node.getIdNode()) {
                child.setBelongsToCluster(true);
                if(!clusters.contains(child)) clusters.add(child);
                if(!child.isLeaf()) paintNodes(child, clusters);
                if(!exist(child)) amalgamateClasses.add(child);
            }
            else if(!child.isLeaf()) almagamateNodes(node, child, clusters);
        }
    }

    private void paintNodes(ToolTipTreeNode child, ArrayList<ToolTipTreeNode> clusters) {
        ToolTipTreeNode node = null;
        for(int i = 0; i < child.getChildCount(); i++){
            node = (ToolTipTreeNode)child.getChildAt(i);
            node.setBelongsToCluster(true);
            if(!clusters.contains(node)) clusters.add(node);
            if(!node.isLeaf()) paintNodes(node, clusters);
        }
    }

    /**
     * Classify the best clusters and suggests the applicant clusters
     */
    private void classifyClusters() {
        bestClusters = new Cluster[independentClusterNumber];
        for(int i = 0; i < bestClusters.length; i++){
            bestClusters[i] = new Cluster();
        }
        selectedNodes  = new ArrayList<ToolTipTreeNode>();
        int count = 0;
        while(count < independentClusterNumber){
            if(applicantsClusters.size() > 0){
                Cluster cluster = new Cluster();
                ArrayList<ToolTipTreeNode> node = new ArrayList<ToolTipTreeNode>();
                node.add(applicantsClusters.get(0));
                applicantsClusters.remove(0);
                insertChildren(node);
                cluster.setCluster(node);
                bestClusters[count] = cluster;
                selectedNodes.addAll(node);
                count++;
            }else break;
        }
    }

    private void insertChildren(ArrayList<ToolTipTreeNode> cluster) {
        for(int i=0; i < cluster.size(); i++){
            for(int j=0; j < applicantsClusters.size(); j++) {
                if(!cluster.contains(applicantsClusters.get(j)) && cluster.get(i).getIdNode() == applicantsClusters.get(j).getIdFather()){
                    cluster.add(applicantsClusters.get(j));
                }
            }
            applicantsClusters.removeAll(cluster);
        }        
    }

    private boolean exist(ToolTipTreeNode parent) {
        for(ToolTipTreeNode node : amalgamateClasses){
            if(node.getMethodName().split("\\.")[0].equals(parent.getMethodName().split("\\.")[0])) return true;
        }
        return false;
    }

}