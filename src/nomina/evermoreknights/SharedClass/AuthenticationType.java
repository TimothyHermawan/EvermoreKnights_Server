package nomina.evermoreknights.SharedClass;

public enum AuthenticationType {
	Login(0),
	Register(1);
	
	private final int value;
	
    private AuthenticationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static AuthenticationType getEnum(int value){
        for (AuthenticationType e:AuthenticationType.values()) {
            if(e.getValue() == value)
                return e;
        }
        return AuthenticationType.Login;//For values out of enum scope
    }
}
