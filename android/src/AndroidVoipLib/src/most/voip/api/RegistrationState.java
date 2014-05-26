package most.voip.api;

public enum RegistrationState {
	REQUEST_TIMEOUT("REQUEST_TIMEOUT", 408),	
	FORBIDDEN("FORBIDDEN",403),
    NOT_FOUND("NOT_FOUND",404),
    OK("REGISTERED",200),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE",503);
	
	private String stringValue;
    private int intValue;
    
    private RegistrationState(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }
    
    public int intValue() {
    	return intValue;
    }
    
    @Override
    public String toString() {
        return stringValue;
    }
}
