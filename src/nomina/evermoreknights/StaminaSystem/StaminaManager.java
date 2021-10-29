package nomina.evermoreknights.StaminaSystem;

import java.util.logging.Level;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;

import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.SharedClass.References;

public class StaminaManager {
	
	private static StaminaManager instance;
	
	public static StaminaManager Instance() {
		if (instance == null) {
			synchronized (StaminaManager.class) { // thread-safe
				if (instance == null) instance = new StaminaManager();				
			}
		}
		
		return instance;
	}
	
	public void SendStaminaUpdateToUserByPID(String pid) {		
		
		SmartFoxServer sfs = SmartFoxServer.getInstance();
		
		User user = GeneralUtility.GetUserByPID(pid);
		
		if(user != null && sfs.getUserManager().containsUser(user)) {						
			BasicSmartFoxResponse response = GetPlayerStamina(pid);		
			
			sfs.getExtensionManager().getZoneExtension(user.getZone()).send(References.SmartfoxCMD.Update_Stamina, response.ToSFSObject(), user);
		}		
	}
	
	public void InsertInitialStamina(String pid) {	
		// Check if the player has the currency initialized
		if(IsInitializedForPlayer(pid)) return;
		
		StaminaInfo info = new StaminaInfo();
		info.stamina = References.StaminaSettings.Max_Value;
		info.lastRegenTime = GeneralUtility.GetCurrentTime().toString();
		
		Document staminaDocument = GeneralUtility.ConvertToDocument(info);
		staminaDocument.put("pid", pid);
					
		MongoCollection<Document> staminaCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Stamina);
		staminaCollection.insertOne(staminaDocument);
	}
	
	public BasicSmartFoxResponse CheckRegen(String pid) {	
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		StaminaInfo info = GetUserStaminaFromDatabase(pid);
		
		// Player StaminaInfo could not be found. Maybe the player is not registered properly?
		if(info == null) {
			response.status = 0;
			response.message = "Player Stamina could not be found. Maybe the player is not registered properly?";			
			return response;
		}
		
		DateTime lastRegen = DateTime.parse(info.lastRegenTime);
		
		double elapsedMinutes = GeneralUtility.GetElapsedMinutes(lastRegen);
		
		int overlap = (int) Math.floor(elapsedMinutes/References.StaminaSettings.RegenTimeInMinute);
		
		if(overlap <= 0) {
			response.status = 0;
			response.message = "It is not the time yet to have any stamina regeneration.";			
			return response;
		}
		
		DateTime latestRegen = lastRegen.plusMinutes(References.StaminaSettings.RegenTimeInMinute * overlap); 
		
		Bson filter_pid = Filters.eq("pid", pid);
		Bson update_currency = Updates.inc("stamina",  References.StaminaSettings.RegenAmount * overlap); 
		Bson update_lastRegen= Updates.set("lastRegenTime", latestRegen.toString());
		
		MongoCollection<Document> staminaCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Stamina);
		UpdateResult result = staminaCollection.updateOne(filter_pid, Updates.combine(update_currency, update_lastRegen));
			
		if(result.getModifiedCount() >= 1) {
			response.status = 1;
			response.message = "Stamina regenerated.";
			
			SendStaminaUpdateToUserByPID(pid);		
			
			return response;
		}else {
			response.status = 0;
			response.message = "No errors and no changes.";
			
			return response;
		}
	}
	
	public StaminaInfo GetUserStaminaFromDatabase(String pid){
    	
    	MongoCollection<Document> resources = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Stamina);    
    	
    	Bson filter = new Document("pid", pid);
    	Document doc = resources.find(filter).limit(1).first();
    	
    	if(doc == null) return null;
    	
    	StaminaInfo info = GeneralUtility.ConvertFromDocument(doc, StaminaInfo.class);
    	
//    	GeneralUtility.GetLog().log(Level.INFO, GeneralUtility.getGson().toJson(info));
    	    	
    	return info;
    }
	
	private boolean IsInitializedForPlayer(String pid) {
    	MongoCollection<Document> resources = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Stamina);    
    	Bson filter = new Document("pid", pid);    	
    	long result = resources.countDocuments(filter);
    	
    	if(result >= 1) return true;
    	else return false;
	}
	
	public BasicSmartFoxResponse GetPlayerStamina(String pid) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(pid == null || pid.isEmpty()) {
			response.status = 0;
			response.message = "Player id could not be empty.";
			
			return response;		
		}		
		
		
		if(player == null) {
			response.status = 0;
			response.message = "Player not found.";
			
			return response;
		}
		
		try {

			CheckRegen(pid);
			
			StaminaInfo result = GetUserStaminaFromDatabase(pid);
			
			if(result != null) {					
				response.status = 1;
				response.message = "";
				response.data = result;
			}else {
				response.status = 0;
				response.message = "Player stamina data could not be found. The player may not be registered properly.";
			}
			
		}catch(Exception e) {				
			response.status = 0;
			response.message = e.getMessage();
		}
				
		return response;
	}	
	
	// Used when starting a dungeon, or reimburse the stamina as void when something happened
	public BasicSmartFoxResponse AdjustStamina(String pid, int adjustment) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);
		
		if(player == null) {
			response.status = 0;
			response.message = "Player not found.";
			
			return response;
		}
		
		Bson filter_pid = Filters.eq("pid", pid);
		Bson update_stamina = Updates.inc("stamina", adjustment);
//		Bson update_lastRegen= Updates.set("lastRegenTime", GeneralUtility.GetCurrentTime().toString());
		
		UpdateOptions option = new UpdateOptions();
		option.upsert(true);
					
		MongoCollection<Document> staminaCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Stamina);
		UpdateResult result = staminaCollection.updateOne(filter_pid, update_stamina, option);
		
		if(result.getModifiedCount() >= 1) {
			response.status = 1;
			response.message = "";
			
			SendStaminaUpdateToUserByPID(pid);
			
		}else {
			response.status = 0;
			response.message = "No changes";
		}
		
		return response;
	}
}
