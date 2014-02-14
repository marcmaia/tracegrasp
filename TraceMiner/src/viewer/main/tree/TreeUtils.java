/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Luciana
 */
public class TreeUtils {

    public static void expandAll(JTree tree, TreePath parent, boolean expand) {
    // Traverse children
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        //expandAll(tree, path, expand);
        if (expand) {
          tree.expandPath(path);
        } else {
          tree.collapsePath(path);
        }
      }
    }

    // Expansion or collapse must be done bottom-up
    if (expand) {
      tree.expandPath(parent);
    } else {
      tree.collapsePath(parent);
    }
  }

     public static void expandAllTree(JTree tree, TreePath parent, boolean expand) {
    // Traverse children
    TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (Enumeration e = node.children(); e.hasMoreElements();) {
        TreeNode n = (TreeNode) e.nextElement();
        TreePath path = parent.pathByAddingChild(n);
        //expandAll(tree, path, expand);
        if (expand) {
          tree.expandPath(path);
        } else {
          tree.collapsePath(path);
        }
        if(!n.isLeaf()) expandAllTree(tree, path, expand);
      }
    }

    // Expansion or collapse must be done bottom-up
    if (expand) {
      tree.expandPath(parent);
    } else {
      tree.collapsePath(parent);
    }
  }
}
