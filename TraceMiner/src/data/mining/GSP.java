/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.mining;

import data.processing.RecoveryPattern;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Luciana
 */
public class GSP {
    private LinkedList<LinkedList<String>> sequenceList;
    private LinkedList<LinkedList<String>> recoveredPatternList;
    private LinkedList<LinkedList<String>> patterns;
    LinkedList<String> patternsC1;
    private BufferedWriter writer;
    private double minSupport;
    private int size;

    public GSP(double support, String outFile) throws IOException{
        this.minSupport = support;
        this.writer = new BufferedWriter(new FileWriter(outFile));        
    }

    public GSP(double support, int size){
        this.minSupport = support;
        this.size = size;
    }

    public GSP(){
        this.minSupport = 1;
        patternsC1 = new LinkedList<String>();
    }

    public LinkedList<LinkedList<String>> getSequenceList() {
        return sequenceList;
    }

    public void setSequenceList(LinkedList<LinkedList<String>> sequenceList) {
        this.sequenceList = sequenceList;
    }

    public LinkedList<String> getPatternsC1() {
        return patternsC1;
    }

    

    public void getOption(LinkedList<LinkedList<String>> sequences, int option, boolean classOption){
        patternsC1 = new LinkedList<String>();
        if(classOption) {
            miningClasses(sequences);
        }else {
            if(option == 4){
                miningAllInterestingRules(sequences);
            }else{
                if(option == 2) getPrefix(sequences);
                else if(option == 3) getSuffix(sequences);
                else if(option == 1) getEqualMethod(sequences);
                else if(option == 5) getSuffixAndPrefix(sequences);
                if(option != 5) miningSequences();
            }
        }
    }

    /**
     * Used only for option 4
     * @return
     */
    public LinkedList<LinkedList<String>> getRecoveredPatterns(){
        return this.recoveredPatternList;
    }

    private void filter(LinkedList<LinkedList<String>> skeleton, LinkedList<LinkedList<String>> anyPattern) {
        Iterator<LinkedList<String>> skeletonIterator = skeleton.iterator();
        while(skeletonIterator.hasNext()){
            LinkedList<String> sequenceSkeleton = skeletonIterator.next();
            if(!anyPattern.contains(sequenceSkeleton)){
                anyPattern.add(sequenceSkeleton);
            }
        }
    }

    private void finalizePrune(LinkedList<LinkedList<String>> prefix, LinkedList<LinkedList<String>> suffix, LinkedList<LinkedList<String>> recoveredPatterns) {
        int indexRecoveredPattern = 0;
        while(indexRecoveredPattern<recoveredPatterns.size()){
            LinkedList<String> currentPrefix = prefix2(recoveredPatterns.get(indexRecoveredPattern));
            LinkedList<String> currentSuffix = suffix2(recoveredPatterns.get(indexRecoveredPattern));
            if(prefix.contains(currentPrefix) && suffix.contains(currentSuffix)) indexRecoveredPattern++;
            else {
                recoveredPatterns.remove(indexRecoveredPattern);
            }
        }
    }

    private void miningAllInterestingRules(LinkedList<LinkedList<String>> sequences) {
        LinkedList<LinkedList<String>> anyPattern = new LinkedList<LinkedList<String>>();
            LinkedList<LinkedList<String>> recoveredPatterns =  new LinkedList<LinkedList<String>>();
            RecoveryPattern recovery = new RecoveryPattern();
            /* Seeks by the equal names */
            getEqualMethod(sequences);
            miningSequences();
            anyPattern = recovery.getFullMethods(this.patterns, sequences);
            recoveredPatterns = recovery.getMethods();

            getSuffixAndPrefix(sequences);
            filter(recoveredPatterns, this.recoveredPatternList);
            filter(anyPattern, this.patterns);
//            /* Seeks by the prefix */
//            getPrefix(sequences);
//            miningSequences();
//            recovery = new RecoveryPattern();
//            filter(recovery.getSkeleton(this.patterns, sequences), anyPattern);
//            filter(recovery.getMethods(), recoveredPatterns);
//
//            /* Seeks by the suffix */
//            getSuffix(sequences);
//            miningSequences();
//            recovery = new RecoveryPattern();
//            filter(recovery.getSuffixSkeleton(this.patterns, sequences), anyPattern);
//            filter(recovery.getMethods(), recoveredPatterns);
//
//            recoveredPatternList = new LinkedList<LinkedList<String>>();
//            patterns = new LinkedList<LinkedList<String>>();
//            this.patterns.addAll(anyPattern);
//            this.recoveredPatternList.addAll(recoveredPatterns);
    }

