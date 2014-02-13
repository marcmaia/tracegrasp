package traceextractor.monitor.aspectj;

import java.io.File;

import org.aspectj.lang.JoinPoint;

import traceextractor.gui.ExtractorControlGui;
import traceextractor.register.SimpleFileTraceRegister;
import traceextractor.trace.TraceEvent;
import traceextractor.trace.TraceEventBuilder;
import traceextractor.trace.TraceEventType;
import traceextractor.trace.TraceFactory;
import traceextractor.trace.TraceRegister;

public class TraceFactoryArgImpl implements TraceFactory {

	private TraceEventBuilder eventBuilder = new TraceEventBuilder();
	
	public ExtractorControlGui createControlGui() throws SecurityException{
		return new ExtractorControlGui();
	}

	public TraceEvent createEventData(JoinPoint joinPoint,
			TraceEventType eventType, int nestingLevel) {
		return eventBuilder.buildEventData(joinPoint, eventType, nestingLevel);
	}

	public TraceRegister createRegister(File outputDir) {
		if (!outputDir.exists() && !outputDir.mkdir()) {
			System.out.println("Problems while creating output dir for traces: " + outputDir.getPath());
			System.exit(0);
		}
		return new SimpleFileTraceRegister(outputDir);
	}

}
