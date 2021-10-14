package nomina.evermoreknights.SharedClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;


import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import com.google.gson.Gson;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

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
	
//	public static Object DeepCopyJSON(Object object) {
//	   try {
//	    String json = gson.toJson(object);
//	    
//	    Object data = new Object();
//	    data = gson.fromJson(json, Object.class);
//	    
//	    return data;
//	    
//	   }
//	   catch (Exception e) {
//	     e.printStackTrace();
//	     return null;
//	   }
//	 }
	
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
	
//	public static <T> List<T> FilterWithPredicate(List<T> l, Predicate<T> p) 
//    { 
//        // using Predicate condition in lambda expression 
//        l = l.stream().filter(p).collect(Collectors.toList()); 
//  
//        // Return the list  
//        return l; 
//    } 
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