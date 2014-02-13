package traceextractor.trace;

import java.io.*;

import org.aspectj.lang.JoinPoint;

import traceextractor.gui.ExtractorControlGui;

public abstract interface TraceFactory {
	TraceRegister createRegister(File outputDir);
	TraceEvent createEventData(JoinPoint joinPoint, TraceEventType eventType, int nestingLevel);
	ExtractorControlGui createControlGui();
}
