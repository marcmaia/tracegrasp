/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import data.mining.GSP;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Luciana
 */
public class CompressTree {
    LinkedList<Node> root;

    public CompressTree(LinkedList<Node> root){
        this.root = root;
    }

    public void applyCompressTree() {
       int level=0;
       int existLevelToCompress = 1;
       /* Compresses the repeatable single nodes */
       while(existLevelToCompress>0){
           existLevelToCompress = 0;
           level++;
           existLevelToCompress = getNodeLevel(root, level, existLevelToCompress);
       }
       level=0;
       /* Compresses the repeat sequences greater than one*/
       existLevelToCompress = 1;
       while(existLevelToCompress>0){
           existLevelToCompress = 0;
           level++;
           existLevelToCompress = getSequencesLevel(root, level, existLevelToCompress);
       }
       existLevelToCompress = 1;
       /* Inserts level */
       existLevelToCompress = 1;
       level=0;
       while(existLevelToCompress>0){
           existLevelToCompress = 0;
           level++;
           existLevelToCompress = navigateInTree(root, level, existLevelToCompress);
       }
    }

    private void compressGreaterSequeces(LinkedList<Node> node) {
        LinkedList<String> nodeList = new LinkedList<String>();
        for(Node nodes: node){
            nodeList.add(nodes.getLabel());
        }
        if(nodeList.size()>=4){
            LinkedList<LinkedList<String>> nodesWithoutRepetitions = new LinkedList<LinkedList<String>>();
            /* Takes the nodes without repetitions */
            Iterator<Node> nodeIterator = node.iterator();
            while(nodeIterator.hasNext()){
                Node tempNode = nodeIterator.next();
                LinkedList<String> list = new LinkedList<String>();
                list.add(tempNode.getLabel());
                if(!nodesWithoutRepetitions.contains(list)){
                    nodesWithoutRepetitions.add(list);
                }
            }
            nodesWithoutRepetitions = filterSequences(nodesWithoutRepetitions, nodeList);
            //TODO DELETAR CONJUNTOS CONTIDOS DENTRO DOS CONJUNTOS MAXIMAIS
            if(nodesWithoutRepetitions != null) findSequenceIntervals(nodesWithoutRepetitions, nodeList, node);
        }
    }

    private void compressSequence(int beginning, int indexMark,int support,LinkedList<Node> node, LinkedList<String> nodeList, LinkedList<String> sequence) {
        String nodeName = "[Repeat - "+support+"] - ("+nodeList.get(beginning);
            int mark = beginning;
            int times = sequence.size();
            while(times>1){
                beginning++;
                nodeName = nodeName + " -- "+nodeList.get(beginning);
                times--;
            }
            nodeName = nodeName + ")";
            Node specialNode = new Node();
            specialNode.setLabel(nodeName);
            specialNode.addChildren(node.subList(mark, indexMark));
            times = mark;
            int subNodeNumber = 0;
            while(times<indexMark){
                subNodeNumber = subNodeNumber + node.get(times).getSubNodeNumber();
                times++;
            }
            specialNode.setSubNodeNumber(subNodeNumber);
            node.set(mark, specialNode);
            mark++;
            times = indexMark-mark;            
            while(times>0){
                node.remove(mark);
                nodeList.remove(mark);
                times--;
            }
    }

    private void findSequenceIntervals(LinkedList<LinkedList<String>> repeatedSequences, LinkedList<String> nodeList, LinkedList<Node> node) {
        for(LinkedList<String> sequence : repeatedSequences){
            int beginning = nodeList.indexOf(sequence.get(0));
            int indexMark = beginning;
            int support = 0;
            while((indexMark != -1) && (indexMark + sequence.size()<=nodeList.size())){
                List<String> subList = nodeList.subList(indexMark, indexMark + sequence.size());
                if(subList.equals(sequence)){
                    support++;
                    indexMark = indexMark + sequence.size();
                }else if(support>1){
                    compressSequence(beginning, indexMark, support, node, nodeList, sequence);
                    support = 0;
                    beginning = indexMark;
                }else {
                    support = 0;
                    indexMark++;
                }
            }
            if(node.size()<sequence.size()) break;
            else if(beginning != -1 && indexMark != -1 && support>1) compressSequence(beginning, indexMark, support, node, nodeList, sequence);
        }
    }

