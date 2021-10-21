package nomina.evermoreknights.RequestHandler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import nomina.evermoreknights.CurrencySystem.CurrencyManager;
import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.References;

public class PingHandler extends BaseClientRequestHandler {

	@Override
	public void handleClientRequest(User sender, ISFSObject params) {        
		send(References.SmartfoxCMD.Ping, new SFSObject(), sender);
	}

}
