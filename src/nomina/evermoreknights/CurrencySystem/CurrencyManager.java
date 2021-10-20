package nomina.evermoreknights.CurrencySystem;

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
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import nomina.evermoreknights.SharedClass.BasicSmartFoxResponse;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.SharedClass.TransactionReceipt;

public class CurrencyManager {

	private static CurrencyManager instance;
	
	public static CurrencyManager Instance() {
		if (instance == null) {
			synchronized (CurrencyManager.class) { // thread-safe
				if (instance == null) instance = new CurrencyManager();				
			}
		}
		
		return instance;
	}
	
	public List<CurrencyValue> GetUserCurrenciesFromDatabase(String pid){
    	    	
    	MongoCollection<Document> resources = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency);    	
    	
    	Bson filter = new Document("pid", pid);
    	Document doc = resources.find(filter).limit(1).first();
    	
    	if(doc == null) return null;
    	if(!doc.containsKey("currencies")) return null;
    	
    	Object docObject = doc.get("currencies");
    	List<CurrencyValue> docList = GeneralUtility.ConvertDocumentElementsToList(docObject, CurrencyValue[].class);
    	
    	return docList;
    }
	
	private List<TransactionReceipt> GetUserTransactionReceiptsFromDatabase(String pid){
    	
    	MongoCollection<Document> resources = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency_Receipt);    	
    	
    	Bson filter = new Document("pid", pid);
    	Document doc = resources.find(filter).limit(1).first();
    	
    	if(doc == null) return null;
    	if(!doc.containsKey("receipts")) return null;
    	
    	Object docObject = doc.get("receipts");
    	List<TransactionReceipt> docList = GeneralUtility.ConvertDocumentElementsToList(docObject, TransactionReceipt[].class);
    	
    	return docList;
    }
	
	public BasicSmartFoxResponse DoTransaction(String pid, CurrencyValue value, String message) {
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(player!=null) {
			
			// PREPARING THE CLIENT SESSION
			MongoClient client = MongoDBManager.getInstance().getClient();
			ClientSession session = client.startSession();
			
			try {
				session.startTransaction(TransactionOptions.builder().writeConcern(WriteConcern.MAJORITY).build());
				
				Bson filter_pid = Filters.eq("pid", pid);
				Bson filter_currency = Filters.eq("currencies.type", value.type);
				Bson update_currency = Updates.inc("currencies.$.amount", value.amount);
							
				MongoCollection<Document> currencyCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency);
				UpdateResult result = currencyCollection.updateOne(session, Filters.and(filter_pid, filter_currency), update_currency);
				
				String transactionMessage = (result.getModifiedCount() >= 1)? "Transaction done." : "Transaction done, but doesn't make any changes.";
				
				// CREATE THE TRANSACTION RECEIPT.
				TransactionReceipt receipt = new TransactionReceipt();
				receipt.id = new ObjectId().toHexString();
				receipt.status = 1;
				receipt.message = (!message.isEmpty() && message != null)? message : transactionMessage;
				receipt.sender = "System";
				receipt.receiver = pid;
				receipt.type = value.type;
				receipt.amount = value.amount;
				receipt.iat = GeneralUtility.GetCurrentTime().toString();
				
				// INSERT THE RECEIPT TO DATABASE
				MongoCollection<Document> receiptCollection =  MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency_Receipt);
				Bson update_receipt = Updates.push("receipts", GeneralUtility.ConvertToDocument(receipt));
				UpdateOptions options = new UpdateOptions();
				options.upsert(true);
				receiptCollection.updateOne(session, filter_pid, update_receipt, options);
				
				if(result.getModifiedCount() >= 1) {					
					response.status = 1;
					response.message = transactionMessage;
					response.data = GeneralUtility.ConvertToSFSObject(receipt);
					
					session.commitTransaction();
				}else {
					session.abortTransaction();
					
					response.status = 0;
					response.message = "Transaction Failed";
				}
			}catch(Exception e) {
				session.abortTransaction();
				
				response.status = 0;
				response.message = e.getMessage();
			}
			
		}else {			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
		}
		return response;		
	}

	public BasicSmartFoxResponse GetPlayerCurrencyReceipts(String pid) {
		
	BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		if(pid == null || pid.isEmpty()) {
			response.status = 0;
			response.message = "Player id could not be empty.";
		}
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(player!=null) {
			
			try {

				List<TransactionReceipt> result = CurrencyManager.Instance().GetUserTransactionReceiptsFromDatabase(pid);
				
				if(result != null) {					
					response.status = 1;
					response.message = "Success.";
					response.data = GeneralUtility.ConvertToSFSObject(result);
				}else {
					response.status = 0;
					response.message = "Player receipts could not be found. The player may not have any transaction yet.";
				}
				
			}catch(Exception e) {				
				response.status = 0;
				response.message = e.getMessage();
			}
			
		}else {		
			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
		}		
				
		return response;
	}
	
	public BasicSmartFoxResponse GetPlayerCurrency(String pid) {
		
		BasicSmartFoxResponse response = new BasicSmartFoxResponse();
		
		if(pid == null || pid.isEmpty()) {
			response.status = 0;
			response.message = "Player id could not be empty.";
		}
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(player!=null) {
			
			try {

				List<CurrencyValue> result = CurrencyManager.Instance().GetUserCurrenciesFromDatabase(pid);
				
				if(result != null) {					
					response.status = 1;
					response.message = "Success.";
					response.data = GeneralUtility.ConvertToSFSObject(result);
				}else {
					response.status = 0;
					response.message = "Player resources could not be found. The player may not be registered properly.";
				}
				
			}catch(Exception e) {				
				response.status = 0;
				response.message = e.getMessage();
			}
			
		}else {		
			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
		}		
				
		return response;
	}
}
