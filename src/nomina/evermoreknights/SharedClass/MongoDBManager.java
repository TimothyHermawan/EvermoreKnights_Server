package nomina.evermoreknights.SharedClass;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.Arrays;
import java.util.logging.Level;

import org.bson.Document;



public class MongoDBManager {

    private static MongoDBManager mongoDbManager = null;
    private MongoClient mongoClient = null;
    private static MongoDatabase database = null;
    private static SFSExtension ext;
    
    private static String SECRET = "EK200920211200WIB_V1";

	private MongoDBManager() {
        if (mongoClient != null) return;
        	
    	String user= References.DatabaseSettings.Username;
    	String databaseName= References.DatabaseSettings.MongoDB_Database; 
    	char[] password= References.DatabaseSettings.Password.toCharArray();
    
    	MongoCredential credential = MongoCredential.createCredential(user, databaseName, password);
    	
    	MongoClientSettings settings = MongoClientSettings.builder()
    	            .credential(credential)
    	            .applyToSslSettings(builder -> builder.enabled(false))
    	            .applyToClusterSettings(builder -> builder.hosts(Arrays.asList(new ServerAddress(References.DatabaseSettings.Host, References.DatabaseSettings.Port))))
    	            .build();        	 
    	        	
    	mongoClient = MongoClients.create(settings);

        database = mongoClient.getDatabase(databaseName);   
        
        GeneralUtility.GetLog().log(Level.INFO, " === MongoDB is initialized properly ===");
    }
   
    public static void initializeConnection (SFSExtension extension) {
        ext = extension;
        if (mongoDbManager == null) {
            mongoDbManager = new MongoDBManager();
        }
    }

    public static MongoDBManager getInstance() {
        return mongoDbManager;
    }

    public MongoClient getClient() {
        return mongoClient;
    }
   
    public MongoDatabase getDBManager() {
        return database;
    }

    public void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
        ext.trace("====closeConnection()===");
    }
    
    public void VerifyToken(String token) throws Exception {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("token", token)).limit(1).first();
    	
    	if(user==null) {    		
    		throw new Exception("Invalid Token");    		
    	}
    }
    
    public void VerifySecret(String secret) throws Exception {
    	if(!secret.contentEquals(SECRET)) throw new Exception("Invalid Key. Did you try to hack us?!");    	
    }
    
    public PlayerData GetPlayerDataByPID(String pid) {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("pid", pid)).limit(1).first();
    	
    	if(user!=null) {    		
    		PlayerData player = GeneralUtility.ConvertFromDocument(user, PlayerData.class);
    		return player;  
    	}
    	else return null;
    }
    
    public PlayerData GetPlayerDataByToken(String token) {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("token", token)).limit(1).first();
    	
    	if(user!=null) {    		
    		PlayerData player = GeneralUtility.ConvertFromDocument(user, PlayerData.class);
    		return player;  
    	}
    	else return null;
    }
    
    public String GetPIDByToken(String token) {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("token", token)).limit(1).first();
    	
    	if(user!=null) {    		
    		PlayerData player = GeneralUtility.ConvertFromDocument(user, PlayerData.class);
    		return player.pid;  
    	}
    	else return null;
    }
    
    public PlayerData GetPlayerDataByUsername(String username) {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("username", username)).limit(1).first();
    	
    	
    	
    	if(user!=null) {    		
    		PlayerData player = GeneralUtility.ConvertFromDocument(user, PlayerData.class);
//    		GeneralUtility.GetLog().log(Level.INFO, user.toJson());
    		return player;  
    	}
    	else {
//    		GeneralUtility.GetLog().log(Level.INFO, "Player not found.");
    		return null;
    	}
    }
    
    public PlayerData GetPlayerDataByEmail(String email) {
    	MongoCollection<Document> users = database.getCollection(References.DatabaseCollection.Players);
    	Document user = users.find(new Document("email", email)).limit(1).first();   	
    	
    	if(user!=null) {    		
    		PlayerData player = GeneralUtility.ConvertFromDocument(user, PlayerData.class);
//    		GeneralUtility.GetLog().log(Level.INFO, user.toJson());
    		return player;  
    	}
    	else {
//    		GeneralUtility.GetLog().log(Level.INFO, "Player not found.");
    		return null;
    	}
    }
    
    
    
}