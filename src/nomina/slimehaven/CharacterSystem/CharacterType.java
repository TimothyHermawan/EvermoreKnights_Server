package nomina.slimehaven.CharacterSystem;

public enum CharacterType {
	Slime(0);
	
	private final int value;
	
    private CharacterType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static CharacterType getEnum(int value){
        for (CharacterType e:CharacterType.values()) {
            if(e.getValue() == value)
                return e;
        }
        return CharacterType.Slime;//For values out of enum scope
    }
}
