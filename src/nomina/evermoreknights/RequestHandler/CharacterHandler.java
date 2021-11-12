package nomina.evermoreknights.RequestHandler;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.StaminaSystem.StaminaManager;
import nomina.slimehaven.CharacterSystem.CharacterManager;
import nomina.slimehaven.CharacterSystem.CharacterType;
import nomina.slimehaven.CharacterSystem.ElementType;

@MultiHandler
public class CharacterHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User sender, ISFSObject params) {
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		String command = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
		
		String pid = sender.getVariable("pid").getStringValue();	
        
		try {

			if (command.equals(References.SmartfoxCMD.Character_GenerateCharacterData)) {
				
				CharacterType charType = CharacterType.getEnum(params.getInt("characterType"));
				ElementType elemType = ElementType.getEnum(params.getInt("elementType"));
				
				response = CharacterManager.Instance().GenerateCharacterData(charType, elemType);
	        }
			
			// COMMAND NOT FOUND
	        else {
	        	response.status = 0;
	        	response.message = "Command could not be found.";
	        }
			
		} catch (Exception e) {

			response.status = 0;
        	response.message = e.getMessage();
        	
        	e.printStackTrace();
			
		} finally {
			send(command, response.ToSFSObject(), sender);
		}

	}

}
