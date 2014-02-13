/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utils.disableNodeJTree;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JTree;
import java.awt.Component;
/**
 *
 * @author Luciana
 */
public class DisabledRenderer extends DefaultTreeCellRenderer {
  protected Icon disabledLeafIcon;

  protected Icon disabledOpenIcon;

  protected Icon disabledClosedIcon;

  public DisabledRenderer() {
    this(new GraydIcon(UIManager.getIcon("Tree.leafIcon")), new GraydIcon(
        UIManager.getIcon("Tree.openIcon")), new GraydIcon(UIManager
        .getIcon("Tree.closedIcon")));
  }

  public DisabledRenderer(Icon leafIcon, Icon openIcon, Icon closedIcon) {
    setDisabledLeafIcon(leafIcon);
    setDisabledOpenIcon(openIcon);
    setDisabledClosedIcon(closedIcon);
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {
    String stringValue = tree.convertValueToText(value, sel, expanded,
        leaf, row, hasFocus);
    setText(stringValue);
    if (sel) {
      setForeground(getTextSelectionColor());
    } else {
      setForeground(getTextNonSelectionColor());
    }

    boolean treeIsEnabled = tree.isEnabled();
    boolean nodeIsEnabled = ((DisabledNode) value).isEnabled();
    boolean isEnabled = (treeIsEnabled && nodeIsEnabled);
    setEnabled(isEnabled);

    if (isEnabled) {
      selected = sel;
      if (leaf) {
        setIcon(getLeafIcon());
      } else if (expanded) {
        setIcon(getOpenIcon());
      } else {
        setIcon(getClosedIcon());
      }
    } else {
      selected = false;
      if (leaf) {
        if (nodeIsEnabled) {
          setDisabledIcon(getLeafIcon());
        } else {
          setDisabledIcon(disabledLeafIcon);
        }
      } else if (expanded) {
        if (nodeIsEnabled) {
          setDisabledIcon(getOpenIcon());
        } else {
          setDisabledIcon(disabledOpenIcon);
        }
      } else {
        if (nodeIsEnabled) {
          setDisabledIcon(getClosedIcon());
        } else {
          setDisabledIcon(disabledClosedIcon);
        }
      }
    }
    return this;
  }

  public void setDisabledLeafIcon(Icon icon) {
    disabledLeafIcon = icon;
  }

  public void setDisabledOpenIcon(Icon icon) {
    disabledOpenIcon = icon;
  }

  public void setDisabledClosedIcon(Icon icon) {
    disabledClosedIcon = icon;
  }
}
