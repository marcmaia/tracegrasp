/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.mining;

import java.util.LinkedList;

/**
 *
 * @author Luciana
 */
public class InterestingPatterns {
    private LinkedList<LinkedList<String>> sequencePatterns;

    public InterestingPatterns() {
        this.sequencePatterns = new LinkedList<LinkedList<String>>();
    }


    public void selectPatterns(){
        quickSort(this.sequencePatterns, 0, this.sequencePatterns.size()-1);
        filterBestPatterns();      
    }

    private void filterBestPatterns() {
        int i=0;
        int j=1;
        boolean contains = false;
        while(i<this.sequencePatterns.size()){
            while(j<this.sequencePatterns.size() &&this.sequencePatterns.get(i).size()==this.sequencePatterns.get(j).size()){
                j++;
            }
            if(j<this.sequencePatterns.size()) contains = isContained(this.sequencePatterns.get(i), this.sequencePatterns.get(j));
            if(contains){
                this.sequencePatterns.remove(i);
                j=i+1;
            }else j++;
            if(j>=this.sequencePatterns.size()){
                i++;
                j=i+1;
            }
        }
    }

    private boolean isContained(LinkedList<String> getI, LinkedList<String> getJ) {
        int count=0;
        int j=0;
        int i=0;
        while(count<getI.size() && i<getI.size() && j<getJ.size()){
            if(getI.get(i).equals(getJ.get(j))){
                i++;
                j++;
                count++;
            }else if(!getI.get(0).equals(getJ.get(j))){
                j++;
                i=0;
                count=0;
            }else {
                i=0;
                count=0;
            }
        }
        if(count == getI.size()){
            return true;
        }
        return false;
    }

    private void quickSort(LinkedList<LinkedList<String>> sequencePatterns, int vetBegin, int vetEnd) {
        int i, j, pivo;
        LinkedList<String> aux = new LinkedList<String>();
        i = vetBegin;
        j = vetEnd;
        pivo = sequencePatterns.get((vetBegin+vetEnd)/2).size();
        while(i<j){
            while(sequencePatterns.get(i).size()<pivo){
            i++;
            }
            while(sequencePatterns.get(j).size()>pivo){
                j--;
            }
            if(i<=j){
                aux = sequencePatterns.get(i);
                sequencePatterns.set(i, sequencePatterns.get(j));
                sequencePatterns.set(j, aux);
                i++;
                j--;
            }
        }
        if(j>vetBegin) quickSort(sequencePatterns, vetBegin, j);
        if(i<vetEnd) quickSort(sequencePatterns, i, vetEnd);
    }
    
    public void setSequentialPatterns(LinkedList<LinkedList<String>> sequential){
        this.sequencePatterns.addAll(sequential);
    }

    public LinkedList<LinkedList<String>> getSequentialPatterns(){
        return this.sequencePatterns;
    }
}
