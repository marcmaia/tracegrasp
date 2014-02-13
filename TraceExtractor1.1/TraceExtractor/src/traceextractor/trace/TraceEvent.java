package traceextractor.trace;



public class TraceEvent {
	public static final long STATIC_CALL_OID = -1;
	private long executionTime;
	private long targetOID;
	private long threadId;
	private int nestingLevel;
	private String targetClassName;
	private String eventTypeSignature;
	private TraceEventType eventType;
	private String args;
	private String modifier;
	
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	public long getTargetOID() {
		return targetOID;
	}
	public void setTargetOID(long targetOID) {
		this.targetOID = targetOID;
	}
	public String getTargetClassName() {
		return targetClassName;
	}
	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}
	public String getEventTypeSignature() {
		return eventTypeSignature;
	}
	public void setEventTypeSignature(String eventTypeSignature) {
		this.eventTypeSignature = eventTypeSignature;
	}
	public long getThreadId() {
		return threadId;
	}
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}
	public int getNestingLevel() {
		return nestingLevel;
	}
	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}
	public TraceEventType getEventType() {
		return eventType;
	}
	public void setEventType(TraceEventType eventType) {
		this.eventType = eventType;
	}
	public void setArgs(String args){
		this.args = args;
	}
	public String getArgs(){
		return args;
	}
	public void setModifier(String modifier){
		this.modifier = modifier;
	}
	public String getModifier(){
		return modifier;
	}
	
}
