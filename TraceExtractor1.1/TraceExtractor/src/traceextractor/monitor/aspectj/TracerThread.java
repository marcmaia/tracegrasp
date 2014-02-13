package traceextractor.monitor.aspectj;

import java.io.File;
import org.aspectj.lang.JoinPoint;

import traceextractor.mark.Mark;
import traceextractor.trace.TraceEvent;
import traceextractor.trace.TraceEventType;
import traceextractor.trace.TraceFactory;
import traceextractor.trace.TraceRegister;

public class TracerThread extends Thread {
	private TraceFactory factory;
	private TraceRegister register;
	private Thread appThread;
	private static int count = 0;
    private File dir;
    private boolean terminated = false;
    private int nestingLevel = 0;
    private int discardCounter = 0;
	
	public TracerThread(Thread appThread, File rootDir, TraceFactory factory) {
		dir = new File(rootDir, "Thread-"+(++count));
		this.register = factory.createRegister(dir);
		this.appThread = appThread;
		this.factory = factory;
	}
	
	public void run() {
		try {
			appThread.join();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		register.terminate();
		terminated = true;
	}
	
	public void finalize() {
		if (!terminated) {
			register.terminate();
		}
	}
	
	public String getThreadName() {
		return appThread.getName();
	}
	
	public long getThreadId() {
		return appThread.getId();
	}
	
	public void registerEvent(JoinPoint joinPoint, TraceEventType eventType) {
		updateNestingLevel(eventType);
		
		if (isDiscardEnabled()) {
			discard(eventType);
//			System.out.println("Discarted...[" + discardCounter + "] + Entry? " + eventType.isEntryEvent());
			return;
		}
		
		TraceEvent event_data = factory.createEventData(joinPoint, eventType, nestingLevel);
		
		if (null == event_data) {
			discard(eventType);
//			System.out.println("Discarted...[" + discardCounter + "] + Entry? " + eventType.isEntryEvent());
			return;
		}
		
		register.registerEvent(event_data);
	}
	
	public void registerMark(Mark mark) {
		register.registerMark(mark);
	}
	
	private void updateNestingLevel(TraceEventType eventType) {
		if (eventType.isEntryEvent()) {
			nestingLevel++;
		} else {
			nestingLevel--;
		}
	}
	
	private boolean isDiscardEnabled() {
		return discardCounter > 0;
	}
	
	private void discard(TraceEventType eventType) {
		if (eventType.isEntryEvent()) {
			discardCounter++;
		} else {
			discardCounter--;
		}
	}
}

