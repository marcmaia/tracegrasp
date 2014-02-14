/*
 *  This class creates the object list to present all classes in a package for the user
 *  to select the objects that belong to sequence diagram.
 */

package viewer.main.tree;

import builder.tree.PaintMultipleTreeNode;
import builder.tree.ToolTipTreeNode;
import data.mining.Cluster;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author Luciana
 */
public class ObjectList {
    private ArrayList<ToolTipTreeNode> list;
    private PaintMultipleTreeNode root;
    private ArrayList<PaintMultipleTreeNode> selectedObjects;
    private Map hashTable;

    public ObjectList(ArrayList<ToolTipTreeNode> list){
        this.list = list;
    }

    public boolean getSizeSelectionObjects(){
        return hashTable==null;
    }

    public JInternalFrame recoverGroups(){
        DefaultMutableTreeNode groups = new DefaultMutableTreeNode("Root");
        Iterator<String> keyIterator = hashTable.keySet().iterator();
        while(keyIterator.hasNext()){
            String key = keyIterator.next();
            ArrayList<String> classes = (ArrayList<String>)hashTable.get(key);
            DefaultMutableTreeNode cluster = null;
            if(classes.size() > 0 ) cluster = new DefaultMutableTreeNode("Objects of the Group << " + key + " >>");
            for(String className : classes){
                if(!className.equals(key)) cluster.add(new DefaultMutableTreeNode(className));
            }
            if(cluster != null && !cluster.isLeaf()) groups.add(cluster);
        }
        return createGroup(groups);
    }

    /**
     * This method generates the object list
     */
    public JInternalFrame generateList(){
        root = new PaintMultipleTreeNode("Root", true);
        for(ToolTipTreeNode node : list){
            boolean inserted = false;
            String packageName = node.getToolTipText().split("\n")[0];
            String methodName = node.getMethodName().split("\\.")[0];
            PaintMultipleTreeNode child = null;
            for(int i=0; i < root.getChildCount(); i++){
                child = (PaintMultipleTreeNode)root.getChildAt(i);
                if(child.getUserObject().toString().equals(packageName)) {
                    child.add(new PaintMultipleTreeNode(methodName, true));
                    inserted = true;
                }
            }
            if(!inserted){
                child = new PaintMultipleTreeNode(packageName, true);
                child.add(new PaintMultipleTreeNode(methodName, true));
                root.add(child);
            }
        }
        list = null;
        return createTree();
    }

    private JInternalFrame createTree() {
        selectedObjects = new ArrayList<PaintMultipleTreeNode>();
        JTree jTree = selectedNode();

        TreeUtils.expandAllTree(jTree, new TreePath(root), true);
        JScrollPane jScrollPaneTree = new JScrollPane();
        JInternalFrame jInternalFrame = new JInternalFrame();
        jScrollPaneTree.setViewportView(jTree);
        jInternalFrame.setTitle("Object List");
        jInternalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jInternalFrame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
        jInternalFrame.setSize(400, 400);
        return jInternalFrame;
    }

    public void selectNodes(){
        selectedNode();
    }

  protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = ObjectList.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
  }

    private JTree selectedNode() {
        JTree jTree = new JTree(root);
        ImageIcon icon = createImageIcon("/images/package.gif");
        ImageIcon leafIcon = createImageIcon("/images/class.gif");
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer(){
            public Component getTreeCellRendererComponent(JTree pTree, Object pValue, boolean pIsSelected, boolean pIsExpanded,
                     boolean pIsLeaf, int pRow, boolean pHasFocus) {
                        PaintMultipleTreeNode node = (PaintMultipleTreeNode)pValue;
                        super.getTreeCellRendererComponent(pTree, pValue, pIsSelected,
                                 pIsExpanded, pIsLeaf, pRow, pHasFocus);
                        if(pIsSelected && !pHasFocus){
                          if(pIsLeaf) {
                              node.setSelected(true);
                              int index = selectedObjects.indexOf(node);
                              if(index == -1) selectedObjects.add(node);
                              else selectedObjects.set(index, node);
                          }else {
                              for(int i = 0; i < node.getChildCount(); i++){
                                  PaintMultipleTreeNode child = (PaintMultipleTreeNode)node.getChildAt(i);
                                  child.setSelected(true);
                                  int index = selectedObjects.indexOf(child);
                                  if(index == -1) selectedObjects.add(child);
                              }
                          }
                        }else if(pIsSelected && pHasFocus){
                            if(node.getSelected()){
                                  node.setSelected(false);
                                  selectedObjects.remove(node);
                            }
                        }
                        return (this);
                }
        };
        if (icon != null && leafIcon != null) {            
            renderer.setOpenIcon(icon);
            renderer.setClosedIcon(icon);
            renderer.setLeafIcon(leafIcon);
            jTree.setCellRenderer(renderer);
        }
        
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        jTree.setCellRenderer(renderer);
        return jTree;
    }



    /**
     * Groups the classes
     * @param clusters
     */
    public void groupObjects(Cluster[] clusters) {
        hashTable = new HashMap();
        for(int cluster = 0; cluster < clusters.length; cluster++) {
            ArrayList<String> classes = new ArrayList<String>();
            Cluster selectedCluster = clusters[cluster];
            if(selectedCluster.getNodes().size() > 0) {
              for(PaintMultipleTreeNode node : selectedObjects){
                for(ToolTipTreeNode selectedNode : selectedCluster.getNodes()) {
                    String selectedClass = selectedNode.getMethodName().split("\\.")[0];
                    if(node.getNode().equals(selectedClass) &&
                            !classes.contains(selectedClass)) {
                        classes.add(selectedClass);
                        break;
                    }
                }
            }
            hashTable.put(selectedCluster.getNodes().get(0).getMethodName().split("\\.")[0], classes);
            }
        }
    }

    private JInternalFrame createGroup(DefaultMutableTreeNode groups) {
        JTree jTree = new JTree(groups);
        ImageIcon leafIcon = createImageIcon("/images/class.gif");
        if(leafIcon != null){
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setLeafIcon(leafIcon);
            jTree.setCellRenderer(renderer);
        }
        TreeUtils.expandAllTree(jTree, new TreePath(root), true);
        JScrollPane jScrollPaneTree = new JScrollPane();
        JInternalFrame jInternalFrame = new JInternalFrame();
        jScrollPaneTree.setViewportView(jTree);
        jInternalFrame.setTitle("Grouped Objects");
        jInternalFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jInternalFrame.getContentPane().add(jScrollPaneTree, java.awt.BorderLayout.CENTER);
        jInternalFrame.setSize(400, 400);
        return jInternalFrame;
    }


}
