package nomina.evermoreknights.ZoneExtension;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.RequestHandler.LoginEventHandler;
import nomina.evermoreknights.RequestHandler.TransactionAndInvoiceHandler;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.References;


public class EvermoreKnightsZoneExtension extends SFSExtension {

	@Override
	public void init() {
		
		trace("===== EvermoreKnights ZoneExtension is Started =====");
		
		MongoDBManager.initializeConnection(this);
		
		// SFSEvents
		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);		
		
		// MultiHandlers
		addRequestHandler(References.SmartfoxCMD.Prefix_Transaction, TransactionAndInvoiceHandler.class);
		
		trace("===== EvermoreKnights ZoneExtension is Initialized =====");
	}
	
//	@Override
//    public Object handleInternalMessage(String cmdName, Object params)
//    {
//        Object result = null;
 
//        trace(String.format("Called by: %s, CMD: %s, Params: %s", Thread.currentThread().getName(), cmdName, params));
 
//        if (cmdName.equals(ServletCommand.LOGIN))
//        {
//        	ServletLoginRequestData data = (ServletLoginRequestData) params;        	
//        	result = ServletHandler.Instance().Login(data.username, data.password);        		
//        }
//        
//        // DONE UPDATED
//        else if (cmdName.equals(ServletCommand.TRANSACTION))
//        {
//        	ServletRequestDataTransaction data = (ServletRequestDataTransaction) params;    
//        	
//        	String receiverPid = data.pid;        	
//        	CurrencyValue value = new CurrencyValue(data.type,  data.amount);
//        	
//        	result = CurrencyManager.Instance().DoTransaction(data.pid, value, data.message);
//
//        }
        
//        else if (cmdName.equals(ServletCommand.GET_PLAYER_CURRENCY))
//        {
//        	SRD_GetPlayerCurrency data = (SRD_GetPlayerCurrency) params;
//        	
//        	result = ServletHandler.Instance().GetPlayerCurrency(data.pid);
//        }
//        
//        else if (cmdName.equals(ServletCommand.GET_PLAYER_CURRENCY_RECEIPTS))
//        {
//        	SRD_GetPlayerCurrency data = (SRD_GetPlayerCurrency) params;
//        	
//        	result = ServletHandler.Instance().GetPlayerCurrencyReceipts(data.pid);
//        }
//        
//        BasicServletResponse response = GeneralUtility.ConvertSmartfoxToServletResponse((BasicSmartFoxResponse)result);  
 
//        return response;
//    }
	
	
}