    private LinkedList<LinkedList<String>> filterSequences(LinkedList<LinkedList<String>> nodesWithoutRepetitions, LinkedList<String> nodeList) {
        /* Builds the sequences of size 2*/
        LinkedList<LinkedList<String>> patterns = null;
        GSP gsp = new GSP();
        LinkedList<LinkedList<String>> sequenceK2 = gsp.combinationK2(nodesWithoutRepetitions);
        prune(sequenceK2, nodeList);
        if(sequenceK2.size()>0) {
            nodesWithoutRepetitions = existenceOfEachSequence(sequenceK2, nodeList);
            LinkedList<LinkedList<String>> theMostInterestingSequences = frequenceOfSequeces(nodesWithoutRepetitions, nodeList);
            patterns = new LinkedList<LinkedList<String>>();
            while(nodesWithoutRepetitions.get(0).size()<=nodeList.size()/2){
                patterns.addAll(theMostInterestingSequences);
                nodesWithoutRepetitions = gsp.combination(nodesWithoutRepetitions);
                nodesWithoutRepetitions = existenceOfEachSequence(nodesWithoutRepetitions, nodeList);
                theMostInterestingSequences = frequenceOfSequeces(nodesWithoutRepetitions, nodeList);
            }
        }
        return patterns;
    }

    private LinkedList<LinkedList<String>> existenceOfEachSequence(LinkedList<LinkedList<String>> sequenceK2, LinkedList<String> nodeList) {
        LinkedList<LinkedList<String>> exists = new LinkedList<LinkedList<String>>();
        int size = sequenceK2.get(0).size();
        for(int i=0; i<sequenceK2.size(); i++){
            int j=0;
            while(j<j+size){
                List<String> subList;
                if(nodeList.size()>j+size) {
                    subList = nodeList.subList(j, j+size);
                if(sequenceK2.get(i).equals(subList)){
                        if(!exists.contains(sequenceK2.get(i))) exists.add(sequenceK2.get(i));
                        j++;
                        break;
                    }else j++;
                }else break;
                
            }
        }
        return exists;
    }

    private LinkedList<LinkedList<String>> frequenceOfSequeces(LinkedList<LinkedList<String>> nodesWithoutRepetitions, LinkedList<String> nodeList) {
        Iterator<LinkedList<String>> applicantIterator = nodesWithoutRepetitions.iterator();
        LinkedList<LinkedList<String>> mostInterestingSequences = new LinkedList<LinkedList<String>>();
        while(applicantIterator.hasNext()){
            int support = 0;
            LinkedList<String> currentAplicant = applicantIterator.next();
            support = countTimes(currentAplicant, nodeList);
            if(support>1) mostInterestingSequences.add(currentAplicant);
        }
        return mostInterestingSequences;
    }

    private int countTimes(LinkedList<String> currentAplicant, LinkedList<String> nodeList) {
     List<String> nodes = new LinkedList<String>();
     int indexNodeName = nodeList.indexOf(currentAplicant.get(0));
     int support = 0;
     nodes.addAll(nodeList);
     List<String> subNodeList;// = nodes.subList(indexNodeName, indexNodeName + currentAplicant.size());
     while((indexNodeName != -1) && (indexNodeName + currentAplicant.size()<nodes.size())){
        subNodeList = nodes.subList(indexNodeName, indexNodeName + currentAplicant.size());
        if(subNodeList.equals(currentAplicant)){
            support++;
            indexNodeName = indexNodeName + currentAplicant.size();
        }else if(support>1) return support;
        else if(support==0)indexNodeName++;
        if(indexNodeName+1 < nodes.size() && !subNodeList.equals(currentAplicant)){
            nodes = nodes.subList(indexNodeName, nodes.size());
            indexNodeName = nodes.indexOf(currentAplicant.get(0));
            support=0;
        }
     }
     return support;
    }



    private LinkedList<LinkedList<String>> prune(LinkedList<LinkedList<String>> sequencePatterns, LinkedList<String> nodeList) {
        for(int i=0; i<sequencePatterns.size(); i++){
            if(!containsSubSequence(sequencePatterns.get(i), nodeList)){
                sequencePatterns.remove(i);
                i=i-1;
            }
        }
        return sequencePatterns;
    }

