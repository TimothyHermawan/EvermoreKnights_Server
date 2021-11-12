package nomina.slimehaven.CharacterSystem;

public enum BodyPartType {
	Body(0),
	Wing(1),
	Ear(2),
	Eye(3),
	Mouth(4);
	
private final int value;
	
    private BodyPartType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static BodyPartType getEnum(int value){
        for (BodyPartType e:BodyPartType.values()) {
            if(e.getValue() == value)
                return e;
        }
        return BodyPartType.Body;//For values out of enum scope
    }
}