    public void miningSequences(){
        patterns = new LinkedList<LinkedList<String>>();
        LinkedList<LinkedList<String>> frequentPatterns = getFirstPatterns();
        LinkedList<LinkedList<String>> sequencePatterns = combinationK2(frequentPatterns);
        LinkedList<LinkedList<String>> pruneList = frequentSequencePatterns(sequencePatterns);
        frequentPatterns = prune(sequencePatterns, pruneList);

        while(!frequentPatterns.isEmpty()){
            patterns.addAll(frequentPatterns);
            sequencePatterns = combination(frequentPatterns);
            pruneList = frequentSequencePatterns(sequencePatterns);
            frequentPatterns = prune(sequencePatterns, pruneList);
        }
       // savePatterns();
    }
    
    /**
     * Gets the sequential patterns recovered by mining
     * @return
     */
    public LinkedList<LinkedList<String>> getPatterns(){
        return this.patterns;
    }


    public LinkedList<LinkedList<String>> combination(LinkedList<LinkedList<String>> frequentPatterns) {
        Iterator<LinkedList<String>> patternIterator = frequentPatterns.iterator();
        LinkedList<LinkedList<String>> sequencePatterns = new LinkedList<LinkedList<String>>();
        while(patternIterator.hasNext()){
            LinkedList<LinkedList<String>> sequences = findSequencePatterns(patternIterator.next(), frequentPatterns);
            if(!sequencePatterns.containsAll(sequences)) sequencePatterns.addAll(sequences);
        }
        return sequencePatterns;
    }

    public LinkedList<LinkedList<String>> combinationK2(LinkedList<LinkedList<String>> frequentPatterns) {
        Iterator<LinkedList<String>> patternIterator = frequentPatterns.iterator();
        LinkedList<LinkedList<String>> sequencePatterns = new LinkedList<LinkedList<String>>();
        while(patternIterator.hasNext()){
            sequencePatterns.addAll(findSequencePatternsK2(patternIterator.next(), frequentPatterns));
        }
        return sequencePatterns;
    }

