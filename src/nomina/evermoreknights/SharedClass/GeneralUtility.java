package nomina.evermoreknights.SharedClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.managers.IUserManager;

public class GeneralUtility {

	private static Gson gson = new Gson();
	
	private static final Logger log = Logger.getGlobal();
	
	public static Logger GetLog() {
		return log;
	}
	
	public static Gson getGson() {
		return gson;
	}
	
	public static double GetElapsedSeconds(DateTime startTime) {
		double elapsedTime = 0;		
		DateTime now = GeneralUtility.GetCurrentTime();
		Duration duration = new Duration(now, startTime);
		elapsedTime = Math.abs(duration.getStandardSeconds());		
		return elapsedTime;
	}
	
	public static double GetElapsedMinutes(DateTime startTime) {
		double elapsedTime = 0;		
		DateTime now = GeneralUtility.GetCurrentTime();
		Duration duration = new Duration(now, startTime);
		elapsedTime = Math.abs(duration.getStandardMinutes());		
		return elapsedTime;
	}
	
//	public static String GetUTCNowString() {
//		DateTimeZone date = DateTimeZone.UTC;
//   		DateTime dt = new DateTime(date);
//   		String dtString = dt.toString(Globals.String_Format_Time_UTC);
//		return dtString;
//	}	
	
	public static DateTime ConvertDateToDateTime(long unixTimeMillis)
	{
	    DateTime epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeZone.UTC);
	    return epoch.plus(unixTimeMillis);
	}
	
	public static DateTime GetCurrentTime() {   		
		return DateTime.now(DateTimeZone.UTC).plusHours(7);
	}	
	
	public static DateTime DateTimeFromString(String dateTimeString) {   		
		return DateTime.parse(dateTimeString);
	}
	
	public static BasicSmartFoxResponse APILogin(String username, String password) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByUsername(username);		
		
		if(player!=null) {
			
			if(player.password.equals(password)) {
				
				// GENERATE NEW TOKEN, MOSTLY FOR WEB API, AND APLLY IT TO PLAYER IN RESPONSE
				String newToken = new ObjectId().toHexString();
				player.token = newToken;	
				player.lastLogin = GeneralUtility.GetCurrentTime().toString();
				
				// UPDATE TOKEN IN THE DB
				MongoCollection<Document> playerCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Players);
				Bson filter_pid = Filters.eq("pid", player.pid);
				Bson update_token = Updates.set("token", newToken);
				Bson update_lastLogin = Updates.set("lastLogin", player.lastLogin);
				UpdateResult result = playerCollection.updateOne(filter_pid, Updates.combine(update_token, update_lastLogin));		
				
				if(result.getModifiedCount() >= 1) {	
					// MAKE SURE WE DON'T SEND PASSWORD TO CLIENT
					player.password = "";
					
					// USERNAME AND PASSWORD MATCHED, TOKEN IS UPDATED.
					response.status = 1;
					response.message = "Login success and token is updated.";
					response.data = player;
				}else {
					// FAILED UPDATING TOKEN
					response.status = 0;
					response.message = "Failed updating token.";
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
				
		return response;
	}
	
	public static User GetUserByName(String name) {		
		User sfsUser = SmartFoxServer.getInstance().getUserManager().getUserByName(name);
		return sfsUser;
	}
	
	public static User GetUserByPID(String pid) {		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);
		
		if(player !=null) return GetUserByName(player.username);
		else return null;
	}
	
	
