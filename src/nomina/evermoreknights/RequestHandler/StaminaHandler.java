package nomina.evermoreknights.RequestHandler;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.StaminaSystem.StaminaManager;

@MultiHandler
public class StaminaHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User sender, ISFSObject params) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		String command = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
		
		String pid = sender.getVariable("pid").getStringValue();	
        
		try {

			if (command.equals(References.SmartfoxCMD.Stamina_AdjustStamina)) {
				
				int adjustment = params.getInt("adjustment");
				
				response = StaminaManager.Instance().AdjustStamina(pid, adjustment);
	        }
			
			else if (command.equals(References.SmartfoxCMD.Stamina_CheckRegen)) {
								
				response = StaminaManager.Instance().CheckRegen(pid);
	        }
	        
			// COMMAND NOT FOUND
	        else {
//	        	command = "";
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

}
