package nomina.evermoreknights.servlet.requesthandler;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoException;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import nomina.evermoreknights.CurrencySystem.CurrencyManager;
import nomina.evermoreknights.CurrencySystem.CurrencyValue;
import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.PlayerData;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.servlet.sharedclass.BasicServletResponse;
import nomina.evermoreknights.servlet.sharedclass.TransactionReceipt;

public class ServletHandler {
	
	private static ServletHandler instance;
	
	public static ServletHandler Instance() {
		if (instance == null) {
			synchronized (ServletHandler.class) { // thread-safe
				if (instance == null) instance = new ServletHandler();				
			}
		}
		
		return instance;
	}

	public BasicServletResponse Login(String username, String password) {
		
		BasicServletResponse response = new BasicServletResponse();
		
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
					response.data = "";
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
		response.data = "";
				
		return response;
	}

	public BasicServletResponse CurrencyTransaction(String pid, CurrencyValue value, String message) {
		
		BasicServletResponse response = new BasicServletResponse();
		
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
					response.data = receipt;
					
					session.commitTransaction();
				}else {
					session.abortTransaction();
					
					response.status = 0;
					response.message = "Transaction Failed";
					response.data = "";
				}
			}catch(Exception e) {
				session.abortTransaction();
				
				response.status = 0;
				response.message = e.getMessage();
				response.data = "";
			}
			
		}else {			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
			response.data = "";
		}		
				
		return response;
	}

	public BasicServletResponse GetPlayerCurrency(String pid) {
		
		BasicServletResponse response = new BasicServletResponse();
		
		if(pid == null || pid.isEmpty()) {
			response.status = 0;
			response.message = "Player id could not be empty.";
			response.data = "";
		}
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(player!=null) {
			
			try {

				List<CurrencyValue> result = CurrencyManager.Instance().GetUserCurrenciesFromDatabase(pid);
				
				if(result != null) {					
					response.status = 1;
					response.message = "Success.";
					response.data = result;
				}else {
					response.status = 0;
					response.message = "Player resources could not be found. The player may not be registered properly.";
					response.data = "";
				}
				
			}catch(Exception e) {				
				response.status = 0;
				response.message = e.getMessage();
				response.data = "";
			}
			
		}else {		
			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
			response.data = "";
		}		
				
		return response;
	}
	
	public BasicServletResponse GetPlayerCurrencyReceipts(String pid) {
		
		BasicServletResponse response = new BasicServletResponse();
		
		if(pid == null || pid.isEmpty()) {
			response.status = 0;
			response.message = "Player id could not be empty.";
			response.data = "";
		}
		
		PlayerData player = MongoDBManager.getInstance().GetPlayerDataByPID(pid);		
		
		if(player!=null) {
			
			try {

				List<TransactionReceipt> result = CurrencyManager.Instance().GetUserTransactionReceiptsFromDatabase(pid);
				
				if(result != null) {					
					response.status = 1;
					response.message = "Success.";
					response.data = result;
				}else {
					response.status = 0;
					response.message = "Player receipts could not be found. The player may not have any transaction yet.";
					response.data = "";
				}
				
			}catch(Exception e) {				
				response.status = 0;
				response.message = e.getMessage();
				response.data = "";
			}
			
		}else {		
			
			// PLAYER NOT FOUND
			response.status = 0;
			response.message = "Player not found.";
			response.data = "";
		}		
				
		return response;
	}



}