//	public static Object DeepCopy(Object object) {
//	   try {
//	     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//	     ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
//	     outputStrm.writeObject(object);
//	     ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//	     ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
//	     Object data = objInputStream.readObject();	     
//	     outputStream.close();
//	     outputStrm.close();
//	     inputStream.close();
//	     objInputStream.close();
//	     return data;
//	   }
//	   catch (Exception e) {
//	     e.printStackTrace();
//	     return null;
//	   }
//	 }
	
	public static <T> T DeepCopyJSON(Object object, Class<T> clazz) {
		String json = gson.toJson(object);
	    
	    return gson.fromJson(json, clazz);
	 }
	
	public static float RandomFloat(float min, float max) {
//		System.out.println(String.format("Min: %d, Max: %d", min, max));
		
		if(min == max) return min;
		if(min>max) return min;
		else {
			return (float)ThreadLocalRandom.current().nextDouble(min, max);
		}
	}
	
	public static int RandomInt(int min, int max) {
//		System.out.println(String.format("Min: %d, Max: %d", min, max));
		
		if(min == max) return min;
		if(min>max) return min;
		else {
			return ThreadLocalRandom.current().nextInt(min, max);
		}
	}
	
	
//	public static boolean IsVowel(String word) {		
//		word = word.toLowerCase();
//		
//		List<Character> vocals = Arrays.asList('a', 'i', 'u', 'e', 'o');
//		
//		char first = word.charAt(0);
//				
//		return vocals.contains(first);
//	}
	
//	public static User GetSFSUSer(String UserObjectIdHex) {
//		
//		MongoCollection<Document> usersCollection = MongoDBManager.getInstance().getDBManager().getCollection(Globals.Database_Collection_Users);	
//		
//		Bson filter = new Document("_id", new ObjectId(UserObjectIdHex));	
//		String name = (usersCollection.find(filter).limit(1).first()).getString("username");
//		
//		SmartFoxServer sfs = SmartFoxServer.getInstance();
//		IUserManager userManager = sfs.getUserManager();
//		
//		User sfsUser = userManager.getUserByName(name);
//		
//		return sfsUser;
//	}
	
//	public static String GetSFSUsername(String playerId) {
//		MongoCollection<Document> usersCollection = MongoDBManager.getInstance().getDBManager().getCollection(Globals.Database_Collection_Users);	
//		
//		Bson filter = new Document("_id", new ObjectId(playerId));	
//		
////		System.out.println(String.format("playerId: %s, usersColelction: %b", playerId, usersCollection!=null));
//		
//		Document userDoc = (usersCollection.find(filter).limit(1).first());
//		
//		if(userDoc == null) return "MISSING PLAYER";
//		
//		String name = userDoc.getString("username");
//				
//		return name;
//	}
	
	public static <T> List<T> FilterWithPredicate(List<T> l, Predicate<T> p) 
    { 
        // using Predicate condition in lambda expression 
        l = l.stream().filter(p).collect(Collectors.toList()); 
  
        // Return the list  
        return l; 
    } 
//	
//	public static Gson GetGson() {
//		return gson;
//	}
	
//	public static double GetClosest(double find, List<Double> availableSpellDuration) {
//		double closest = availableSpellDuration.get(0);
//		double distance = Math.abs(closest - find);
//	    for(double i: availableSpellDuration) {
//	    	double distanceI = Math.abs(i - find);
//	       if(distance > distanceI) {
//	           closest = i;
//	           distance = distanceI;
//	       }
//	    }
//	    return closest;
//	}
	
//	public static String GetTimeForOneSignal(double delay) {		
//		DateTime nextTime = GetUTCNow().plusSeconds((int)Math.ceil(delay));
//		
//		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");	
//		
//		String result = nextTime.toString(formatter);
//		
//		result += " UTC-0000";
//		
////		System.out.println("OneSignalTime: " + result);
//		
//		return result;		
//	}

    public static <T> T RandomElement(List<T> items)
    {
        // Return a random item.
        return items.get(RandomInt(0, items.size()));
    }
	
	public static <T> List<T> ConvertObjectToList(String s, Class<T[]> clazz) {
	    T[] arr = new Gson().fromJson(s, clazz);
	    return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
	}
	
	public static <T> List<T> ConvertDocumentElementsToList(Object o, Class<T[]> clazz) {
	    T[] arr = new Gson().fromJson(gson.toJson(o), clazz);
	    return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
	}
	
	public static void wait(int ms)
	{
	    try
	    {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex)
	    {
	        Thread.currentThread().interrupt();
	    }
	}
	
	public static double GenerateRandomDouble(double min, double max) {
		
		if(min == max) {
			return min;
		}
		
	    double leftLimit = min;
	    double rightLimit = max;
	    double generatedDouble = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit + 1);
	    return generatedDouble;
	}


	// region CONVERSION METHOD
