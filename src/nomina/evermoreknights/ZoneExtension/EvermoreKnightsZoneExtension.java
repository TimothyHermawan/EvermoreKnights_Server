package nomina.evermoreknights.ZoneExtension;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.servlet.requesthandler.ServletHandler;
import nomina.evermoreknights.servlet.sharedclass.SRD_GetPlayerCurrency;
import nomina.evermoreknights.servlet.sharedclass.ServletCommand;
import nomina.evermoreknights.servlet.sharedclass.ServletLoginRequestData;
import nomina.evermoreknights.servlet.sharedclass.ServletRequestDataTransaction;

public class EvermoreKnightsZoneExtension extends SFSExtension {

	@Override
	public void init() {
		
		trace("===== EvermoreKnights ZoneExtension is Started =====");
		
		MongoDBManager.initializeConnection(this);
		
		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);		
		
		trace("===== EvermoreKnights ZoneExtension is Initialized =====");
	}
	
	@Override
    public Object handleInternalMessage(String cmdName, Object params)
    {
        Object result = null;
 
        trace(String.format("Called by: %s, CMD: %s, Params: %s", Thread.currentThread().getName(), cmdName, params));
 
        if (cmdName.equals(ServletCommand.LOGIN))
        {
        	ServletLoginRequestData data = (ServletLoginRequestData) params;        	
        	result = ServletHandler.Instance().Login(data.username, data.password);        		
        }
        else if (cmdName.equals(ServletCommand.TRANSACTION))
        {
        	ServletRequestDataTransaction data = (ServletRequestDataTransaction) params;    
        	
        	String receiverPid = data.pid;        	
        	CurrencyValue value = new CurrencyValue(data.type,  data.amount);
        	
        	result = ServletHandler.Instance().CurrencyTransaction(receiverPid, value, data.message);
        }
        else if (cmdName.equals(ServletCommand.GET_PLAYER_CURRENCY))
        {
        	SRD_GetPlayerCurrency data = (SRD_GetPlayerCurrency) params;
        	
        	result = ServletHandler.Instance().GetPlayerCurrency(data.pid);
        }
        else if (cmdName.equals(ServletCommand.GET_PLAYER_CURRENCY_RECEIPTS))
        {
        	SRD_GetPlayerCurrency data = (SRD_GetPlayerCurrency) params;
        	
        	result = ServletHandler.Instance().GetPlayerCurrencyReceipts(data.pid);
        }
 
        return result;
    }
	
	
}
