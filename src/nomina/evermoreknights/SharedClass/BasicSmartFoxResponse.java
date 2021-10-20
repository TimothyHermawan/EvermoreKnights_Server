package nomina.evermoreknights.SharedClass;

import com.smartfoxserver.v2.entities.data.SFSObject;

public class BasicSmartFoxResponse {

	public int status;
	public String message;
	public Object data;
	
	public BasicSmartFoxResponse() {
		status = 1;
		message = "";
		data= new Object();
	}
	
	public SFSObject ToSFSObject() {
		
		SFSObject response = new SFSObject();
		
		response.putInt("status", status);
		
		if(message == null || message.isEmpty() || message == "")
			response.putNull("message");
		else		
		response.putUtfString("message", message);
		
		response.putSFSObject("data", SFSObject.newFromJsonData(GeneralUtility.getGson().toJson(data)));		
		
		return response;		
	}
//	
//	public BasicServletResponse ToServletResponse() {
//		BasicServletResponse response = new BasicServletResponse();
//		
//		response.status = status;
//		response.message = message;
//		response.data = data.toJson(); // to exclude SFSObject "dataHolder" and "serializer" mess.
//		
//		return response;
//	}
//	
	
	
}
