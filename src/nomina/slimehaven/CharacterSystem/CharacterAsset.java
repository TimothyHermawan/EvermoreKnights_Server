package nomina.slimehaven.CharacterSystem;

import java.util.ArrayList;
import java.util.List;

import nomina.evermoreknights.SharedClass.GeneralUtility;

public class CharacterAsset {
	public int charType;
	public int element;
	public List<Integer> randomizedBodyParts = new ArrayList<Integer>();
	
	
	public CharacterData GenerateCharacterData()
    {
        CharacterData data = new CharacterData();
        data.characterType = charType;
        data.element = element;

        SlotData bodyData = null;

        // MAKE SURE "BODY" GOES FIRST AS IT MAY BE A "BASE" FOR THE OTHER PART
        for(int part : randomizedBodyParts)
        {
            SlotAsset target = CharacterManager.Instance().GetRandomBodyPart(CharacterType.getEnum(charType), ElementType.getEnum(element), BodyPartType.getEnum(part));

            SlotData slotData = target.GenerateSlotData();

            if (target.slotPairType == BodyPartType.Body.getValue()) bodyData = slotData;

            if (target.copyColorFromBody && bodyData != null)
                slotData.hsv = GeneralUtility.DeepCopyJSON(bodyData.hsv, SlotPairRandomizedColor.class);

            data.bodyParts.add(slotData);
        }

        return data;
    }
	
}
