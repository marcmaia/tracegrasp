package tracecompressor;


import java.awt.Graphics;
import java.io.*;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.devlib.schmidt.CopyFile;

public class Compressor {
	private EventEqualityCondition condition = new EventEqualityCondition();
	private int removedRecursionCalls = 0;
	private int removedLoopCalls = 0;
	private int removedSequenceCalls = 0;
	private int sequencesFound = 0;
	private int totalOfCallsInOriginal = 0;
	private File lastOriginal = null;
	private File lastCompressed = null;
        private File compressedFile = null;
        private String progressTask = "";
	
//	public static void main(String[] args) {
//		Compressor compressor = new Compressor();
////		for (int i = 4; i <= 4; i++) {
////			for (int j = 1; j <=6; j++) {
////				compressor.buildCompressedFromTraceDir(new File("F:\\Base de dados para Chameleon\\Trace Diagrama de Atividades\\"));
////                        }
////		}
//		//compressor.buildCompressedFromTraceDir(new File("F:\\Base de dados para Chameleon\\Merge\\"));
//                compressor.buildCompressedFromTraceDir(new File("F:\\Base de dados para Chameleon\\Traces\\Trace Diagrama de Atividades\\"));
//	}

        public File getCompressedFile(){
            return this.compressedFile;
        }

        public String getProgressTask(){
            return this.progressTask;
        }
        
