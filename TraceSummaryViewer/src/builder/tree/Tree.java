/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import builder.tree.ToolTipTreeNode;
import data.handler.CarryFileMemory;
import data.processing.MarkBorder;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 *
 * @author Luciana
 */
public class Tree {
    private CarryFileMemory traceFile;
    private CarryFileMemory sequenceFile;
    private String sequenceFilePath;
    private LinkedList<Node> root;
    private Map clusterNumberByLayer;
    private String filePath;
    private String packageName;
    private String directorySO;

    public Tree() {
        this.packageName = "";
        this.filePath ="";
    }
    /**
     * 
     * @param traceFile
     */
    public Tree(String traceFile){
        this.filePath = traceFile;
        this.traceFile = new CarryFileMemory(traceFile);
        this.root = new LinkedList<Node>();
        this.clusterNumberByLayer = new HashMap();
    }

    public Tree(String sequence, String trace, String directory) {
        filePath = trace;
        sequenceFilePath = sequence;
        directorySO = directory;
        this.root = new LinkedList<Node>();
        clusterNumberByLayer = new HashMap();
    }

    /**
     * Method used to generate the tree during the high view
     */
    public int generateTree() throws Error, IOException, ArrayIndexOutOfBoundsException{
        return insertChildren();
    }

    /**
     * This method is used to build the tree for utility mining
     */
    public ArrayList<ToolTipTreeNode> buildTree(String[] trace) throws FileNotFoundException, IOException {
        return insertFrequentChildren(trace);
    }

    public String getFilePath(){
        return this.filePath;
    }

    /**
     *
     * @return
     */
    public LinkedList<Node> getTree(){
        return this.root;
    }

    /**
     *
     * @param key
     * @return
     */
    public int getClusterNumber(int key) {
        return (Integer)this.clusterNumberByLayer.get(key);
    }

    private ArrayList<ToolTipTreeNode> insertFrequentChildren(String[] trace) throws FileNotFoundException, IOException {
        String[] resultDataMining = this.traceFile.carryCompleteFile();
        ArrayList<ToolTipTreeNode> groupByClass = new ArrayList<ToolTipTreeNode>();
        for(String line : trace){
            for(String className : resultDataMining){
                if(line.split(",")[0].equals(className.split(" ")[0])){
                    ToolTipTreeNode node = null;
                    boolean exist = false;
                    String children = line.split(",")[1];
                    for(ToolTipTreeNode search : groupByClass){
                       if(search.getMethodName().equals(className)){
                           Enumeration<DefaultMutableTreeNode> enum1 = ((DefaultMutableTreeNode)search).children();
                           while(enum1.hasMoreElements()){
                               DefaultMutableTreeNode temp = enum1.nextElement();
                               if(temp.getUserObject().toString().equals(children)){
                                   exist = true;
                                   break;
                               }
                           }
                            if(!exist){
                                search.add(new DefaultMutableTreeNode(children));
                                exist = true;
                                break;
                            }
                       }
                    }
                    if(!exist){
                       node = new ToolTipTreeNode(className, null, -1, null, false, -1, -1, -1);
                       node.add(new DefaultMutableTreeNode(line.split(",")[1]));
                       groupByClass.add(node);
                       break;
                    }
                }
            }
        }

        return groupByClass;
    }

    private String getClass(String className) {
        int index = -1;
        String subString = "";
        for(int k=className.length()-1; k>=0; k--){
            if(className.charAt(k)=='.'){
                index=k;
                break;
            }
        }
        if(index>0) {
            subString = className.substring(index+1, className.length());
            this.packageName = className.substring(0, index);
        }else this.packageName = "";
        return subString;
    }


    /**
     *
     * @param node
     */
    private void setClusterNumberLayer(Node node) {
        if(this.clusterNumberByLayer.containsKey(node.getLevel())){
           int count = (Integer)this.clusterNumberByLayer.get(node.getLevel());
           this.clusterNumberByLayer.put(node.getLevel(), count+1);
        }else this.clusterNumberByLayer.put(node.getLevel(), 1);
    }