    private boolean containsSubSequence(LinkedList<String> currentAplicant, LinkedList<String> nodeList) {
        int index = nodeList.indexOf(currentAplicant.get(0));
        LinkedList<String> sequenceCopy = new LinkedList<String>();
        sequenceCopy.addAll(nodeList);
        if(index != -1){
            List<String> sub = sequenceCopy.subList(index, sequenceCopy.size());
            int size1 = 0;
            while(sub.size()>0 && size1<currentAplicant.size()) {
                if(sub.get(0).equals(currentAplicant.get(size1))) {
                    size1++;
                    sub.remove(0);
                }
                else size1 = 0;
                if(size1==0 && !sub.get(0).equals(currentAplicant.get(size1))) sub.remove(0);
            }
            if(size1==currentAplicant.size()) return true;
        }
        return false;
    }

    private void compressSingleNode(LinkedList<Node> node) {
        for(int i=0; i<node.size(); i++){
            int beginning = i;
            int repeated=1;
            int theEnd = -1;
            for(int j=i+1; j<node.size(); j++){
                if(node.get(i).getLabel().equals(node.get(j).getLabel())){
                    i=j;
                    repeated++;
                }else if(repeated>1){
                    theEnd = j-1;                    
                    break;
                }else break;
            }
            if(repeated>1){
                if(theEnd==-1) theEnd=i;
                dryRepetableNodes(beginning, theEnd, repeated, node);
            }
        }        
    }

    private void dryRepetableNodes(int beginning, int theEnd, int repeated, LinkedList<Node> node) {
        Node tempNode = node.get(beginning);
        Node specialNode = new Node();
        specialNode.setLabel("[Repeat - "+repeated+"] - "+tempNode.getLabel());
        specialNode.setLevelCompressed(tempNode.getLevel());
        specialNode.setSubNodeNumber(tempNode.getSubNodeNumber());
        specialNode.addChild(tempNode);
        node.set(beginning, specialNode);
        beginning++;
        int mark = beginning;
        while(beginning <= theEnd){
            specialNode.addChild(node.get(beginning));
            beginning++;
        }
        while(mark < node.size() && node.get(mark).getLabel().equals(tempNode.getLabel())){
            node.remove(mark);
        }
    }


    private int getNodeLevel(LinkedList<Node> node, int level, int count) {
        if(node.size()>0){
            if(node.get(0).getLevel()==level){
                compressSingleNode(node);
                count++;
            }else{
                for(Node children : node){
                    count = getNodeLevel(children.getChildren(), level, count);
                }
            }
        }
        return count;
    }

    private int getSequencesLevel(LinkedList<Node> node, int level, int count) {
        if(node.size() > 0){
            int i=0;
            boolean compressed = false;
            while(i<node.size() && (node.get(i).getLevel() == -1)){
                if((node.get(i).getLevel() == -1) && ((node.get(i).getLevelCompressed() == level))){
                    compressed = true;
                    break;
                }
                i++;
            }
            if(compressed) return count;
            else if((i<node.size()) && (node.get(i).getLevel()==level)){
                compressGreaterSequeces(node);
                count++;
            }else{
                for(Node children : node){
                    count = getSequencesLevel(children.getChildren(), level, count);
                }
            }
        }
        return count;
    }

    private int navigateInTree(LinkedList<Node> node, int level, int count) {
        if(node.size() > 0){
            int i = 0;
            while((i<node.size()) &&(node.get(i).getLevel() == -1)){i++;}
            if((i<node.size()) && (node.get(i).getLevel()==level)){
                setFakeLevel(node);
                count++;
            }else{
                for(Node children : node){
                    count = navigateInTree(children.getChildren(), level, count);
                }
            }
        }
        return count;
    }

    private void setFakeLevel(LinkedList<Node> nodes) {
        boolean compressed = checkCompress(nodes);
        if(compressed){
            for(int i=0; i<nodes.size(); i++){
                Node node = nodes.get(i);
                if(!node.getLabel().startsWith("[Repeat")){
                    Node tempNode = new Node();
                    tempNode.setLabel("[Repeat - 1] - "+node.getLabel());
                    tempNode.setSubNodeNumber(node.getSubNodeNumber());
                    tempNode.addChild(node);
                    nodes.set(i, tempNode);
                }
            }
        }
    }

    private boolean checkCompress(LinkedList<Node> nodes) {
        for(Node node : nodes){
            if(node.getLabel().startsWith("[Repeat")) return true;
        }
        return false;
    }
}
