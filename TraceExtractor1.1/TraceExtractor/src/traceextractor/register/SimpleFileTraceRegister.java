package traceextractor.register;

import java.io.*;

import traceextractor.mark.Mark;
import traceextractor.trace.TraceEvent;

public class SimpleFileTraceRegister extends FileTraceRegister {
	private PrintWriter writer;
	private File outputFile;
	
	public SimpleFileTraceRegister(File file) {
		super(file);
	}
	
	/**
	 * Only entry events are considered here.
	 */
	public void registerEvent(TraceEvent eventData) {
		if (!eventData.getEventType().isEntryEvent()) {
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(eventData.getTargetClassName() + TOKEN_SEP);
		//sb.append(eventData.getModifier() + TOKEN_SEP);
		/*if (eventData.getEventType().isConstructor()) {
			sb.append("<init>" + TOKEN_SEP);
		} else {*/
			sb.append(eventData.getEventTypeSignature() + TOKEN_SEP);
		//}
		sb.append(eventData.getNestingLevel() + TOKEN_SEP);
		sb.append(eventData.getTargetOID() + TOKEN_SEP);
		sb.append(eventData.getExecutionTime() + TOKEN_SEP);
		sb.append( OPEN + eventData.getArgs() + CLOSE);		
		getWriter().println(sb.toString());
		getWriter().flush();
	}
	
	public void registerMark(Mark mark) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(MARK_INDICATOR);
		sb.append(mark.type() + TOKEN_SEP);
		sb.append(mark.getName() + TOKEN_SEP);
		sb.append(mark.getTime());
		getWriter().println(sb.toString());
		getWriter().flush();
	}
	
	public void terminate() {
		if (null != writer) {
			writer.flush();
			writer.close();
		}
	}
	
	protected PrintWriter getWriter() {
		if (null == writer) {
			try {
				writer = new PrintWriter(getOutputFile());
			} catch (FileNotFoundException ex) {
				System.out.println("Cannot find file: " + getOutputDir());
			}
			
		}
		return writer;
	}
	
	protected File getOutputFile() {
		if (null == outputFile) {
			outputFile = new File(getOutputDir(), "data.trace");
		}
		
		return outputFile;
	}
}
