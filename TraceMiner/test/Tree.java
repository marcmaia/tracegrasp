/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import data.mining.GSP;
import data.mining.InterestingPatterns;
import data.processing.RecoveryPattern;
import data.processing.SequenceStructure;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import viewer.main.tree.TreeUtils;

/**
 *
 * @author Luciana
 */
public class Tree {
    private static DefaultMutableTreeNode root;
    private static JTree jtree;
    private static TreePath path;
    private static LinkedList<LinkedList<String>> sequentialPatternList;
    private DefaultTreeModel treemodel = new DefaultTreeModel(root);


    public static boolean start(String strCurrentPath, int option, int size, double support, String subject, JPanel panel, JLabel label) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        boolean isOk=false;
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame();
            Container contentPane = frame.getContentPane();
            contentPane.setLayout(new GridLayout(1, 1));
            isOk = Tree.createTree(strCurrentPath, option, size, support, subject, panel, label);
            if(isOk){
            contentPane.add(new JScrollPane(jtree));
            frame.setTitle(strCurrentPath + subject + " - Suporte: "+(support*100)+"%");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            }
        return isOk;
    }

    
    private static boolean createTree(String strCurrentPath, int option, int size, double support, String subject, JPanel panel, JLabel label) {
        root = new DefaultMutableTreeNode("root"+subject);
        LinkedList<LinkedList<String>> structure = seekSequentialPatterns(strCurrentPath, option, size, support, panel, label);
        label.setText("Construindo árvore das sequências recuperadas ... ");
        repaintComponent(panel);
        if(structure.size()>0 && sequentialPatternList.size()>0){
            InterestingPatterns biggest = new InterestingPatterns();
            biggest.setSequentialPatterns(structure);
            biggest.selectPatterns();
            LinkedList<LinkedList<String>> biggestPatterns = biggest.getSequentialPatterns();
            Iterator<LinkedList<String>> biggestIterator = biggestPatterns.descendingIterator();
            int counter = 1;
            while(biggestIterator.hasNext()){
                LinkedList<String> chosenPatterns = biggestIterator.next();
                Iterator<String> chosenIterator = chosenPatterns.iterator();
                DefaultMutableTreeNode ancestor = new DefaultMutableTreeNode("Padrão Sequencial "+counter);
                counter++;
                while(chosenIterator.hasNext()){
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(chosenIterator.next());
                    ancestor.add(node);
                }
                addChildren(ancestor, chosenPatterns);
                root.add(ancestor);
            }
            path = new TreePath(root);
            jtree = new JTree(root);
            TreeUtils.expandAll(jtree, path, true);
            return true;
        }
        return false;
    }

    private static void addChildren(DefaultMutableTreeNode ancestor, LinkedList<String> structure){
        Iterator<LinkedList<String>> sequentialPatternIterator = sequentialPatternList.iterator();
        int sequence = 1;
        LinkedList<LinkedList<String>> indexes = new LinkedList<LinkedList<String>>();
        while(sequentialPatternIterator.hasNext()){
            LinkedList<String> sequentialPattern = sequentialPatternIterator.next();
            int mark = structure.indexOf(sequentialPattern.get(0).split("-")[1]);
            int index = 0;
            if(structure.size()>=sequentialPattern.size()){
              while(index < sequentialPattern.size() && mark != -1 && mark < structure.size()){
                 if(mark < structure.size() && index < sequentialPattern.size() && structure.get(mark).equals(sequentialPattern.get(index).split("-")[1])){
                    index++;
                    mark++;
                }else if(mark < structure.size()){/* Searchs the next occurrence of sequentialPattern.get(0) */
                    index=0;
                    mark = searchNextOccurrence(sequentialPattern.get(0).split("-")[1], structure, mark);
                }
              }
            if(index == sequentialPattern.size()){
                mark = mark - index;
                addNewChild(sequence, mark, sequentialPattern, ancestor);
                indexes.add(sequentialPattern);
                sequence++;
            }
            }
        }
        removePatterns(indexes);
    }
    
    private static void removePatterns(LinkedList<LinkedList<String>> indexes){
        int index = -1;
        Iterator<LinkedList<String>> indexIterator = indexes.iterator();
        while(indexIterator.hasNext()){
            index = sequentialPatternList.indexOf(indexIterator.next());
            sequentialPatternList.remove(index);
        }
    }


    private static void addNewChild(int sequence, int mark, LinkedList<String> sequentialPattern, DefaultMutableTreeNode ancestor) {
        int count = 0;
        int index = 0;
        Enumeration e = ancestor.children();
        while(e.hasMoreElements()){
             DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
             if(count < mark) count++;
             else if(index < sequentialPattern.size()){
                 /*Funcionando 1a opção*/
                 boolean inserted = false;
                 if(!node.isLeaf()){
                     Enumeration enum1 = node.children();
                     while(enum1.hasMoreElements()){
                         DefaultMutableTreeNode child = (DefaultMutableTreeNode)enum1.nextElement();
                         String[] userObject = ((String)child.getUserObject()).split(" / ");
                         if(userObject[0].equals(sequentialPattern.get(index).split("-")[0])){
                                child.setUserObject(child.getUserObject()+"-"+sequence);
                                inserted = true;
                                break;
                         }
                     }
                 }
                 if(!inserted){
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sequentialPattern.get(index).split("-")[0]+" / "+sequence);
                    node.add(newChild);
                 }
                 /* primeira opção */
//                 DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sequence + " - " +sequentialPattern.get(index).split("-")[0]);
//                 node.add(newChild);
                 index++;
             }else if(index == sequentialPattern.size()) break;
        }
    }



    private static int searchNextOccurrence(String method, List<String> list, int index) {
        while(index < list.size()){
            if(list.get(index).equals(method)) return index;
            index++;
        }
        return -1;
    }

    private static LinkedList<LinkedList<String>> seekSequentialPatterns(String strCurrentPath, int option, int size, double support, JPanel panel, JLabel label) {
        SequenceStructure sequences = new SequenceStructure(strCurrentPath);
        sequentialPatternList = new LinkedList<LinkedList<String>>();
        label.setText("Recuperando Sequências do Rastro");
        repaintComponent(panel);
       // sequences.getTraceSequences();
        label.setText("Minerando sequências");
        repaintComponent(panel);
        GSP gsp = new GSP(support, size);
        RecoveryPattern recovery = new RecoveryPattern();
//        gsp.getOption(sequences.getStructure(), option);
        if(option == 2) sequentialPatternList.addAll(recovery.getSkeleton(gsp.getPatterns(), sequences.getStructure()));
        else if(option == 3) sequentialPatternList.addAll(recovery.getSuffixSkeleton(gsp.getPatterns(), sequences.getStructure()));
        else if(option == 1) sequentialPatternList.addAll(recovery.getFullMethods(gsp.getPatterns(), sequences.getStructure()));
        else if(option == 4 || option ==5) {
            sequentialPatternList.addAll(gsp.getPatterns());
            return gsp.getRecoveredPatterns();
        }
        return recovery.getMethods();
    }

    private static void repaintComponent(JPanel panel) {
        Graphics g = panel.getGraphics();
        panel.paint(g);
    }
}
