package nomina.evermoreknights.SharedClass;

import java.util.ArrayList;
import java.util.List;

import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.StaminaSystem.StaminaInfo;

public class CompletePlayerData {
	public PlayerData playerData;	
	public List<CurrencyValue> currencies = new ArrayList<CurrencyValue>();
	public StaminaInfo staminaInfo;
}
