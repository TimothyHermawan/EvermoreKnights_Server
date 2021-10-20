package nomina.evermoreknights.RequestHandler;

import com.smartfoxserver.v2.annotations.MultiHandler;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.CurrencySystem.CurrencyManager;
import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.References;

@MultiHandler
public class TransactionAndInvoiceHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User sender, ISFSObject params) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		String command = params.getUtfString(SFSExtension.MULTIHANDLER_REQUEST_ID);
		
		String pid = sender.getVariable("pid").getStringValue();	
        
		try {

			if (command.equals(References.SmartfoxCMD.Transaction_DoTransaction)) {
				
				int type = params.getInt("type");
				double amount = params.getDouble("amount");
				String message = params.getUtfString("message");
				
				response = CurrencyManager.Instance().DoTransaction(pid, new CurrencyValue(type, amount), message);
	        }
	        
	        else if (command.equals(References.SmartfoxCMD.Transaction_Get_Invoice)) {
	        	
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

}