    public boolean containsSubSequence(LinkedList<String> currentAplicant, LinkedList<String> sequence) {
        int index = sequence.indexOf(currentAplicant.get(0));
        LinkedList<String> sequenceCopy = new LinkedList<String>();
        sequenceCopy.addAll(sequence);
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

    public LinkedList<String> findFirstSequencePatterns() {
        Iterator<LinkedList<String>> sequenceListIterator = this.sequenceList.iterator();
        //LinkedList<String> patternsC1 = new LinkedList<String>();
        patternsC1.add(this.sequenceList.get(0).get(0));
        while(sequenceListIterator.hasNext()){
            LinkedList<String> sequenceIterator = sequenceListIterator.next();
            Iterator<String> item = sequenceIterator.iterator();
            while(item.hasNext()){
                String methodName = item.next();
                if(!patternsC1.contains(methodName)){
                    patternsC1.add(methodName);
                }
            }
        }
        return patternsC1;
    }

    private LinkedList<LinkedList<String>> findSequencePatterns(LinkedList<String> frequentPatterns1,
            LinkedList<LinkedList<String>> frequentPatterns2) {
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        LinkedList<String> tempList = new LinkedList<String>();
        tempList.addAll(frequentPatterns1);
        String firstElement = tempList.remove(0);
        Iterator<LinkedList<String>> iteratorList = frequentPatterns2.iterator();
        while(iteratorList.hasNext()){
            LinkedList<String> pattern = new LinkedList<String>();
            pattern.addAll(iteratorList.next());
            String lastElement = pattern.remove(pattern.size()-1);
            if(isEqual(pattern, tempList)){
                LinkedList<String> acceptedPattern = new LinkedList<String>();
                acceptedPattern.add(firstElement);
                acceptedPattern.addAll(pattern);
                acceptedPattern.add(lastElement);
                patternList.add(acceptedPattern);
            }
        }
        return patternList;
    }

    private boolean isEqual(LinkedList<String> pattern, LinkedList<String> target){
        if(pattern.size()==target.size()){
            for(int i=0; i<pattern.size(); i++){
                if(!pattern.get(i).equals(target.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    private LinkedList<LinkedList<String>> findSequencePatternsK2(LinkedList<String> frequentPatterns1, LinkedList<LinkedList<String>> frequentPatterns2) {
        LinkedList<LinkedList<String>> patternList = new LinkedList<LinkedList<String>>();
        String methodName = frequentPatterns1.get(0);
        Iterator<LinkedList<String>> patternIterator2 = frequentPatterns2.iterator();
        while(patternIterator2.hasNext()){
           LinkedList<String> pattern = new LinkedList<String>();
           pattern.add(methodName);
           pattern.addAll(patternIterator2.next());
           patternList.add(pattern);
        }
        return patternList;
    }



    private LinkedList<String> firstFrequentSequencePatterns(LinkedList<String> patternsC1) {
        Iterator<String> patternIterator =  patternsC1.iterator();
        LinkedList<String> pruneList = new LinkedList<String>();
        while(patternIterator.hasNext()){
            double supportCount = 0;
            String pattern = patternIterator.next();
            Iterator<LinkedList<String>> sequenceListIterator = this.sequenceList.iterator();
            while(sequenceListIterator.hasNext()){
               LinkedList<String> sequence = sequenceListIterator.next();
               if(sequence.contains(pattern)){
                  supportCount++;
               }
            }
            if((supportCount/this.sequenceList.size()) < this.minSupport) pruneList.add(pattern);
        }
        return pruneList;
    }

    private LinkedList<String> firstFrequentSequencePatternsToChameleon(LinkedList<String> patternsC1) {
        Iterator<String> patternIterator =  patternsC1.iterator();
        LinkedList<String> pruneList = new LinkedList<String>();
        while(patternIterator.hasNext()){
            double supportCount = 0;
            String pattern = patternIterator.next();
            Iterator<LinkedList<String>> sequenceListIterator = this.sequenceList.iterator();
            while(sequenceListIterator.hasNext()){
               LinkedList<String> sequence = sequenceListIterator.next();
               if(sequence.contains(pattern)){
                  supportCount++;
               }
            }
            if((supportCount/this.sequenceList.size()) == 0) pruneList.add(pattern);
        }
        return pruneList;
    }

    private LinkedList<LinkedList<String>> frequentSequencePatterns(LinkedList<LinkedList<String>> applicants){
        Iterator<LinkedList<String>> applicantIterator = applicants.iterator();
        LinkedList<LinkedList<String>> pruneList = new LinkedList<LinkedList<String>>();
        while(applicantIterator.hasNext()){
            double support = 0;
            LinkedList<String> currentAplicant = applicantIterator.next();
            Iterator<LinkedList<String>> sequenceListIterator = this.sequenceList.iterator();
            while(sequenceListIterator.hasNext()){
                LinkedList<String> sequence = sequenceListIterator.next();
                boolean exist = containsSubSequence(currentAplicant, sequence);
                if(exist){
                    support++;
                }
            }
            if((support/this.sequenceList.size()) < this.minSupport) pruneList.add(currentAplicant);
        }
        return pruneList;
    }

    private LinkedList<LinkedList<String>> frequentSequencePatternsToChameleon(LinkedList<LinkedList<String>> applicants){
        Iterator<LinkedList<String>> applicantIterator = applicants.iterator();
        LinkedList<LinkedList<String>> frequenceList = new LinkedList<LinkedList<String>>();
        while(applicantIterator.hasNext()){
            double support = 0;
            LinkedList<String> currentAplicant = applicantIterator.next();
            Iterator<LinkedList<String>> sequenceListIterator = this.sequenceList.iterator();
            while(sequenceListIterator.hasNext()){
                LinkedList<String> sequence = sequenceListIterator.next();
                boolean exist = containsSubSequence(currentAplicant, sequence);
                if(exist){
                    support++;
                }
            }
        double frequence = support/this.sequenceList.size();
        if(Math.round(frequence*100)>0) {
            //When this will use hmetis -->>
            //currentAplicant.add(String.valueOf(Math.round(frequence*100)));
            currentAplicant.add(String.valueOf(frequence));
            frequenceList.add(currentAplicant);
        }
            
        }
        
        return frequenceList;
    }

    private LinkedList<LinkedList<String>> getFirstPatterns() {
        LinkedList<String> applicantPatternsC1 = findFirstSequencePatterns();
        LinkedList<String> pruneListC1 = firstFrequentSequencePatterns(applicantPatternsC1);
        return (pruneFirstSequencePatterns(applicantPatternsC1, pruneListC1));
    }

    public LinkedList<LinkedList<String>> prune(LinkedList<LinkedList<String>> sequencePatterns, LinkedList<LinkedList<String>> pruneList) {
        Iterator<LinkedList<String>> prune = pruneList.iterator();
        while(prune.hasNext()){sequencePatterns.remove(prune.next());}
        return sequencePatterns;
    }

    public LinkedList<LinkedList<String>> pruneFirstSequencePatterns(LinkedList<String> patternsC1, LinkedList<String> pruneList) {
        Iterator<String> prune = pruneList.iterator();
        while(prune.hasNext()){ patternsC1.remove(prune.next());}
        Iterator<String> newPattern = patternsC1.iterator();
        LinkedList<LinkedList<String>> frequentPatterns = new LinkedList<LinkedList<String>>();
        while(newPattern.hasNext()){
            LinkedList<String> methodName =  new LinkedList<String>();
            methodName.add(newPattern.next());
            frequentPatterns.add(methodName);
        }
        return frequentPatterns;
    }

    /**
     * Gets prefix from sequences builded through execution SequenceStructure class
     *
     * @param sequences
     */
    public void getPrefix(LinkedList<LinkedList<String>> sequences) {
        this.sequenceList = new LinkedList<LinkedList<String>>();
        for(int i=0; i<sequences.size(); i++){
            //LinkedList<String> sequence = sequences.get(i);
            //this.sequenceList.add(seqList);
            this.sequenceList.add(prefix(sequences.get(i)));
        }
    }

    private LinkedList<String> prefix(LinkedList<String> sequence){
        LinkedList<String> seqList = new LinkedList<String>();
            for(int j=0; j<sequence.size(); j++){
                int index = -1;
                int prefixSize = this.size;
                String[] seq = sequence.get(j).split("-");
                int option = 1;
//                if(classOption) option = 0;
//                else option = 1;
                for(int k=0; k<seq[option].length(); k++){
                    if(!Character.isUpperCase(seq[option].charAt(k))) index = k;
                    else if(prefixSize>1) {prefixSize--; index=k;}
                    else break;
                }
                if(index>0) seqList.add(seq[option].substring(0, index+1));
            }
        return seqList;
    }


    private LinkedList<String> prefix2(LinkedList<String> sequence){
        LinkedList<String> seqList = new LinkedList<String>();
            for(int j=0; j<sequence.size(); j++){
                int index = -1;
                String seq = sequence.get(j);
                for(int k=0; k<seq.length(); k++){
                    if(!Character.isUpperCase(seq.charAt(k))) index = k;
                    else break;
                }
                if(index>0) seqList.add(seq.substring(0, index+1));
            }
        return seqList;
    }

    /**
     * Gets suffix from sequences builded through execution SequenceStructure class
     * @param sequences
     */
    private void getSuffix(LinkedList<LinkedList<String>> sequences) {
        this.sequenceList = new LinkedList<LinkedList<String>>();
        for(int i=0; i<sequences.size(); i++){
//            LinkedList<String> sequence = sequences.get(i);
//            this.sequenceList.add(seqList);
            this.sequenceList.add(suffix(sequences.get(i)));
        }
    }

    private LinkedList<String> suffix(LinkedList<String> sequence){
        LinkedList<String> seqList = new LinkedList<String>();
            for(int j=0; j<sequence.size(); j++){
                int index = -1;
                int prefixSize = this.size;
                String[] seq = sequence.get(j).split("-");
                int option = 1;
//                if(classOption) option = 0;
//                else option = 1;
                for(int k=seq[option].length()-1; k>=0; k--){
                    if(!Character.isUpperCase(seq[option].charAt(k))){
                       index = k;
                    }else if(prefixSize>1) {prefixSize--; index=k;}
                    else break;
                }
                if(index>0) seqList.add(seq[option].substring(index-1,  seq[option].length()));
                else if(index == 0) seqList.add(seq[option]);
            }
        return seqList;
    }


    private LinkedList<String> suffix2(LinkedList<String> sequence){
        LinkedList<String> seqList = new LinkedList<String>();
            for(int j=0; j<sequence.size(); j++){
                int index = -1;
                 String seq = sequence.get(j);
                for(int k=seq.length()-1; k>=0; k--){
                    if(!Character.isUpperCase(seq.charAt(k))){
                       index = k;
                    }else break;
                }
                if(index>0) seqList.add(seq.substring(index-1,  seq.length()));
                else if(index == 0) seqList.add(seq);
            }
        return seqList;
    }


    private void getSuffixAndPrefix(LinkedList<LinkedList<String>> sequences) {
            LinkedList<LinkedList<String>> recoveredPatterns =  new LinkedList<LinkedList<String>>();
            LinkedList<LinkedList<String>> prefix =  new LinkedList<LinkedList<String>>();
            LinkedList<LinkedList<String>> suffix =  new LinkedList<LinkedList<String>>();
            RecoveryPattern recovery = new RecoveryPattern();

            /* Seeks by the prefix */
            getPrefix(sequences);
            miningSequences();
            prefix.addAll(this.patterns);
            recovery = new RecoveryPattern();
            recovery.getSkeleton(this.patterns, sequences);
           // filter(recovery.getSkeleton(this.patterns, sequences), anyPattern);
            filter(recovery.getMethods(), recoveredPatterns);

            /* Seeks by the suffix */
            getSuffix(sequences);
            miningSequences();
            suffix.addAll(this.patterns);
            recovery = new RecoveryPattern();
            recovery.getSuffixSkeleton(this.patterns, sequences);
            //filter(recovery.getSuffixSkeleton(this.patterns, sequences), anyPattern);
            filter(recovery.getMethods(), recoveredPatterns);

            finalizePrune(prefix, suffix, recoveredPatterns);
            recoveredPatternList = new LinkedList<LinkedList<String>>();
            patterns = new LinkedList<LinkedList<String>>();
            this.patterns.addAll(recovery.getFullMethods(recoveredPatterns, sequences));
            this.recoveredPatternList.addAll(recoveredPatterns);
    }

    public void getEqualMethod(LinkedList<LinkedList<String>> sequences) {
        this.sequenceList = new LinkedList<LinkedList<String>>();
        for(int i=0; i<sequences.size(); i++){
            LinkedList<String> sequence = sequences.get(i);
            LinkedList<String> seqList = new LinkedList<String>();
            int option = 1;
//            if(classOption)  option = 0;
//            else option = 1;
            for(int j=0; j<sequence.size(); j++){
                String[] seq = sequence.get(j).split("-");
                seqList.add(seq[option]);
            }                
            this.sequenceList.add(seqList);
        }
    }


    private void savePatterns() throws IOException {
        Iterator<LinkedList<String>> listIterator = patterns.iterator();
        StringBuilder builder = new StringBuilder();
        while(listIterator.hasNext()){
            LinkedList<String> patternList = listIterator.next();
            Iterator<String> pattern = patternList.iterator();
            while(pattern.hasNext()){
                builder.append(pattern.next()+",");
            }
            builder.append("\n");
        }
        this.writer.write(builder.toString());
        this.writer.flush();
        
    }

    private void miningClasses(LinkedList<LinkedList<String>> sequences) {
        classes(sequences);
        miningSequences();
    }

    private void classes(LinkedList<LinkedList<String>> sequences) {
        this.sequenceList = new LinkedList<LinkedList<String>>();
        for(int i=0; i<sequences.size(); i++){
            LinkedList<String> sequence = sequences.get(i);
            LinkedList<String> seqList = new LinkedList<String>();
            for(int j=0; j<sequence.size(); j++){
                String[] seq = sequence.get(j).split("-")[0].split("\\.");
                seqList.add(seq[seq.length-1]);
            }
            this.sequenceList.add(seqList);
        }
    }

}
