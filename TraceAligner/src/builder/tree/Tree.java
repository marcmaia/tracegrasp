/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import data.handler.CarryFileMemory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import viewer.main.tree.ColapsedTrees;

/**
 *
 * @author Luciana
 */
public class Tree {
    private CarryFileMemory traceFile;
    private LinkedList<Node> root;
    private Map clusterNumberByLayer;
    private String filePath;
    private String packageName;

    public Tree() {
        this.packageName = "";
        this.filePath ="";
        this.root = new LinkedList<Node>();
        this.clusterNumberByLayer = new HashMap();
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

    /**
     *
     */
    public void generateTree() throws Error, IOException, ArrayIndexOutOfBoundsException{
        insertChildren();
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

    public String getClass(String className) {
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
    private void insertChildren() throws Error, IOException, NullPointerException, ArrayIndexOutOfBoundsException{
        String[] traceSequence = this.traceFile.carryCompleteFile();
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
            insertSpecialNodes(nodeRoot, new LinkedList<Node>(), tempNode, 1);
        }
        LinkedList<Node> nodes = nodeRoot.getChildren();
        root.add(nodeRoot);
        this.clusterNumberByLayer.put(1, 1);
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
                String[] tempParameterValues = nodeString[5].substring(1, nodeString[5].length()).split("]");
                if(tempParameterValues.length > 0) node.setParameterValues(tempParameterValues[0]);
                if(node.getLevel()==1){
                    nodeRoot = null;
                    nodeRoot = node;
                    root.add(nodeRoot);
                }
                else if(nodeRoot.getLevel()+1 == node.getLevel()){
                    nodeRoot.increaseSubNodes();
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
                            childrenNavigator(currentNode, node);
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
                            insertSpecialNodes(nodeRoot, nodes, node, levelNewNode);
                        }
                    }
                }
                inserted = false;
            }
        }
        traceSequence = null;
    }

    private void insertSpecialNodes(Node nodeRoot, LinkedList<Node> nodes, Node node, int levelNewNode) {
        while(levelNewNode < node.getLevel()-1){
                            if(nodes.size()==0){
                                nodeRoot.addChild(new Node());
                                nodeRoot.getChildAt(0).setLabel("Special Node");
                                nodeRoot.getChildAt(0).setLevel(levelNewNode+1);
                                nodeRoot.getChildAt(0).increaseSubNodes();
                                nodes = nodeRoot.getChildAt(0).getChildren();
                                nodeRoot = nodeRoot.getChildAt(0);
                            }
                            levelNewNode++;
                        }
                        nodeRoot.addChild(node);
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
            insertSpecialNodes(nodeRoot, new LinkedList<Node>(), tempNode, 1);
        }
        return nodeRoot;
    }

    //TODO: SUBSTITUIR ESTE MÉTODO ABAIXO E OS ANINHADOS PELO ANTIGO.
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
            if((currentNode.getLevel()+1) == node.getLevel()) break;// && ((nodeRoot.getShiftedSequence() == -1) || (nodeRoot.getShiftedSequence() == node.getShiftedSequence()))) break;
            else {//if(currentNode.hasChildren()){
                //PARA ESTE CASO NÃO ESTÁ INSERINDO O NÚMERO DE SUBNODES, MAS PARA ESTE CASO NÃO É PRECISO
                inserted = childrenNavigator(currentNode, node);
//                if(inserted) break;
                inserted = true;
                break;
            }
        }
        if(inserted == false){
            if(currentNode != null){
                currentNode.increaseSubNodes();
                currentNode.addChild(node);
                setClusterNumberLayer(node);
                currentNode=null;
            }
            else {
                int levelNewNode = nodeRoot.getLevel();
                nodes = nodeRoot.getChildren();
                insertSpecialNodes(nodeRoot, nodes, node, levelNewNode);
            }
        }
    }

    public void insertFurtherNodes(Node nodeRoot, Node node, int sequence){
        boolean inserted = false;
        LinkedList<Node> nodes = nodeRoot.getChildren();
        Node currentNode = null;
        nodeRoot.increaseSubNodes();
        Iterator<Node> rootIterator = nodes.descendingIterator();
        while(rootIterator.hasNext()){
            currentNode = rootIterator.next();
            if((currentNode.getLevel()+1) == node.getLevel()) break;// && ((nodeRoot.getShiftedSequence() == -1) || (nodeRoot.getShiftedSequence() == node.getShiftedSequence()))) break;
            else {//if(currentNode.hasChildren()){
                //PARA ESTE CASO NÃO ESTÁ INSERINDO O NÚMERO DE SUBNODES, MAS PARA ESTE CASO NÃO É PRECISO
                if(currentNode.getShiftedSequence() != -1 && currentNode.getShiftedSequence() != sequence && sequence != -1) {
                    currentNode = null;
                    break;
                }else {
                    inserted = childrenNavigator(currentNode, node, sequence);
                    inserted = true;
                    break;
                }
            }
        }
        if(inserted == false){
            if(currentNode != null){
                currentNode.increaseSubNodes();
                currentNode.addChild(node);
                setClusterNumberLayer(node);
                currentNode=null;
            }
            else {
                int levelNewNode = nodeRoot.getLevel();
                nodes = nodeRoot.getChildren();
                insertSpecialNodes(nodeRoot, nodes, node, levelNewNode);
            }
        }
    }

    /**
     *
     * @param currentNode
     * @param newNode
     * @return
     */
    private boolean childrenNavigator(Node currentNode, Node newNode) {
     Node children = null;
     if(currentNode.hasChildren()){
//         children = currentNode.getChildren().getLast();
         Iterator<Node> iterator = currentNode.getChildren().descendingIterator();
         children = iterator.next();
//         children.increaseSubNodes();
         if((children.getLevel()+1) == newNode.getLevel() && ((children.getShiftedSequence() == -1) || (children.getShiftedSequence() == newNode.getShiftedSequence()))){
             children.addChild(newNode);
             setClusterNumberLayer(newNode);
             return true;
         }else if((children.getLevel()+1) == newNode.getLevel() && ((children.getShiftedSequence() != -1) ||children.getShiftedSequence() != newNode.getShiftedSequence())){
             while(iterator.hasNext() && ((children.getShiftedSequence() != -1) || children.getShiftedSequence() != newNode.getShiftedSequence())){
                 children = iterator.next();
             }
             if(((children.getShiftedSequence() == -1) || (children.getShiftedSequence() == newNode.getShiftedSequence()))){
                 children.addChild(newNode);
                 setClusterNumberLayer(newNode);
             }else{
                 children = new Node();
                 children.setLabel("SpecialNode");
                 children.setLevel(currentNode.getLevel()+1);
                 currentNode.addChild(children);
                 currentNode = currentNode.getChildAt(0);
                 children.addChild(newNode);
             }
             return true;
         }else childrenNavigator(children, newNode);

     }else{
        int level = currentNode.getLevel();
        while(level < newNode.getLevel()-1){
            children = new Node();
            children.setLabel("SpecialNode");
            //children.setLevel(newNode.getLevel()-1);
            children.setLevel(level+1);
            //children.addChild(newNode);
            children.increaseSubNodes();
            currentNode.addChild(children);
            currentNode = currentNode.getChildAt(0);
            setClusterNumberLayer(children);
            level++;
        }
        children.addChild(newNode);
        setClusterNumberLayer(newNode);
        return true;
     }
     return false;
    }

    /**
     *
     * @param currentNode
     * @param newNode
     * @param sequence
     * @return
     */
    private boolean childrenNavigator(Node currentNode, Node newNode, int sequence) {
     Node children = null;
     if(currentNode.hasChildren()){
         Iterator<Node> iterator = currentNode.getChildren().descendingIterator();
         children = iterator.next();
         if(children.getShiftedSequence() != -1 && children.getShiftedSequence() != sequence && iterator.hasNext()){
             children = iterator.next();
         }
         if((children.getLevel()+1) == newNode.getLevel() && ((children.getShiftedSequence() == -1) || (children.getShiftedSequence() == sequence))){
             children.addChild(newNode);
             setClusterNumberLayer(newNode);
             return true;
         }else if((children.getLevel()+1) == newNode.getLevel() && (children.getShiftedSequence() != sequence)){
             while(iterator.hasNext() && (children.getShiftedSequence() != sequence) && (children.getShiftedSequence() != -1)){
                 children = iterator.next();
             }
             if((children.getShiftedSequence() == newNode.getShiftedSequence()) || (children.getShiftedSequence() == -1)){
                 children.addChild(newNode);
                 setClusterNumberLayer(newNode);
             }else{
                 children = new Node();
                 children.setLabel("SpecialNode");
                 children.setLevel(currentNode.getLevel()+1);
                 currentNode.addChild(children);
                 currentNode = currentNode.getChildAt(0);
                 children.addChild(newNode);
             }
             return true;
         }else childrenNavigator(children, newNode, sequence);

     }else{
        int level = currentNode.getLevel();
        while(level < newNode.getLevel()-1){
            children = new Node();
            children.setLabel("SpecialNode");
            children.setLevel(level+1);
            children.increaseSubNodes();
            currentNode.addChild(children);
            currentNode = currentNode.getChildAt(0);
            level++;
        }
        if(children == null){
            System.out.println("childrenNavigator da classe tree");
        }
        children.addChild(newNode);
        setClusterNumberLayer(newNode);
        return true;
     }
     return false;
    }
}