	public StringBuilder buildCompressedFromTraceDir(File dir, String signal, JPanel panel, JLabel label) {
            Graphics g = panel.getGraphics();
            File output_dir = null;
            if(dir.getAbsolutePath().endsWith("Raw")) output_dir = new File(dir.getAbsolutePath().substring(0, dir.getAbsolutePath().length()-3)+"Compressed");
            else output_dir = new File(dir.getPath() + "-Compressed");
                StringBuilder builder = new StringBuilder();
//		System.out.println("#########################################################################");
//		System.out.println("Compressing files for dir: " + dir);
                builder.append("\nCompressing files for dir: \n" + dir);

//		if (output_dir.exists()) {
//			//System.out.println("Can't build output dir! The directory already exists: " + output_dir.getPath());
//                    builder.append("\nCan't build output dir! The directory already exists: \n" + output_dir.getPath());
//			//return;
//                        //return builder;
//		}
		
		if (!output_dir.mkdir()) {
			//System.out.println("Can't build output dir! Problems during creation: " + output_dir.getPath());
                    builder.append("\nCan't build output dir! Problems during creation: \n" + output_dir.getPath());
			//return builder;
		}
		//System.out.println("Output dir created: " + dir);
                builder.append("\nOutput dir created: \n" + dir);
		
		File[] trace_dir_files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		
		for (File f : trace_dir_files) {
			try {
				CopyFile.copyFile(f, new File(output_dir, f.getName()));
				//System.out.println("File copied to output dir: " + f.getName());
                                builder.append("\nFile copied to output dir: \n" + f.getName());
				
			} catch (IOException ex) {
				//System.out.println("Can't copy to output dir: " + f.getName());
                            builder.append("\nCan't copy to output dir: \n" + f.getName());
				ex.printStackTrace();
			}
		}
		
		File[] thread_dirs = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() && file.getName().startsWith("Thread");
			}
		});
		
		for (File thread_dir : thread_dirs) {
			File trace_file = new File(thread_dir, "data.trace");
			label.setText("Executing the Compressor in "+ trace_file.getParentFile().getName()+"\\"+trace_file.getName());
            panel.paint(g);
			if (trace_file.exists()) {
				File output_thread_dir = new File(output_dir, thread_dir.getName());
				if (output_thread_dir.mkdir()) {
					buildCompressedFrom(trace_file, new File(output_thread_dir.getPath(), trace_file.getName()));
					builder.append(printStatistics());
				
				}
                                File fileExists = null;
                                fileExists = new File(output_thread_dir.getAbsolutePath()+signal+"data.trace");
                                if(!fileExists.exists()){
                                    buildCompressedFrom(trace_file, new File(output_thread_dir.getPath(), trace_file.getName()));
                                    builder.append(printStatistics());
                                    //builder.append("\nCan't create output thread dir: \n" + output_thread_dir);
				//	System.out.println("Can't create output thread dir: " + output_thread_dir);
				}
				
				
			} else {
				//System.out.println("Trace file not found in: " + thread_dir.getPath());
                            builder.append("\nTrace file not found in: \n" + thread_dir.getPath());
			}
			
		}
                //this.compressedFile = new File(dir.getPath() + "-Compressed"+signal+dir.listFiles()[0].getName());
        this.compressedFile = output_dir;
		//System.out.println("#########################################################################");
                return builder;
	}
	
	public StringBuilder printStatistics() {
		java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		
		int total_removed = removedRecursionCalls + removedLoopCalls + removedSequenceCalls;
		int total_of_calls_in_compressed = totalOfCallsInOriginal - total_removed;
		nf.setMaximumFractionDigits(2);
//
//		System.out.println("======================================================");
//		System.out.println("Last Original File: " + lastOriginal);
//		System.out.println("Last Compressed File: " + lastCompressed);
//		System.out.println("Removed Recursion calls: " + nf.format(removedRecursionCalls));
//		System.out.println("Removed Loop calls: " + nf.format(removedLoopCalls));
//		System.out.println("Removed Sequence calls: " + nf.format(removedSequenceCalls));
//		System.out.println("Sequences found: " + nf.format(sequencesFound));
//		System.out.println("Total of calls removed: " + nf.format(total_removed));
//		System.out.println("Total of calls in original file: " + nf.format(totalOfCallsInOriginal));
//		System.out.println("Total of calls in compressed file: " + nf.format(total_of_calls_in_compressed));
//		System.out.println("Compression ratio: " + nf.format((1-(total_of_calls_in_compressed/(float)totalOfCallsInOriginal))*100) + "%");
//		System.out.println("======================================================");
//
                StringBuilder statistic = new StringBuilder();
                statistic.append("\n======================================================");
                statistic.append("\nLast Original File: " + lastOriginal);
		statistic.append("\nLast Compressed File: " + lastCompressed);
		statistic.append("\nRemoved Recursion calls: " + nf.format(removedRecursionCalls));
		statistic.append("\nRemoved Loop calls: " + nf.format(removedLoopCalls));
		statistic.append("\nRemoved Sequence calls: " + nf.format(removedSequenceCalls));
		statistic.append("\nSequences found: " + nf.format(sequencesFound));
		statistic.append("\nTotal of calls removed: " + nf.format(total_removed));
		statistic.append("\nTotal of calls in original file: " + nf.format(totalOfCallsInOriginal));
		statistic.append("\nTotal of calls in compressed file: " + nf.format(total_of_calls_in_compressed));
		statistic.append("\nCompression ratio: " + nf.format((1-(total_of_calls_in_compressed/(float)totalOfCallsInOriginal))*100) + "%");
		statistic.append("\n======================================================");
                return statistic;
	}
	
	public File buildCompressedFrom(File file, File compressed) {
		removedRecursionCalls = 0;
		removedLoopCalls = 0;
		removedSequenceCalls = 0;
		sequencesFound = 0;
		totalOfCallsInOriginal = 0;
		lastOriginal = file;
		lastCompressed = compressed;
		
		try {
			compressed.createNewFile();
			applyCompression(file, compressed);
			
		} catch (IOException ex) {
			ex.printStackTrace();
			
		}
		
		return compressed;
	}
	
	public File buildCompressedFrom(File file) {
		File compressed = new File(file.getParentFile(), "compressed-" + file.getName());
		buildCompressedFrom(file, compressed);
		
		return compressed;
	}
	
	private void applyCompression(File original, File target) {
		List<TraceEvent> not_redundant_events = getNotRedundantCallsFrom(original);
		try {
			int k = 2;  // Algorithm iterations
			int d = 75; // Maximum sequence size
			for (int m = 0; m < k; m++) {
				removeRedundantCallsSequence(not_redundant_events, d);
			}
			
			PrintWriter writer = new PrintWriter(target);
			for (TraceEvent e : not_redundant_events) {
				writer.print(e.convertToLine());
			}
			writer.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
			
		}
	}
	
	private List<TraceEvent> getNotRedundantCallsFrom(File original) {
		ArrayList<TraceEvent> not_redundant_calls = new ArrayList<TraceEvent>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(original));
			
			String line = null;
			String last = null;
			
			while (null != (line = reader.readLine())) {
				
				if (isCallRepetition(line, last)) {
					removedLoopCalls++;
					
				} else if (isCallRecursion(line, last)) {
					removedRecursionCalls++;
				}
				else {
					not_redundant_calls.add(TraceEvent.parseFromLine(line));
				}
				
				last = line;
				totalOfCallsInOriginal++;
			}
			
			reader.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
			
		}
		
		return not_redundant_calls;
	}
	
	private boolean isCallRepetition(String line, String last) {
		if (null == line
				|| null == last) {
			return false;
		}
		
		TraceEvent e = TraceEvent.parseFromLine(line);
		TraceEvent l_e = TraceEvent.parseFromLine(last);
		
		return (e.getClassQualifiedName().equals(l_e.getClassQualifiedName())
				&& e.getCallName().equals(l_e.getCallName())
				&& e.getCallNesting() == l_e.getCallNesting());
	}
	
	private boolean isCallRecursion(String line, String last) {
		if (null == line
				|| null == last) {
			return false;
		}
		
		TraceEvent e = TraceEvent.parseFromLine(line);
		TraceEvent l_e = TraceEvent.parseFromLine(last);
		
		return (e.getClassQualifiedName().equals(l_e.getClassQualifiedName())
				&& e.getCallName().equals(l_e.getCallName())
				&& e.getCallNesting() == l_e.getCallNesting() + 1);
	}
	
	private void removeRedundantCallsSequence(List<TraceEvent> events, int d) {
		int start = 0;
		int i = 0;
		while (i < events.size()) {
			List<Integer> j_candidates = findRedundanceIndexes(events, i, start, d); // Search for starter index of a potential sequence redundance
			if (j_candidates.size() == 0) {
				i++;
				
			} else { 
				boolean sequence_matched = false;
				Iterator<Integer> j_it = j_candidates.iterator();
				
				while (j_it.hasNext()
						&& !sequence_matched) {
					
					int j = j_it.next();
					
					SequenceTrace sequence = SequenceTrace.buildSequence(events, j, i-1); // build a sequence from the found index
					int offset = i;
					boolean matched = false;
					
					do {
						matched = isMatched(events, sequence, offset); // try to match the sequence found with the traces ahead
						if (matched) {
							sequence.incrementOcurrences();
							removeRedundantSequence(events, offset, sequence.getDirectChildsQtd()); // remove redundant occurrences of the build sequence
						}
					} while (matched); // repeat the procedure to find any additional continuous sequence  
					
					if (sequence.getOcurrences() > 1) { // if redundant continuous sequences were found, remove the remained sequence (first) and replace with a surrogate
						removeRedundantSequence(events, j, sequence.getDirectChildsQtd());
						sequence.incrementCallNesting();
						events.add(j, sequence);
						start = j + 1;
						i = j + 1;
						sequence_matched = true;
	
						// Statistics /////////////////////////////////////////////////////////////
						this.sequencesFound++; // only for statistics
						this.removedSequenceCalls += (sequence.getOcurrences() - 1)*sequence.getDirectChildsQtd(); // only for statistics
						///////////////////////////////////////////////////////////////////////////
					}
				}
				
				if (!sequence_matched) {
					i++;
				}
			}
		}
	}
	
	private List<Integer> findRedundanceIndexes(List<TraceEvent> traces, int i, int start, int d) {
		List<Integer> j_candidates = new ArrayList<Integer>();
		int j = start;
		TraceEvent ei = traces.get(i);
		while (j < i) { 
			if ((i-j) <= d) {
				TraceEvent ej = traces.get(j);
				if (ei.equalsWithCondition(ej, getCondition())) {
					j_candidates.add(j);
				}
			}
			j++;
		}
		return j_candidates;
	}
	
	private EventEqualityCondition getCondition() {
		return condition;
	}
	
	private boolean isMatched(List<TraceEvent> traces, SequenceTrace sequence, int offset) {
		if (offset > traces.size()-1
				|| offset + sequence.getDirectChildsQtd() > traces.size()-1) {
			return false;
		}
		
		Iterator<TraceEvent> it = sequence.getIterator();
		boolean matched = true;
		
		// Checking if the sequence match
		while (it.hasNext()) {
			TraceEvent event = it.next();
			//if (!condition.isSatisfied(event, traces.get(offset))) {
			if (!event.equalsWithCondition(traces.get(offset), condition)) {
				matched = false;
				break;
			}
			offset++;
		}
		return matched;
	}
	
	private void removeRedundantSequence(List<TraceEvent> traces, int offset, int sequenceSize) {
		int count = sequenceSize;
		while (count > 0) {
			traces.remove(offset);
			count--;
		}
	}
}

