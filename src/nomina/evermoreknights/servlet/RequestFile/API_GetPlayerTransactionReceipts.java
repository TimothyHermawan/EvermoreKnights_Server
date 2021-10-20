package nomina.evermoreknights.servlet.RequestFile;

import java.io.BufferedReader;
import java.io.IOException;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.extensions.ISFSExtension;

import nomina.evermoreknights.CurrencySystem.CurrencyManager;
import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.servlet.RequestData.SRD_GetPlayerCurrency;
 
@SuppressWarnings("serial")
public class API_GetPlayerTransactionReceipts extends HttpServlet
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
    	BasicSmartFoxResponse result = new BasicSmartFoxResponse();
    	
    	// GET THE REQUEST DATA
    	  StringBuffer jb = new StringBuffer();
    	  String line = null;
    	  try {
    	    BufferedReader reader = request.getReader();
    	    while ((line = reader.readLine()) != null)
    	      jb.append(line);
    	  } catch (Exception e) { /*report an error*/ }
    	  
    	  
    	  

    	  try {
    		  
    		  SRD_GetPlayerCurrency dataRequest = GeneralUtility.getGson().fromJson(jb.toString(), SRD_GetPlayerCurrency.class);
    		  		  
    		  if(dataRequest != null) {
    			  // VERIFY SECRET
        		  MongoDBManager.getInstance().VerifySecret(dataRequest.secret);
    			  
        		  // EXECUTE ACTION
    			  result = CurrencyManager.Instance().GetPlayerCurrencyReceipts(dataRequest.pid);
    		  }else {
    			  result.status = 0;
    			  result.message = "Data Request Error.";
    		  }    		  
    		  
    	  } catch (Exception e) {

    		  result.status = 0;
			  result.message = e.getMessage();
    		  
    	  }finally {
    		  resp.getWriter().write(GeneralUtility.getGson().toJson(result));    		  
    	  }
    	  
    }
}