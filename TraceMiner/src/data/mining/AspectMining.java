/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package data.mining;

import data.processing.SequenceStructure;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import log.messages.LOG;

/**
 *
 * @author Luciana
 */
public class AspectMining {
    private double support;
    private Map traces;
    private Map sequencesHash;
    private LinkedList<LinkedList<String>> patterns;

    public AspectMining(String[] paths, double support) throws IOException {
        this.support = support;
        traces = new HashMap();
        sequencesHash = new HashMap();
        readTraces(paths);
    }

    /**
     * Reads all traces into the String[] paths
     * @param paths     contains the paths which will be used to mine the sequences
     * @throws IOException
     */
    private void readTraces(String[] paths) throws IOException{
        for(int i = 0; i < paths.length; i++){
            LinkedList<LinkedList<LinkedList<String>>> tracesList = new LinkedList<LinkedList<LinkedList<String>>>();
            LinkedList<LinkedList<LinkedList<String>>> tracesHash = new LinkedList<LinkedList<LinkedList<String>>>();
            File[] files = new File(paths[i]).listFiles()[0].listFiles();
            for(File file : files){
                SequenceStructure sequences = new SequenceStructure(file.getAbsolutePath()+LOG.aspectTraceFile());
                sequences.getTraceSequences();
                tracesList.add(sequences.getStructure());
                GSP gsp = new GSP();
                gsp.getEqualMethod(sequences.getStructure());
                tracesHash.add(gsp.getSequenceList());
            }
            traces.put(i, tracesList);
            sequencesHash.put(i, tracesHash);
        }
    }

    public LinkedList<LinkedList<String>> getPatterns() {
        return patterns;
    }

    public Map getTraces() {
        return traces;
    }

    public void miningSequences(){
        patterns = new LinkedList<LinkedList<String>>();
        GSP gsp = new GSP();
        LinkedList<LinkedList<String>> frequentPatterns = getFirstPatterns();
        LinkedList<LinkedList<String>> sequencePatterns = gsp.combinationK2(frequentPatterns);
        LinkedList<LinkedList<String>> pruneList = frequentSequencePatterns(sequencePatterns);
        frequentPatterns = gsp.prune(sequencePatterns, pruneList);
        while(!frequentPatterns.isEmpty()){
            patterns.addAll(frequentPatterns);
            sequencePatterns = gsp.combination(frequentPatterns);
            pruneList = frequentSequencePatterns(sequencePatterns);
            frequentPatterns = gsp.prune(sequencePatterns, pruneList);
        }
    }

    private LinkedList<LinkedList<String>> frequentSequencePatterns(LinkedList<LinkedList<String>> applicants){
        GSP gsp = new GSP();
        Iterator<LinkedList<String>> applicantIterator = applicants.iterator();
        LinkedList<LinkedList<String>> pruneList = new LinkedList<LinkedList<String>>();
        while(applicantIterator.hasNext()){//For each applicant sequence
            double supportCount = 0;
            LinkedList<String> currentAplicant = applicantIterator.next();
            Iterator<Integer> iterator = sequencesHash.keySet().iterator();
            while(iterator.hasNext()){//For each scenario trace
                LinkedList<LinkedList<LinkedList<String>>> sequenceTrace = (LinkedList<LinkedList<LinkedList<String>>>)sequencesHash.get(iterator.next());
                Iterator<LinkedList<LinkedList<String>>> sequenceListIterator = sequenceTrace.iterator();
                while(sequenceListIterator.hasNext()){//For each sequence of scenario trace
                    boolean counted = false;
                    LinkedList<LinkedList<String>> trace = sequenceListIterator.next();
                    Iterator<LinkedList<String>> iteratorSequence = trace.iterator();
                    while(iteratorSequence.hasNext()){//For each subsequence
                        LinkedList<String> sequence = iteratorSequence.next();
                        boolean exist = gsp.containsSubSequence(currentAplicant, sequence);
                        if(exist){
                            supportCount++;
                            counted = true;
                            break;
                        }
                    }
                    if(counted) break;
                }
            }
            if((supportCount/sequencesHash.entrySet().size()) < support) pruneList.add(currentAplicant);
        }
        return pruneList;
    }

    private LinkedList<LinkedList<String>> getFirstPatterns() {
        LinkedList<String> applicantPatternsC1 = findFirstSequencePatterns();
        LinkedList<String> pruneListC1 = firstFrequentSequencePatterns(applicantPatternsC1);
        GSP gsp = new GSP();
        return (gsp.pruneFirstSequencePatterns(applicantPatternsC1, pruneListC1));
    }

    private LinkedList<String> findFirstSequencePatterns() {
        Iterator<Integer> iterator = sequencesHash.keySet().iterator();
        GSP gsp = new GSP();
        while(iterator.hasNext()){
            LinkedList<LinkedList<LinkedList<String>>> sequences = (LinkedList<LinkedList<LinkedList<String>>>)sequencesHash.get(iterator.next());
            Iterator<LinkedList<LinkedList<String>>> iteratorSequenceTraces = sequences.iterator();
            while(iteratorSequenceTraces.hasNext()){
                gsp.setSequenceList(iteratorSequenceTraces.next());
                gsp.findFirstSequencePatterns();
            }
        }
        return gsp.getPatternsC1();
    }

    private LinkedList<String> firstFrequentSequencePatterns(LinkedList<String> patternsC1) {
        Iterator<String> patternIterator =  patternsC1.iterator();
        LinkedList<String> pruneList = new LinkedList<String>();
        while(patternIterator.hasNext()){//For each pattern
            double supportCount = 0;
            String pattern = patternIterator.next();
            Iterator<Integer> iterator = sequencesHash.keySet().iterator();
            while(iterator.hasNext()){//For each scenario trace
                LinkedList<LinkedList<LinkedList<String>>> sequenceTrace = (LinkedList<LinkedList<LinkedList<String>>>)sequencesHash.get(iterator.next());
                Iterator<LinkedList<LinkedList<String>>> sequenceListIterator = sequenceTrace.iterator();
                while(sequenceListIterator.hasNext()){//For each sequence of scenario trace
                   boolean exist = false;
                   LinkedList<LinkedList<String>> sequences = sequenceListIterator.next();
                   Iterator<LinkedList<String>> sequenceIterator = sequences.iterator();
                   while(sequenceIterator.hasNext()){
                       LinkedList<String> sequence = sequenceIterator.next();
                       if(sequence.contains(pattern)){
                          supportCount++;
                          exist = true;
                          break;
                       }
                   }
                   if(exist) break;
                }
            }
            if((supportCount/sequencesHash.entrySet().size()) < support) pruneList.add(pattern);
        }
        return pruneList;
    }
}
