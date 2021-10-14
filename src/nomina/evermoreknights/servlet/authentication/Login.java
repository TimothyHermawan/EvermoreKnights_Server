package nomina.evermoreknights.servlet.authentication;

import java.io.BufferedReader;
import java.io.IOException;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.ISFSExtension;

import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.servlet.sharedclass.BasicServletResponse;
import nomina.evermoreknights.servlet.sharedclass.ServletCommand;
import nomina.evermoreknights.servlet.sharedclass.ServletLoginRequestData;
 
@SuppressWarnings("serial")
public class Login extends HttpServlet
{
 
    private SmartFoxServer sfs;
    private ISFSExtension myExtension;
 
    @Override
    public void init() throws ServletException
    {
        sfs = SmartFoxServer.getInstance();
        myExtension = sfs.getZoneManager().getZoneByName("EvermoreKnights").getExtension();
    }
 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException
    {
    	BasicServletResponse result = new BasicServletResponse();
    	
    	// GET THE REQUEST DATA
    	  StringBuffer jb = new StringBuffer();
    	  String line = null;
    	  try {
    	    BufferedReader reader = request.getReader();
    	    while ((line = reader.readLine()) != null)
    	      jb.append(line);
    	  } catch (Exception e) { /*report an error*/ }
    	  
    	  
    	  

    	  try {
    		  
    		  ServletLoginRequestData dataRequest = GeneralUtility.getGson().fromJson(jb.toString(), ServletLoginRequestData.class);
    		  
    		  if(dataRequest != null) {
    			// VERIFY SECRET
        		  MongoDBManager.getInstance().VerifySecret(dataRequest.secret);
    			  
    			  result = (BasicServletResponse) myExtension.handleInternalMessage(ServletCommand.LOGIN, dataRequest);
    		  }else {
    			  result.status = 0;
    			  result.message = "Data Request Error.";
    			  result.data = "";
    		  }    		  
    		  
    	  } catch (Exception e) {
    		  
    		  result.status = 0;
			  result.message = e.getMessage();
			  result.data = "";

    	  }finally {
    		  resp.getWriter().write(GeneralUtility.getGson().toJson(result));    		  
    	  }
    	  
    }
}