class TraceEvent {
	private String classQualifiedName;
	private String callName;
	private int callNesting;
	private int targetObjectId;
	private long executionTime;
        private String parameterValues;
	
	public String getClassQualifiedName() {
		return classQualifiedName;
	}

	public void setClassQualifiedName(String classQualifiedName) {
		this.classQualifiedName = classQualifiedName;
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public int getCallNesting() {
		return callNesting;
	}

	public void setCallIdent(int callIdent) {
		this.callNesting = callIdent;
	}

	public void incrementCallNesting() {
		this.callNesting++;
	}
	
	public int getTargetObjectId() {
		return targetObjectId;
	}

	public void setObjectId(int objectId) {
		this.targetObjectId = objectId;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

        public String getParameterValues(){
            return parameterValues;
        }

        public void setParameterValues(String paramaterValues){
            this.parameterValues = paramaterValues;
        }

	public String convertToLine() {
		return TraceEvent.convertToLine(this);
	}
	
	public boolean equalsWithCondition(TraceEvent trace, EventEqualityCondition condition) {
		if (!(trace instanceof TraceEvent)) {
			return false;
		}
		return condition.isEqual(this, trace);
	}
	
	public static TraceEvent parseFromLine(String line) {
		TraceEvent event = new TraceEvent();
		String[] tokens = line.split(",");
		event.setClassQualifiedName(tokens[0]);
		event.setCallName(tokens[1]);
		event.setCallIdent(Integer.parseInt(tokens[2]));
		event.setObjectId(Integer.parseInt(tokens[3]));
		event.setExecutionTime(Long.parseLong(tokens[4]));
                event.setParameterValues(tokens[5]);
		
		return event;
	}
	
	public static String convertToLine(TraceEvent event) {
		StringBuilder sb = new StringBuilder();
		sb.append(event.getClassQualifiedName() + ",");
		sb.append(event.getCallName() + ",");
		sb.append(event.getCallNesting() + ",");
		sb.append(event.getTargetObjectId() + ",");
		sb.append(event.getExecutionTime() + ",");
		sb.append(event.getParameterValues());
                
		return sb.toString() + "\n";
	}
}

class SequenceTrace extends TraceEvent {
	private ArrayList<TraceEvent> events;
	private int ocurrences = 0;
	
	private SequenceTrace() {
		events = new ArrayList<TraceEvent>();
		ocurrences++;
	}
	
	public boolean equalsWithCondition(TraceEvent trace, EventEqualityCondition condition) {
		if (!(trace instanceof SequenceTrace)) {
			return false;
		}
		SequenceTrace ts = (SequenceTrace) trace;
			
		if (events.size() != ts.events.size()) {
			return false;
		}
		
		for (int i = 0; i < events.size(); i++) {
		    TraceEvent e_this = events.get(i);
		    TraceEvent e_trace = ts.events.get(i);
		           if (!e_this.equalsWithCondition(e_trace, condition))
		          return false;
		}
		return true;
	}
	
	public void incrementCallNesting() {
		super.incrementCallNesting();
		for (TraceEvent ev : events) {
			ev.incrementCallNesting();
		}
	}
	
	private void add(TraceEvent event) {
		events.add(event);
	}
	
	public Iterator<TraceEvent> getIterator() {
		return events.iterator();
	}
	
	public void incrementOcurrences() {
		ocurrences++;
	}
	
	public int getOcurrences() {
		return ocurrences;
	}
	
	public int getDirectChildsQtd() {
		return events.size();
	}
	
	public static SequenceTrace buildSequence(List<TraceEvent> traces, int start, int end) {
		SequenceTrace sequence = new SequenceTrace();
		for (int i = start; i <= end; i++) {
			sequence.add(traces.get(i));
		}
		sequence.setCallIdent(sequence.events.get(0).getCallNesting() - 1);
		return sequence;
	}
	
	public String convertToLine() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<SEQ_START:" + getOcurrences() + "> \n");
		for (TraceEvent e : events) {
			sb.append(e.convertToLine());
		}
		sb.append("<SEQ_END> \n");
		return sb.toString();
	}
	
	/*public int getCalleeOid() {
		return events.get(0).getNestingLevel();
	}

	public Class getCalleeType() {
		return events.get(0).getCalleeType();
	}

	public int getCallerOid() {
		return events.get(0).getCallerOid();
	}

	public Class getCallerType() {
		return events.get(0).getCallerType();
	}

	public EventType getEventType() {
		return events.get(0).getEventType();
	}

	public long getExecutionTime() {
		return events.get(0).getExecutionTime();
	}

	public int getId() {
		return events.get(0).getId();
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public long getThreadId() {
		return events.get(0).getThreadId();
	}*/
}

class EventEqualityCondition  {
	
	public boolean isEqual(TraceEvent a, TraceEvent b) {
		return a.getCallNesting() == b.getCallNesting()
			/*&& previous.getCallerOid() ==  current.getCallerOid()
			&& previous.getCalleeOid() == current.getCalleeOid()*/
			//&& a.getTargetObjectId() ==  b.getTargetObjectId()
			&& a.getClassQualifiedName().equals(b.getClassQualifiedName())
			&& a.getCallName().equals(b.getCallName())
			/*&& previous.getCallerType().equals(current.getCallerType())*/;
	}
}