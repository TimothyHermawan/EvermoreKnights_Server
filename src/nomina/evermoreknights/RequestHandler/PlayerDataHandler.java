package nomina.evermoreknights.RequestHandler;

import java.util.List;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.CurrencySystem.*;
import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.CompletePlayerData;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.StaminaSystem.StaminaInfo;
import nomina.evermoreknights.StaminaSystem.StaminaManager;

@MultiHandler
public class PlayerDataHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User sender, ISFSObject params) {
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		String command = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
		
		String pid = sender.getVariable("pid").getStringValue();	
        
		try {

			if (command.equals(References.SmartfoxCMD.Player_GetPlayerData)) {
				
				String targetPid = params.getUtfString("pid");
				
				response = GetCompletePlayerData(targetPid);
	        }
	        
			// COMMAND NOT FOUND
	        else {
	        	response.status = 0;
	        	response.message = "Command could not be found.";
	        }
			
		} catch (Exception e) {

			response.status = 0;
        	response.message = e.getMessage();
			
		} finally {
			send(command, response.ToSFSObject(), sender);
		}
		
	}
	
	private BasicSmartFoxResponse GetCompletePlayerData(String pid) {
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
				
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);	
		
		if(player == null) {
			response.status = 0;
			response.message = "Player data could not be found.";
			
			return response;
		}
		
		StaminaManager.Instance().CheckRegen(pid);
		
		List<CurrencyValue> currencies = CurrencyManager.Instance().GetUserCurrenciesFromDatabase(pid);
		StaminaInfo staminaInfo = StaminaManager.Instance().GetUserStaminaFromDatabase(pid);
		
		CompletePlayerData completeData = new CompletePlayerData();
		
		completeData.playerData = player;
		completeData.currencies = currencies;		
		completeData.staminaInfo = staminaInfo;		
		
		response.status = 1;
		response.message = "Complete player data found.";
		response.data = completeData;			
		return response;
	}



}
