package nomina.evermoreknights.SharedClass;

import com.smartfoxserver.v2.entities.data.SFSObject;

public class BasicSmartFoxResponse {

	public int status;
	public String message;
	public SFSObject data;
	
	public BasicSmartFoxResponse() {
		status = 1;
		message = "";
		data= new SFSObject();
	}
	
	public SFSObject ToSFSObject() {
		
		SFSObject response = new SFSObject();
		
		response.putInt("status", status);
		
		if(message == null || message.isEmpty() || message == "")
			response.putNull("message");
		else		
		response.putUtfString("message", message);
		
		response.putSFSObject("data", data);		
		
		return response;		
	}
	
	
	
}
