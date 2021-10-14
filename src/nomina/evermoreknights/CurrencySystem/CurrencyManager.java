package nomina.evermoreknights.CurrencySystem;

import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;

import nomina.evermoreknights.SharedClass.GeneralUtility;
import nomina.evermoreknights.SharedClass.MongoDBManager;
import nomina.evermoreknights.SharedClass.References;
import nomina.evermoreknights.servlet.sharedclass.TransactionReceipt;

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
	
	public List<TransactionReceipt> GetUserTransactionReceiptsFromDatabase(String pid){
    	
    	MongoCollection<Document> resources = MongoDBManager.getInstance().getDBManager().getCollection(References.DatabaseCollection.Currency_Receipt);    	
    	
    	Bson filter = new Document("pid", pid);
    	Document doc = resources.find(filter).limit(1).first();
    	
    	if(doc == null) return null;
    	if(!doc.containsKey("receipts")) return null;
    	
    	Object docObject = doc.get("receipts");
    	List<TransactionReceipt> docList = GeneralUtility.ConvertDocumentElementsToList(docObject, TransactionReceipt[].class);
    	
    	return docList;
    }
	
	
	
}
