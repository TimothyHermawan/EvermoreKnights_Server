package nomina.slimehaven.CharacterSystem;

public enum ElementType {
	Dark(0),
    Earth(1),
    Fire(2),
    Ice(3),
    Light(4),
    Lightning(5),
    Neutral(6),
    Plant(7),
    Water(8),
    Wind(9);
	
	private final int value;
	
    private ElementType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
    public static ElementType getEnum(int value){
        for (ElementType e:ElementType.values()) {
            if(e.getValue() == value)
                return e;
        }
        return ElementType.Dark;//For values out of enum scope
    }
}
