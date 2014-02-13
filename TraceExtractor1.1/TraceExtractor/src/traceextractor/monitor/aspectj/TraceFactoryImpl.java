package traceextractor.monitor.aspectj;

import java.io.File;

import org.aspectj.lang.JoinPoint;

import traceextractor.gui.ExtractorControlGui;
import traceextractor.register.SimpleFileTraceRegister;
import traceextractor.trace.TraceConfiguration;
import traceextractor.trace.TraceEvent;
import traceextractor.trace.TraceEventBuilder;
import traceextractor.trace.TraceEventType;
import traceextractor.trace.TraceFactory;
import traceextractor.trace.TraceRegister;

public class TraceFactoryImpl implements TraceFactory {
	//private TraceConfiguration conf = new TraceConfiguration(); 
	private TraceEventBuilder eventBuilder = new TraceEventBuilder();
	
	public synchronized TraceRegister createRegister(File outputDir) {
		if (!outputDir.exists() && !outputDir.mkdir()) {
				System.out.println("Problems while creating output dir for traces: " + outputDir.getPath());
				System.exit(0);
		}
		
		/*Class clazz = null;
		TraceRegister register = null;
		String registerClass = null;
		
		try {
			registerClass = conf.getProperty("register_class");
			if (null != registerClass) {
				clazz = Class.forName(registerClass);
				register = (TraceRegister)clazz.newInstance();
			}
			
		} catch (InstantiationException ex) {
			System.out.println("InstantiationException while creating register of class: " + registerClass);
			
		} catch (IllegalAccessException ex) {
			System.out.println("IllegalAccessException while creating register of class: " + registerClass);
			
		} catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException while creating register of class: " + registerClass);
			
		} finally {
			if (null == register) {
				System.out.println("Creating SimpleFileTraceRegister!");
				register = new SimpleFileTraceRegister(outputDir);	
			}
		}
		
		return register;*/
		return new SimpleFileTraceRegister(outputDir);
	}
	
	public synchronized TraceEvent createEventData(JoinPoint joinPoint, TraceEventType eventType, int nestingLevel) {
		return eventBuilder.buildEventData(joinPoint, eventType, nestingLevel);
	}
	
	public synchronized ExtractorControlGui createControlGui() throws SecurityException{
		return new ExtractorControlGui();
	}
}