    /**
     * Reads the file and inserts children
     */
    private int insertChildren() throws Error, IOException, NullPointerException, ArrayIndexOutOfBoundsException{
        MarkBorder markBorder = new MarkBorder(directorySO);
        String[] traceSequence = markBorder.delimitBorders(filePath, sequenceFilePath);
        Node nodeRoot = new Node();
        nodeRoot.setLevel(1);
        int id = 1;
        if(!traceSequence[0].startsWith("<SEQ")) {
            int level = Integer.parseInt(traceSequence[0].split(",")[2]);
            if(level == 1){
                String[] callMethod = traceSequence[0].split(",");
                nodeRoot.setLabel(getClass(callMethod[0]) + "." + callMethod[1]);
                nodeRoot.setPackageName(this.packageName);
                nodeRoot.setBelongsToCluster(false);
                nodeRoot.setIdFather(0);
                nodeRoot.setIdNode(id);
                id++;
                if(callMethod[5].substring(1, callMethod[5].length()).split("]").length > 0) nodeRoot.setParameterValues(callMethod[5].substring(1, callMethod[5].length()).split("]")[0]);
            }else{
                Node tempNode = new Node();
                tempNode.setLevel(level);
                String[] callMethod = traceSequence[0].split(",");
                tempNode.setLabel(getClass(callMethod[0]) + "." + callMethod[1]);
                tempNode.setPackageName(this.packageName);
                tempNode.setBelongsToCluster(false);
                if(callMethod[5].substring(1, callMethod[5].length()).split("]").length > 0) tempNode.setParameterValues(callMethod[5].substring(1, callMethod[5].length()).split("]")[0]);
                nodeRoot.setLabel("Special Node");
                id = insertSpecialNodes(nodeRoot, new LinkedList<Node>(), tempNode, 1,id);
            }
            root.add(nodeRoot);
            this.clusterNumberByLayer.put(1, 1);
        }
        LinkedList<Node> nodes = nodeRoot.getChildren();
        Node node = null;
        Node currentNode = null;
        boolean inserted = false;                        
        for(int i=1;i<traceSequence.length; i++){
            String[] nodeString = traceSequence[i].split(",");
            if(!nodeString[0].startsWith("<SEQ_")){
                node = new Node();
                node.setLabel(getClass(nodeString[0])+"."+nodeString[1]);
                node.setPackageName(this.packageName);
                node.setLevel(Integer.parseInt(nodeString[2]));
                node.setBelongsToCluster(false);
                String[] tempParameterValues = nodeString[5].substring(1, nodeString[5].length()).split("]");
                if(tempParameterValues.length > 0) node.setParameterValues(tempParameterValues[0]);
                if(node.getLevel()==1){
                    nodeRoot = null;
                    node.setIdFather(0);
                    node.setIdNode(id);
                    id++;
                    nodeRoot = node;
                    root.add(nodeRoot);
                }
                else if(nodeRoot.getLevel()+1 == node.getLevel()){
                    nodeRoot.increaseSubNodes();
                    node.setIdFather(nodeRoot.getIdNode());
                    node.setIdNode(id);
                    id++;
                    nodeRoot.addChild(node);
                    setClusterNumberLayer(node);
                }
                else{
                    nodeRoot.increaseSubNodes();
                    nodes = nodeRoot.getChildren();
                    Iterator<Node> rootIterator = nodes.descendingIterator();
                    while(rootIterator.hasNext()){
                        currentNode = rootIterator.next();
                        currentNode.increaseSubNodes();
                        if((currentNode.getLevel()+1) == node.getLevel()) break;
                        else {//if(currentNode.hasChildren()){
                            id = childrenNavigator(currentNode, node, id);
                            inserted = true;
                            break;
                        }
                    }
                    if(inserted == false){
                        if(currentNode != null){
                            node.setIdNode(id);
                            node.setIdFather(currentNode.getIdNode());
                            id++;
                            currentNode.addChild(node);
                            setClusterNumberLayer(node);
                            currentNode=null;
                        }
                        else {
                            int levelNewNode = nodeRoot.getLevel();
                            nodes = nodeRoot.getChildren();
                            id = insertSpecialNodes(nodeRoot, nodes, node, levelNewNode, id);
                        }
                    }
                }
                inserted = false;
            }            
        }        
        traceSequence = null;
        return id;
    }

