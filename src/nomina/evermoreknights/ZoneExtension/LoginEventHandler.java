package nomina.evermoreknights.ZoneExtension;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import nomina.evermoreknights.CurrencySystem.CurrencyManager;
import nomina.evermoreknights.CurrencySystem.CurrencyType;
import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.CustomErrors;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.SharedClass.References;

public class LoginEventHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		ISFSObject parameters = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);		    
		ISFSObject loginResponseOut = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
		ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
		
		String password = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);	    
		String username = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
	    
	    // Force logout first to prevent account-hanging
	    User existingUser = getApi().getUserByName(username);
	    if(existingUser !=null) getApi().logout(existingUser);
	    
		AuthenticationType authenticationType = AuthenticationType.getEnum(parameters.getInt(References.CustomLoginData.Authentication_Type)); 
		
		if(authenticationType == AuthenticationType.Login) response = Login(session, username, password);			
		else response = Register(username, password, parameters);

		if(response.status != 1) {		
			
			short errorCode = response.data.getShort("errorCode");
			
			SFSErrorData errData = new SFSErrorData(CustomErrors.getEnum(errorCode));
			 
			throw new SFSLoginException(response.message, errData);		
		}
		
		PlayerData player = GeneralUtility.ConvertFromSFSObject(response.data, PlayerData.class);
		
		loginResponseOut.putSFSObject("OutData", response.ToSFSObject());
		
		// PREPARING ON LOGIN DATA		
		List<CurrencyValue> currencies = CurrencyManager.Instance().GetUserCurrenciesFromDatabase(player.pid);
		
		// CONVERT TO SMARTFOX DATA
		loginResponseOut.putSFSArray("currencies", GeneralUtility.ConvertListToSFSArray(currencies));		
	}


	private BasicSmartFoxResponse Login(ISession session, String username, String password) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByUsername(username);		
		
		if(player!=null) {
			
			if(getApi().checkSecurePassword(session, player.password, password)) {
				
				// GENERATE NEW TOKEN, MOSTLY FOR WEB API, AND APLLY IT TO PLAYER IN RESPONSE
				String newToken = new ObjectId().toHexString();
				player.token = newToken;	
				player.lastLogin = GeneralUtility.GetCurrentTime().toString();
				
				// UPDATE TOKEN IN THE DB
				MongoCollection<Document> playerCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Players);
				Bson filter_pid = Filters.eq("pid", player.pid);
//				Bson update_token = Updates.set("token", newToken);
				Bson update_lastLogin = Updates.set("lastLogin", player.lastLogin);
				UpdateResult result = playerCollection.updateOne(filter_pid, update_lastLogin);		
				
				if(result.getModifiedCount() >= 1) {	
					// MAKE SURE WE DON'T SEND PASSWORD TO CLIENT
					player.password = "";
					
					// USERNAME AND PASSWORD MATCHED, TOKEN IS UPDATED.
					response.status = 1;
					response.message = "Login success.";
					response.data = GeneralUtility.ConvertToSFSObject(player);
				}else {
					// FAILED UPDATING TOKEN
					response.status = 0;
					response.message = "Failed updating data.";
					response.data.putShort("errorCode", CustomErrors.FAIL_UPDATE_TOKEN.getId());
				}				
				
				return response;
				
			}else {				
				// WRONG PASSWORD				
			}
			
		}else {			
			// PLAYER NOT FOUND
		}
		
		response.status = 0;
		response.message = "Invalid username or password.";
		response.data.putShort("errorCode", CustomErrors.INVALID_CREDENTIAL.getId());
				
		return response;
	}

	public BasicSmartFoxResponse Register(String username, String password, ISFSObject parameters){
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = null;
		
		// CHECK FOR USERNAME AVAILABILITY
		player = MongoDBManager.getInstance().GetPlayerDataByUsername(username);		
		if(player != null) {
			response.status = 0;
			response.message = "Username is already taken. Try another.";
			response.data.putShort("errorCode", CustomErrors.USERNAME_TAKEN.getId());
			return response;
		}
		
		// CHECK FOR EMAIL AVAILABILITY
		String email = parameters.getUtfString(References.CustomLoginData.Register_Email);
		String custom_password = parameters.getUtfString(References.CustomLoginData.Register_Password);
		player = MongoDBManager.getInstance().GetPlayerDataByEmail(email);
		if(player != null) {
			response.status = 0;
			response.message = "Email is already taken. Try another.";
			response.data.putShort("errorCode", CustomErrors.EMAIL_TAKEN.getId());
			return response;
		}
		
		// PREPARING THE CLIENT SESSION
		MongoClient client = MongoDBManager.getInstance().getClient();
		ClientSession session = client.startSession();
		
		try {
			session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
			
			// PREPARE AND INSERT TO PLAYERS COLLECTION
			player = new PlayerData();
			player.username = username;
			player.password = custom_password;
			player.email = email;
			player.pid = new ObjectId().toHexString();
			player.created = GeneralUtility.GetCurrentTime().toString();
			player.lastLogin = player.created;
			player.token = new ObjectId().toHexString();		
			
			MongoCollection<Document> playerCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Players);
			playerCollection.insertOne(session, GeneralUtility.ConvertToDocument(player));
			
			// PREPARE AND INSERT TO CURRENCY COLLECTION
			List<CurrencyValue> currValues = new ArrayList<CurrencyValue>();
			currValues.add(new CurrencyValue(CurrencyType.Evergem.getValue(), 654321));
			currValues.add(new CurrencyValue(CurrencyType.Zenny.getValue(), 123456));
			
			Document currencyDocument = new Document();
			currencyDocument.put("pid", player.pid);
			currencyDocument.put("currencies", GeneralUtility.ConvertToListDocument(currValues));
						
			MongoCollection<Document> currencyCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency);
			currencyCollection.insertOne(currencyDocument);
			
			// MAKE SURE WE DON'T SEND PASSWORD TO CLIENT
			player.password = "";
			
			response.status = 1;
			response.data = GeneralUtility.ConvertToSFSObject(player);
			
			session.commitTransaction();
			
		}catch(Exception e){
			
			e.printStackTrace();
			
			session.abortTransaction();

			response.status = 0;
			response.message = "Transaction Failed";
			response.data = new SFSObject(); // NEED TO RESET, SINCE IT HAS BEEN FILLED BEFORE
			response.data.putShort("errorCode", CustomErrors.TRANSACTION_FAILED.getId());
			
			
		}finally {
			session.close();
		}
		
		
		return response;
	}

}
