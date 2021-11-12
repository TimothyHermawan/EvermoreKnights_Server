package nomina.evermoreknights.SharedClass;

import java.util.concurrent.ThreadLocalRandom;

import nomina.slimehaven.CharacterSystem.SlotPairRandomizedColor;

public class ColorRange {
	public Vector2 H;
	public Vector2 S;
	public Vector2 V;
	
	public SlotPairRandomizedColor GenerateColorData()
    {
        SlotPairRandomizedColor result = new SlotPairRandomizedColor();

        result.H = GeneralUtility.RandomFloat(H.x, H.y);
        result.S = GeneralUtility.RandomFloat(S.x, S.y);
        result.V = GeneralUtility.RandomFloat(V.x, V.y);

        return result;
    }
}
