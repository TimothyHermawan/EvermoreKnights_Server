package nomina.evermoreknights.CurrencySystem;

public enum CurrencyType {
	Evergem(0),
	Zenny(1);
	
	private final int value;
	
    private CurrencyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static CurrencyType getEnum(int value){
        for (CurrencyType e:CurrencyType.values()) {
            if(e.getValue() == value)
                return e;
        }
        return CurrencyType.Zenny;//For values out of enum scope
    }
}