//	public static DateTime ConvertStringToDateTime(String timeString) {
//		DateTime time = DateTime.parse(timeString, DateTimeFormat.forPattern(Globals.String_Format_Time_UTC));
//		
//		return time;		
//	}
	
//	public static String ConvertDateTimeToString(DateTime dt) {			
//		return dt.toString(Globals.String_Format_Time_UTC);
//	}
	
	public static Document ConvertToDocument(Object rawDocument) {	
		
	    if (rawDocument instanceof Document) return (Document)rawDocument;
	    
	    String json = gson.toJson(rawDocument);
	    Document doc = Document.parse(json); 
	    return doc;
	}
	
	public static List<Document> ConvertToListDocument(List<?> raw) {	
		
		List<Document> docs = new ArrayList<>();
//		List<Object> datas = (List<Object>) raw;
		
		for(Object data : raw) {
			
			Document doc = ConvertToDocument(data);
			docs.add(doc);
		}		
		
		return docs;
	}
	
	public static <T> T ConvertFromDocument(Document doc, Class<T> clazz) {
	    String json = doc.toJson();	    
	    return gson.fromJson(json, clazz);
	}
	
	public static <T> T ConvertFromSFSObject(SFSObject data, Class<T> clazz) {
	    String json = data.toJson();
	    return gson.fromJson(json, clazz);
	}
	
	public static SFSObject ConvertToSFSObject(Object data) {
		return (SFSObject) SFSObject.newFromJsonData(gson.toJson(data));
	}
		
	public static SFSArray ConvertListToSFSArray(List<?> raw) {
		SFSArray array = new SFSArray();		
		
		for(Object data : raw) array.addSFSObject(ConvertToSFSObject(data));		
		
		return array;
	}
	
//	public static <T> List<T> ConvertSFSArrayList (SFSArray array) {			
//		
//		List<Class<?>> result = new ArrayList<>();
//		
//		Class<T> classN ;
//		
//		for (int i = 0; i < array.size(); i++) {
//			SFSObject data = (SFSObject) array.getSFSObject(i);
//			T element = (T) ConvertFromSFSObject(data, classN);
//			result.add(element);
//		}
//		
//		return array;
//	}
	// endregion
			
	// region SEND EXTENSION RESPONSE TO PLAYER
//	public static void SendTroopUpdateToUser(User user) {		
//		
//		SmartFoxServer sfs = SmartFoxServer.getInstance();
//		
//		if(user != null && sfs.getUserManager().containsUser(user)) {
//			String attackerUid = user.getVariable("uid").getStringValue();
//			
//			List<TroopData> attackerTroopsCollection = MongoDBManager.getInstance().GetUserTroopsCollection(attackerUid);
//			List<OutTroopsData> outTroops = MongoDBManager.getInstance().GetUserOutTroopsCollection(attackerUid);
//			
//			SFSObject data1 = new SFSObject();
//			data1.putSFSArray("troops", ConvertListToSFSArray(attackerTroopsCollection));
//			data1.putSFSArray("outTroops", ConvertListToSFSArray(outTroops));
//			
//			BasicSmartFoxResponse attackerUpdate = new BasicSmartFoxResponse();
//			attackerUpdate.st = 1;
//			attackerUpdate.data = data1;
//			
//			sfs.getExtensionManager().getZoneExtension(user.getZone()).send(CMD.TroopsUpdate, attackerUpdate.ToSFSObject(), user);
//		}
//		
//		
//	}
	// endregion
	
}
