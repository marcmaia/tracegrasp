/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;


/**
 *
 * @author Luciana
 */
public class TraceTree{
    private static DefaultMutableTreeNode root;
    private static JTree jtree;
    private static TreePath path;

    public static JTree start(File[] files) {
        root = new DefaultMutableTreeNode("Data Base");
        for(File file : files){
            DefaultMutableTreeNode repositoryTree = new DefaultMutableTreeNode(file.getName());
            insertChildren(repositoryTree, file.listFiles());
            root.add(repositoryTree);
        }
        path = new TreePath(root);
        jtree = new JTree(root);
        TreeUtils.expandAll(jtree, path, true);
        return jtree;
    }

    private static void insertChildren(DefaultMutableTreeNode repositoryTree, File[] files) {
        for(File file: files){
            if(file.isDirectory()){
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
                insertChildren(node, file.listFiles());
                repositoryTree.add(node);
            }
            else repositoryTree.add(new DefaultMutableTreeNode(file.getName()));
        }
    }


}
