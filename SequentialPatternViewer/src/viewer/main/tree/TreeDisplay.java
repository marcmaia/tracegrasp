/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;



import builder.tree.Tree;
import builder.tree.ToolTipTreeNode;
import builder.tree.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
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
    private String dir;
    private String callNum;
    private String levelNum;
    private String clusterNum;
    private String logger;
    private JTextPane jTextPaneArgs;
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
    
    /**
    *
    * @param buildedTree
    * @param clusterNum
    * @param callNum
    * @param levelNum
    */
   public boolean begin(Tree buildedTree, String clusterNum, String callNum, String levelNum, String absolutPath, String dir) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
            boolean updated = false;
            filePath = new File(absolutPath);
            this.callNum = callNum;
            this.levelNum = levelNum;
            this.clusterNum = clusterNum;
            this. dir = dir;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            /*Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout(2, 1));*/
            JScrollPane jScrollPaneTree = new JScrollPane();
            JScrollPane jScrollPaneArgs = new JScrollPane();
            JPanel jPanelTree = new JPanel();
            jTextPaneArgs = new JTextPane();

            createJTree(buildedTree, Integer.parseInt(clusterNum), Double.parseDouble(callNum), Integer.parseInt(levelNum));
            jTree = paintTreeNodes();
            model = (DefaultTreeModel) jTree.getModel();
            jTree.addMouseListener(new MouseAdapter(){            
            public void mouseClicked(MouseEvent event){
                    doMouseClicked(event);
                }
            });
            if(logger != null){
                updated = true;
            }
            TreeUtils.expandAllTree(jTree, new TreePath(root), true);
            //contentPane.add(new JScrollPane(jTree));
            jScrollPaneTree.setViewportView(jTree);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
            jPanelTree.setBorder(javax.swing.BorderFactory.createTitledBorder(" Parameter Values "));
            jScrollPaneArgs.setViewportView(jTextPaneArgs);
            javax.swing.GroupLayout jPanelLayoutTree = new GroupLayout(jPanelTree);
            jPanelTree.setLayout(jPanelLayoutTree);
            jPanelLayoutTree.setHorizontalGroup(jPanelLayoutTree.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneArgs, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE));
            jPanelLayoutTree.setVerticalGroup(jPanelLayoutTree.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneArgs, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE));
            frame.getContentPane().add(jPanelTree, BorderLayout.PAGE_END);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null);
            frame.setTitle(absolutPath);
            frame.setVisible(true);
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

   private void createJTree(Tree buildedTree, int clusterNum, double callNum, int level) {
       tree = buildedTree;
      // root = new DefaultMutableTreeNode("root");
       ToolTipTreeNode[] nodes = new ToolTipTreeNode[tree.getTree().size()];
       root = new ToolTipTreeNode("root", "", 0, null, 0);
       ToolTipTreeNode nodeTree;
       Iterator<Node> iterator = tree.getTree().iterator();
       int index = 0;
       while(iterator.hasNext()){
           Node subTree = iterator.next();
           nodes[index] = new ToolTipTreeNode(subTree.getLabel(), subTree.getPackageName(), subTree.getLevel(), subTree.getParameterValues(), 0);
           root.add(nodes[index]);
           LinkedList<String> checkedClusters = checkLevel(clusterNum, callNum, subTree.getChildren());
           if(checkedClusters != null){
                for(int i=0; i<subTree.getChildCount(); i++){
                   Node node = subTree.getChildAt(i);                   
                   if(checkedClusters.contains(node.getLabel()) && level>=node.getLevel()){
                       nodeTree = new ToolTipTreeNode(node.getLabel(), node.getPackageName(), node.getLevel(), subTree.getParameterValues(), 0);
                       nodes[index].add(nodeTree);                       
                       if(node.hasChildren()) addChildren(nodeTree, node.getChildren(), level, clusterNum, callNum);
                   }
               }
           }
           index++;
       }
   }

   private static void addChildren(ToolTipTreeNode jTreeNode, LinkedList<Node> nodeList, int level, int clusterNum, double callNum) {
        LinkedList<String> checkedNodes = checkLevel(clusterNum, callNum, nodeList);
       if(checkedNodes != null){
           for(int i=0; i<nodeList.size(); i++){
                Node child = nodeList.get(i);
                if(child.getLevel()<=level && checkedNodes.contains(child.getLabel())){
                    //DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(child.getLabel());
                    ToolTipTreeNode newChild = new ToolTipTreeNode(child.getLabel(), child.getPackageName(), child.getLevel(), child.getParameterValues(), 0);
                    if(child.hasChildren()) {
                        addChildren(newChild, child.getChildren(), level, clusterNum, callNum);
                    }
                    jTreeNode.add(newChild);
                }
            }
       }
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
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)pValue;
                        super.getTreeCellRendererComponent(pTree, pValue, pIsSelected,
                                 pIsExpanded, pIsLeaf, pRow, pHasFocus);
                        node.getUserObject().toString();
                        int repeatedTimes = 0;

                        repeatedTimes = getTimes(node.getUserObject().toString());
                        if(repeatedTimes==1) {
                            setBackgroundNonSelectionColor(Color.yellow);
                        }
                        else if(repeatedTimes>1) {
                            setBackgroundNonSelectionColor(Color.red);
                        }
                        else {
                            setBackgroundNonSelectionColor(Color.white);
                        }
                        repeatedTimes = 0;
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

}
