/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import data.mining.AspectMining;
import data.processing.RecoveryPattern;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JTree;

/**
 *
 * @author Luciana
 */
public class AspectView {
    private static LinkedList<LinkedList<String>> sequentialPatternList;

    public static JTree start(String[] paths, double support) throws IOException{
        AspectMining aspect = new AspectMining(paths, support);
        aspect.miningSequences();
        sequentialPatternList = new LinkedList<LinkedList<String>>();
        RecoveryPattern recovery = new RecoveryPattern();
        sequentialPatternList.addAll(recovery.getFullMethods(aspect.getPatterns(), aspect.getTraces()));
        return createTree(recovery.getMethods());
    }

    @SuppressWarnings("static-access")
    private static JTree createTree(LinkedList<LinkedList<String>> structure) {
        Tree tree = new Tree();
        tree.setRoot("root");
        tree.setSequentialPatternList(sequentialPatternList);
        tree.buildTree(structure);
        return tree.getJtree();
    }
}
