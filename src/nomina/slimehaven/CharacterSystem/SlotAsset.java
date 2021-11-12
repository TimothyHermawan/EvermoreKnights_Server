package nomina.slimehaven.CharacterSystem;

import nomina.evermoreknights.SharedClass.ColorRange;

import java.util.ArrayList;
import java.util.List;

public class SlotAsset {
	public String id;
	public int characterType;
	public int slotPairType;
	public List<Integer> elements = new ArrayList<Integer>();
	public ColorRange colorRange;
	public boolean copyColorFromBody;
	
	public SlotData GenerateSlotData()
    {
        SlotData data = new SlotData();
        data.id = this.id;
        data.hsv = colorRange.GenerateColorData();

        return data;
    }
}