    private int insertSpecialNodes(Node nodeRoot, LinkedList<Node> nodes, Node node, int levelNewNode, int id) {
        while(levelNewNode < node.getLevel()-1){
                            if(nodes.size()==0){
                                nodeRoot.addChild(new Node());
                                nodeRoot.getChildAt(0).setLabel("Special Node");
                                nodeRoot.getChildAt(0).setLevel(levelNewNode+1);
                                nodeRoot.getChildAt(0).setIdNode(id);
                                id++;
                                nodeRoot.getChildAt(0).increaseSubNodes();
                                nodes = nodeRoot.getChildAt(0).getChildren();
                                nodeRoot = nodeRoot.getChildAt(0);
                            }
                            levelNewNode++;
                        }
        node.setIdFather(id-1);
        node.setIdNode(id);
        nodeRoot.addChild(node);
        return id;
    }

    /**
     * Inserts first node in a tree related with the first line from the trace
     * file
     * @param traceSequence
     * @return nodeRoot
     */
    public Node insertFirstNodeTrace(String[] traceSequence){
        Node nodeRoot = new Node();
        nodeRoot.setLevel(1);
        int level = Integer.parseInt(traceSequence[0].split(",")[2]);
        if(level == 1){
            String[] callMethod = traceSequence[0].split(",");
            nodeRoot.setLabel(getClass(callMethod[0]) + "." + callMethod[1]);
            nodeRoot.setPackageName(this.packageName);
            if(callMethod[5].substring(1, callMethod[5].length()).split("]").length > 0) nodeRoot.setParameterValues(callMethod[5].substring(1, callMethod[5].length()).split("]")[0]);
        }else{
            Node tempNode = new Node();
            tempNode.setLevel(level);
            String[] callMethod = traceSequence[0].split(",");
            tempNode.setLabel(getClass(callMethod[0]) + "." + callMethod[1]);
            tempNode.setPackageName(this.packageName);
            if(callMethod[5].substring(1, callMethod[5].length()).split("]").length > 0) tempNode.setParameterValues(callMethod[5].substring(1, callMethod[5].length()).split("]")[0]);
            nodeRoot.setLabel("Special Node");
            insertSpecialNodes(nodeRoot, new LinkedList<Node>(), tempNode, 1, 1);
        }
        return nodeRoot;
    }

    /**
     * This method is called when it is necessary to navigate into the tree
     * @param nodeRoot
     * @param node
     */
    public void insertFurtherNodes(Node nodeRoot, Node node){
        boolean inserted = false;
        LinkedList<Node> nodes = nodeRoot.getChildren();
        Node currentNode = null;
        nodeRoot.increaseSubNodes();
        Iterator<Node> rootIterator = nodes.descendingIterator();
        while(rootIterator.hasNext()){
            currentNode = rootIterator.next();
            currentNode.increaseSubNodes();
            if((currentNode.getLevel()+1) == node.getLevel()) break;
            else {//if(currentNode.hasChildren()){
                childrenNavigator(currentNode, node, 1);
                inserted = true;
                break;
            }
        }
        if(inserted == false){
            if(currentNode != null){
                currentNode.addChild(node);
                setClusterNumberLayer(node);
                currentNode=null;
            }
            else {
                int levelNewNode = nodeRoot.getLevel();
                nodes = nodeRoot.getChildren();
                insertSpecialNodes(nodeRoot, nodes, node, levelNewNode, 1);
            }
        }
    }

    /**
     *
     * @param currentNode
     * @param newNode
     * @return
     */
    private int childrenNavigator(Node currentNode, Node newNode, int id) {
     Node children = null;
     if(currentNode.hasChildren()){
         children = currentNode.getChildren().getLast();
         children.increaseSubNodes();
         if((children.getLevel()+1) == newNode.getLevel()){
             newNode.setIdFather(children.getIdNode());
             newNode.setIdNode(id);
             id++;
             children.addChild(newNode);
             setClusterNumberLayer(newNode);
             return id;
         }else return childrenNavigator(children, newNode, id);

     }else{
        int level = currentNode.getLevel();

        while(level < newNode.getLevel()-1){
            children = new Node();
            children.setLabel("SpecialNode");
            //children.setLevel(newNode.getLevel()-1);
            children.setLevel(level+1);
            //children.addChild(newNode);
            children.increaseSubNodes();
            children.setIdFather(currentNode.getIdNode());
            children.setIdNode(id);
            id++;
            currentNode.addChild(children);
            currentNode = currentNode.getChildAt(0);
            setClusterNumberLayer(children);
            level++;
        }
        newNode.setIdFather(id-1);
        newNode.setIdNode(id);
        id++;
        children.addChild(newNode);
        setClusterNumberLayer(newNode);
     }     
     return id;
    }


   




}
