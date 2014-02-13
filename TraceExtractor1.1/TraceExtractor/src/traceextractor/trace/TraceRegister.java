package traceextractor.trace;

import traceextractor.mark.Mark;

public interface TraceRegister {
	public void registerEvent(TraceEvent eventData);
	public void registerMark(Mark mark);
	public void terminate();
}
