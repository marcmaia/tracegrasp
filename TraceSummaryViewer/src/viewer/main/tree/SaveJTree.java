/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 *
 * @author Luciana
 */
public class SaveJTree {


    public static DefaultMutableTreeNode loadModel(File path) throws FileNotFoundException, IOException, ClassNotFoundException{
        DefaultMutableTreeNode model = null;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.getAbsolutePath()));
        model = (DefaultMutableTreeNode)in.readObject();
        in.close();        
       return model;
    }

    public static void saveModel(DefaultMutableTreeNode model, DefaultMutableTreeNode tree, File path, String dir, String support, String motive, int size) throws FileNotFoundException, IOException{
        File directory = null;
        if(model != null){
            directory = new File(path.getAbsolutePath()+dir+"SequentialPatterns");
        }else{
            directory = new File(path.getParentFile().getParentFile().getAbsolutePath()+dir+"CallTree");
        }
        directory.mkdirs();
        if(directory.exists()){
            ObjectOutputStream out = null;
            if(model != null) {
                out = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath()+dir+path.getName()+support+"%Motive-"+motive+"Size"+size+".info"));
                out.writeObject(model);
            }
            else{
                String[] granularity = path.getParentFile().getName().split("-");
                out = new ObjectOutputStream(new FileOutputStream(directory.getAbsolutePath()+dir+path.getName()+"-"+granularity[granularity.length-2]+"-Ratio"+support+"%-depth"+motive+"-MethLevel"+size+".info"));
                out.writeObject(tree);
            }
            out.close();
        }
    }

    public static JTree loadTree(File path) throws FileNotFoundException, IOException, ClassNotFoundException{
        JTree jTree = null;
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.getAbsolutePath()));
        jTree = (JTree)in.readObject();
        in.close();
       return jTree;
    }

}
